package tech.yuliu.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.yuliu.bean.Choice;
import tech.yuliu.bean.User;
import tech.yuliu.dao.ChoiceDao;
import tech.yuliu.dao.UserDao;
import tech.yuliu.service.ChoiceService;
import tech.yuliu.utils.AliasMapping;
import tech.yuliu.utils.ResultJson;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChoiceServiceImpl implements ChoiceService {

    @Autowired
    ChoiceDao choiceDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AliasMapping aliasMapping;

    @Value("${word.review-multiple}")
    private Integer REVIEWMULTIPLE;

    @Override
    public ResultJson addOneChoice(Integer userId, String category, Integer learn) {
        // 用户输入参数为空
        if (userId == null || learn == null || !StringUtils.hasLength(category)) {
            log.error(String.format("参数为空，userId: %s, category: %s, learn: %d", userId, category, learn));
            return ResultJson.parameterNotEntered();
        }
        // 用户输入类别是否存在
        Set<String> allCategory = aliasMapping.getCategory().keySet();
        Set<String> allCategoryZh = aliasMapping.getCategoryZh().keySet();
        // 如果用户输入是中文，那么转成英文
        if (allCategoryZh.contains(category)) {
            category = aliasMapping.getCategoryZh().getOrDefault(category, "");
        }
        if (!allCategory.contains(category) || learn < 10) {
            log.error(String.format("参数错误，userId: %s, category: %s, learn: %d", userId, category, learn));
            return ResultJson.parameterError();
        }
        User user = userDao.queryByUserId(userId);
        // 用户不存在
        if (user == null) {
            log.error(String.format("用户不存在，userId: %d", userId));
            return ResultJson.nonExists();
        }
        Choice choice = choiceDao.selectChoiceByUserIdAndCategory(userId, category);
        // 如果存在，且和在学的相等
        if (choice != null && choice.getCategory().equals(category)) {
            log.error(String.format("此choice已存在，userId: %s, category: %s, learn: %d", userId, category, learn));
            return ResultJson.alreadyExists();
        }
        Integer affect = 0;
        // 不存在才添加
        if (choice == null) {
            // 默认复习数量为学习数量的3倍
            Choice insertChoice = new Choice(null, userId, learn, REVIEWMULTIPLE * learn, category);
            log.info(String.format("插入choice: %s", insertChoice));
            affect = choiceDao.insertChoice(insertChoice);
        }
        log.info(String.format("userId: %d, 更新learning: %s", userId, category));
        affect += userDao.updateLearningById(userId, category);
        if (affect > 1) {
            return ResultJson.ok();
        } else {
            return ResultJson.fail();
        }
    }

    @Override
    public ResultJson deleteOneChoice(Integer userId, String category) {
        Set<String> allCategoryZh = aliasMapping.getCategoryZh().keySet();
        // 如果用户输入是中文，那么转成英文
        if (allCategoryZh.contains(category)) {
            category = aliasMapping.getCategoryZh().getOrDefault(category, "");
        }
        Choice choice = choiceDao.selectChoiceByUserIdAndCategory(userId, category);
        if (choice == null) {
            log.error(String.format("不存在的choice，userId: %d, category: %s", userId, category));
            return ResultJson.nonExists();
        }
        log.info(String.format("删除choice: %s", choice));
        Integer affect = choiceDao.deleteChoiceById(choice.getId());
        User user = userDao.queryByUserId(userId);
        // 如果是用户正在背诵的，那么置为空
        if (user != null && choice.getCategory().equals(user.getLearning())) {
            log.info(String.format("用户userId: %d, 设置learning: null", userId));
            userDao.updateLearningById(userId, null);
        }
        List<Choice> allChoice = choiceDao.selectAllChoiceByUserId(userId);
        // 如果还有在学的，则把第一个设置为学习中
        if (allChoice.size() > 0) {
            userDao.updateLearningById(userId, allChoice.get(0).getCategory());
        }
        if (affect > 0) {
            return ResultJson.ok();
        } else {
            return ResultJson.nonExists();
        }
    }

    @Override
    public ResultJson getAllChoiceByUserId(Integer userId) {
        List<Choice> choices = choiceDao.selectAllChoiceByUserId(userId);
        return ResultJson.ok().data("count", choices.size()).data("choices", choices);
    }

    @Override
    public ResultJson getAllCategory() {
        List<String> allCategory = choiceDao.selectAllCategory();
        return ResultJson.ok().data("count", allCategory.size()).data("category", allCategory);
    }

    @Override
    public ResultJson getAllOption(Integer userId) {
        // 用户输入参数为空
        if (userId == null) {
            log.error(String.format("参数为空，userId: %d", userId));
            return ResultJson.parameterNotEntered();
        }
        List<Choice> selectedChoice = choiceDao.selectAllChoiceByUserId(userId);
        log.info(String.format("用户userId: %d，获取所有choice: %s", userId, selectedChoice));
        List<String> selectedCategory = selectedChoice.stream().map(Choice::getCategory).collect(Collectors.toList());
        ArrayList<Map<String, Object>> resList = new ArrayList<>();
        User user = userDao.queryByUserId(userId);
        for (String category : choiceDao.selectAllCategory()) {
            String alias = aliasMapping.getCategory().getOrDefault(category, "");
            HashMap<String, Object> hashMap = new HashMap<String, Object>() {{
                put("category", category);
                put("alias", alias);
                put("selected", 0);
                put("selectedAlias", "未学习");
            }};
            if (selectedCategory.contains(category)) {
                if (category.equals(user.getLearning())) {
                    hashMap.put("selected", 2);
                    hashMap.put("selectedAlias", "学习中");
                } else {
                    hashMap.put("selected", 1);
                    hashMap.put("selectedAlias", "学习过");
                }
            }
            resList.add(hashMap);
        }
        return ResultJson.ok().data("count", resList.size()).data("option", resList);
    }
}
