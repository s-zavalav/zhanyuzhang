package tech.yuliu.service;

import tech.yuliu.utils.ResultJson;

public interface ScheduleService {
    /**
     * @description: 获取当前用户规划复习的单词
     * @param: [userId]
     * @return: tech.yuliu.utils.ResultJson
     * @author Yuliu
     * @date: 2022/4/12 15:34
     */
    ResultJson getReviewWord(Integer userId);

    /**
     * @description: 获得当前用户未学过的新词
     * @param: [userId]
     * @return: tech.yuliu.utils.ResultJson
     * @author Yuliu
     * @date: 2022/4/12 15:32
     */
    ResultJson getUnlearnedWord(Integer userId);
}
