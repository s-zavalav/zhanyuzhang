package tech.yuliu.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.yuliu.bean.User;
import tech.yuliu.bean.Word;
import tech.yuliu.dao.*;
import tech.yuliu.service.ScheduleService;
import tech.yuliu.utils.ResultJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    UserDao userDao;

    @Autowired
    ChoiceDao choiceDao;

    @Autowired
    WordDao wordDao;

    @Autowired
    LearnDao learnDao;

    @Autowired
    ScheduleDao scheduleDao;

    @Override
    public ResultJson getReviewWord(Integer userId) {
        User user = userDao.queryByUserId(userId);
        // 用户不存在，返回参数错误
        if (user == null) {
            log.error(String.format("参数错误，用户userId: %d不存在", userId));
            return ResultJson.parameterError();
        }
        String learning = user.getLearning();
        // 是否有正在学习的类别
        if (learning == null) {
            log.error(String.format("运行错误，用户userId: %d没有在学习的类别", userId));
            return ResultJson.nonExists();
        }
        // 保存最后返回的单词的list
        List<Word> wordList = new ArrayList<>();
        // 获得需要复习的单词数
        Integer reviewWordNumber = choiceDao.selectReviewWordNumber(userId);
        // 选取当前时间，记忆值在MLT以下的单词，设有n1个
        // 由于记忆曲线是单调的，超出lowThresholdTime即为记忆值在MLT以下的单词
        // 由于遗忘曲线是先快后慢，因此要选择排序后超出时间最多的
        List<Word> lowThresholdTimeList = scheduleDao.selectWordUnderLowThresholdTime(userId);
        int lowThresholdTimeListSize = lowThresholdTimeList.size();
        // 如果低于MLT的单词已经超过总的要复习的单词数，说明选词已经够了，直接返回
        if (lowThresholdTimeListSize > reviewWordNumber) {
            lowThresholdTimeList = lowThresholdTimeList.subList(0, reviewWordNumber);
            wordList.addAll(lowThresholdTimeList);
            return ResultJson.ok().data("count", lowThresholdTimeListSize).data("word", lowThresholdTimeList);
        }
        wordList.addAll(lowThresholdTimeList);
        // 还不够，继续从MLT < m < MT中选取
        List<Word> wordBetweenLowThresholdTimeAndThresholdTime = scheduleDao.selectWordBetweenLowThresholdTimeAndThresholdTime(userId);
        int wordBetweenLowThresholdTimeAndThresholdTimeSize = wordBetweenLowThresholdTimeAndThresholdTime.size();
        // 打乱顺序，抑制记忆
        Collections.shuffle(wordBetweenLowThresholdTimeAndThresholdTime);
        int nDiff = reviewWordNumber - lowThresholdTimeListSize;
        if (nDiff > 0 && wordBetweenLowThresholdTimeAndThresholdTimeSize > nDiff) {
            wordBetweenLowThresholdTimeAndThresholdTime = wordBetweenLowThresholdTimeAndThresholdTime.subList(0, nDiff);
            wordList.addAll(wordBetweenLowThresholdTimeAndThresholdTime);
            return ResultJson.ok().data("count", wordList.size()).data("word", wordList);
        }
        wordList.addAll(wordBetweenLowThresholdTimeAndThresholdTime);
        // 还有，继续从m > MT中选取
        List<Word> wordHigherThresholdTime = scheduleDao.selectWordHigherThresholdTime(userId);
        int wordHigherThresholdTimeSize = wordHigherThresholdTime.size();
        // 打乱顺序，抑制记忆
        Collections.shuffle(wordBetweenLowThresholdTimeAndThresholdTime);
        nDiff = reviewWordNumber - lowThresholdTimeListSize - wordHigherThresholdTimeSize;
        if (nDiff > 0 && wordHigherThresholdTimeSize > nDiff) {
            wordHigherThresholdTime = wordHigherThresholdTime.subList(0, nDiff);
            wordList.addAll(wordHigherThresholdTime);
            return ResultJson.ok().data("count", wordList.size()).data("word", wordList);
        }
        wordList.addAll(wordHigherThresholdTime);
        return ResultJson.ok().data("count", wordList.size()).data("word", wordList);
    }

    @Override
    public ResultJson getUnlearnedWord(Integer userId) {
        User user = userDao.queryByUserId(userId);
        // 用户不存在，返回参数错误
        if (user == null) {
            log.error(String.format("参数错误，用户userId: %d不存在", userId));
            return ResultJson.parameterError();
        }
        String learning = user.getLearning();
        // 是否有正在学习的类别
        if (learning == null) {
            log.error(String.format("运行错误，用户userId: %d没有在学习的类别", userId));
            return ResultJson.nonExists();
        }
        // 获得当前用户正在学习的类别的规划新词数
        Integer learningWordNumber = choiceDao.selectLearningWordNumber(userId);
        List<Word> unlearnedWordList = wordDao.extractUnlearnedWord(userId, learning, learningWordNumber);
        return ResultJson.ok().data("count", unlearnedWordList.size()).data("word", unlearnedWordList);
    }

    private List<Word> removeDuplicateByWordId(List<Word> wordList1, List<Word> wordList2) {
        List<Integer> wordIdList1 = wordList1.stream().map(Word::getId).collect(Collectors.toList());
        List<Word> removedWordList2 = wordList2.stream().filter(word -> !wordIdList1.contains(word.getId())).collect(Collectors.toList());
        wordList1.addAll(removedWordList2);
        return wordList1;
    }
}
