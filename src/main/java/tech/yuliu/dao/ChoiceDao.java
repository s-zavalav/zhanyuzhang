package tech.yuliu.dao;

import org.springframework.stereotype.Repository;
import tech.yuliu.bean.Choice;

import java.util.List;

@Repository
public interface ChoiceDao {
    Choice selectChoiceByUserIdAndCategory(Integer userId, String category);

    Choice selectChoiceByChoiceId(Integer choiceId);

    Integer insertChoice(Choice choice);

    Integer deleteChoiceById(Integer id);

    Integer deleteChoiceByUserIdAndCategory(Integer userId, String category);

    List<Choice> selectAllSelectedByUserId(Integer userId);

    List<String> selectAllCategory();

    List<Choice> selectAllChoiceByUserId(Integer userId);

    Integer updateLearnById(Integer id, Integer learn, Integer review);

    Integer updateLearnByUserIdAndCategory(Integer userId, String category,Integer learn, Integer review);

    Integer selectLearningWordNumber(Integer userId);

    Integer selectReviewWordNumber(Integer userId);
}
