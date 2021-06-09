package tech.yuliu.dao;

import org.springframework.stereotype.Repository;
import tech.yuliu.bean.Word;

import java.util.List;

@Repository
public interface WordDao {
    List<Word> firstExtractWord(String category, Integer wordNumber);

    List<Word> extractUnlearnedWord(Integer userId,String category,Integer wordNumber);

    List<Word> selectWordByIdList(List<Integer> wordIdList);
}
