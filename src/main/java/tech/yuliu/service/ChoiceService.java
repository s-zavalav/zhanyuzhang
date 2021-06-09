package tech.yuliu.service;

import tech.yuliu.utils.ResultJson;

public interface ChoiceService {
    ResultJson addOneChoice(Integer userId, String category, Integer learn);

    ResultJson deleteOneChoice(Integer userId, String category);

    ResultJson getAllChoiceByUserId(Integer userId);

    ResultJson getAllCategory();

    ResultJson getAllOption(Integer userId);
}
