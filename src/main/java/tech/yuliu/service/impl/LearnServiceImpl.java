package tech.yuliu.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.yuliu.bean.*;
import tech.yuliu.dao.LearnDao;
import tech.yuliu.dao.ScheduleDao;
import tech.yuliu.dao.UserDao;
import tech.yuliu.dao.WordDao;
import tech.yuliu.service.LearnService;
import tech.yuliu.utils.ResultJson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LearnServiceImpl implements LearnService {

    @Autowired
    private LearnDao learnDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private WordDao wordDao;

    @Value("${word.default-attenuation}")
    private Double DEFAULT_ATTENUATION;

    @Value("${word.min-attenuation}")
    private Double MIN_ATTENUATION;

    @Value("${word.max-memory}")
    private Integer MAX_MEMORY;

    @Value("${word.max-time}")
    private Integer MAX_TIME;

    @Value("${word.memory-threshold}")
    private Integer MEMORY_THRESHOLD;

    @Value("${word.memory-low-threshold}")
    private Integer MEMORY_LOW_THRESHOLD;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public ResultJson insertLearnedWord(ReceiveLearn receiveLearn) {
        Learn localLearn = learnDao.selectLastLearnByUserIdAndWordId(receiveLearn.getUserId(), receiveLearn.getWordId());
        Learn learn = new Learn(null, receiveLearn.getUserId(), receiveLearn.getWordId(), null, receiveLearn.getTime(), receiveLearn.getMemory(), null);
        // 如果不存在，则默认为1
        if (localLearn == null) {
            learn.setCount(1);
        } else {
            learn.setCount(localLearn.getCount() + 1);
        }
        Integer affect = learnDao.insertLearn(learn);
        if (affect > 0) {
            return ResultJson.ok();
        } else {
            return ResultJson.fail();
        }
    }

    @Override
    public ResultJson insertManyLearnedWord(List<ReceiveLearn> receiveLearns) {
        if (receiveLearns.size() <= 0) {
            log.error("参数错误, receiveLearns大小为0");
            return ResultJson.parameterError();
        }
        // 选择第一个记录的userId作为标准
        Integer firstUserId = receiveLearns.get(0).getUserId();
        User user = userDao.queryByUserId(firstUserId);
        // 是否存在这个用户
        if (user == null) {
            log.error(String.format("参数错误, 不存在的userId: %d", firstUserId));
            return ResultJson.parameterError();
        }
        // 存在user，获取正在学习的类别
        String learning = user.getLearning();
        // 是否选择了一个类别进行学习
        if (Strings.isBlank(learning)) {
            log.error(String.format("运行错误, 用户userId: %d, learning为空", firstUserId));
            return ResultJson.nonExists();
        }
        // 写入schedule的list
        ArrayList<Schedule> insertScheduleList = new ArrayList<>();
        // 写入learn的list
        ArrayList<Learn> insertLearnList = new ArrayList<>();
        // 获得全部前端传入的wordId，并通过id去word表中查询结果
        List<Integer> wordIdList = receiveLearns.stream().map(ReceiveLearn::getWordId).collect(Collectors.toList());
        List<Word> wordList = wordDao.selectWordByIdList(wordIdList);
        // 根据传入的wordList去learn中查询结果
        List<Learn> lastLearnedList = learnDao.selectLastLearnedListByUserIdAndWordIdList(firstUserId, wordIdList);
        for (ReceiveLearn receiveLearn : receiveLearns) {
            Integer userId = receiveLearn.getUserId();
            // 如果有后面的userId和第一个不一样的，则抛异常，事务回滚
            if (!Objects.equals(userId, firstUserId)) {
                log.error(String.format("参数错误, 存在不一致的userId, first: %d, current: %d", firstUserId, userId));
                return ResultJson.fail();
            }
            Integer wordId = receiveLearn.getWordId();
            boolean matchCategory = wordList.stream().anyMatch(word -> word.getCategory().equals(learning));
            if (!matchCategory) {
                log.error(String.format("参数错误，存在和learning不一致的category, userId: %d, wordId: %d", userId, wordId));
                return ResultJson.fail();
            }
            Integer memory = receiveLearn.getMemory();
            Integer rememberTimestamp = receiveLearn.getTime();
            // 从本地查找的记录中看，此单词有没有学过
            Learn localLearn = lastLearnedList.stream().filter(learn -> userId.equals(learn.getUserId()) && wordId.equals(learn.getWordId())).findAny().orElse(null);
            Learn learn = new Learn(null, userId, wordId, null, rememberTimestamp, memory, null);
            // 1.计算本次learn的count和attenuation
            // 如果不存在，则count默认为1，attenuation为default
            if (localLearn == null) {
                learn.setCount(1);
                learn.setAttenuation(DEFAULT_ATTENUATION);
            } else {
                // 如果存在，那么count为原值+1，attenuation为计算后的结果
                learn.setCount(localLearn.getCount() + 1);
                // 计算记忆衰减率
                learn.setAttenuation(computeAttenuation(memory, rememberTimestamp, localLearn.getTime()));
            }
            // 2.计算生成schedule对象
            Integer deltaTime = computeDeltaTime(MEMORY_THRESHOLD, learn.getAttenuation());
            Integer deltaLowTime = computeDeltaTime(MEMORY_LOW_THRESHOLD, learn.getAttenuation());
            // 防止超出最大时长
            if (deltaTime > MAX_TIME) {
                deltaTime = MAX_TIME;
                deltaLowTime = 0;
            }
            int nextThresholdTimestamp = rememberTimestamp + deltaTime;
            int nextLowThresholdTimestamp = rememberTimestamp + deltaLowTime;
            Integer count = learn.getCount();
            Schedule schedule = new Schedule(null, userId, wordId, count, rememberTimestamp, nextThresholdTimestamp, nextLowThresholdTimestamp);
            LocalDateTime rememberDate = LocalDateTime.ofEpochSecond(nextThresholdTimestamp, 0, ZoneOffset.ofHours(8));
            LocalDateTime nextMemoryDate = LocalDateTime.ofEpochSecond(nextThresholdTimestamp, 0, ZoneOffset.ofHours(8));
            LocalDateTime nextLowMemoryDate = LocalDateTime.ofEpochSecond(nextLowThresholdTimestamp, 0, ZoneOffset.ofHours(8));
            log.info(String.format("learn记录, userId: %d, wordId: %d, count: %d, rememberTimestamp: %s, memory: %d, attenuation: %f", userId, wordId, count, rememberDate.format(formatter), memory, learn.getAttenuation()));
            log.info(String.format("schedule记录, userId: %d, wordId: %d, count: %d,thresholdTime: %s, lowThresholdTime: %s", userId, wordId, count, nextMemoryDate.format(formatter), nextLowMemoryDate.format(formatter)));
            insertLearnList.add(learn);
            insertScheduleList.add(schedule);
        }
        Integer learnAffect = learnDao.insertManyLearn(insertLearnList);
        Integer scheduleAffect = scheduleDao.insertManySchedule(insertScheduleList);
        log.info(String.format("用户userId: %d, learnAffect: %d, scheduleAffect: %d", firstUserId, learnAffect, scheduleAffect));
        return ResultJson.ok();
    }


    private Double computeAttenuation(Integer memory, Integer memoryTime, Integer lastTime) {
        // 计算分子
        double numerator = 2.0D - Math.log10(memory);
        // 时间转换到分钟
        double memoryTimeByDouble = memoryTime / 60.0D;
        double lastTimeByDouble = lastTime / 60.0D;
        // 计算分母
        double denominator = Math.log10(memoryTimeByDouble - lastTimeByDouble);
        // 两者相除即为结果
        double res = numerator / denominator;
        // 保留三位小数
        BigDecimal bigDecimal = new BigDecimal(res);
        double value = bigDecimal.setScale(3, RoundingMode.HALF_UP).doubleValue();
        if (value < MIN_ATTENUATION) {
            value = MIN_ATTENUATION;
        }
        return value;
    }

    /**
     * @description: 计算距离下一次记忆的时间
     * @param: [threshold, attenuation]
     * @return: java.lang.Integer
     * @author Yuliu
     * @date: 2022/4/11 17:23
     */
    private Integer computeDeltaTime(Integer threshold, Double attenuation) {
        // 计算公式：deltaTime = (threshold/100)^(-1/attenuation)
        // 计算指数
        double power = -Math.pow(attenuation, -1);
        // 计算底数
        double base = threshold / 100D;
        // 计算时间间隔（分钟）
        double doubleDelta = Math.pow(base, power);
        // 将分钟转换到秒
        return (int) doubleDelta * 60;
    }
}
