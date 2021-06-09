package tech.yuliu.service;

import tech.yuliu.bean.Learn;
import tech.yuliu.bean.ReceiveLearn;
import tech.yuliu.utils.ResultJson;

import java.util.List;

public interface LearnService {
    /**
     * @description: 往learn中插入一个学习记录
     * @param: [learn]
     * @return: tech.yuliu.utils.ResultJson
     * @author Yuliu
     * @date: 2022/4/10 14:54
     */
    ResultJson insertLearnedWord(ReceiveLearn receiveLearn);

    /**
     * @description: 往learn中插入多条学习记录
     * @param: [words]
     * @return: tech.yuliu.utils.ResultJson
     * @author Yuliu
     * @date: 2022/4/10 14:54
     */
    ResultJson insertManyLearnedWord(List<ReceiveLearn> ReceiveLearns);
}
