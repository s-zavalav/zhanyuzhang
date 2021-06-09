package tech.yuliu.dao;

import org.springframework.stereotype.Repository;
import tech.yuliu.bean.Learn;

import java.util.List;

@Repository
public interface LearnDao {
    List<Learn> selectLearnedByUserId(Integer userId);

    Integer insertLearn(Learn learn);

    Integer insertManyLearn(List<Learn> learns);

    Learn selectLastLearnByUserIdAndWordId(Integer userId, Integer wordId);

    List<Learn> selectLastLearnedListByUserIdAndWordIdList(Integer userId, List<Integer> wordIdList);

    Boolean selectExistsLearnRecordByUserId(Integer userId);
}
