package tech.yuliu.dao;

import org.springframework.stereotype.Repository;
import tech.yuliu.bean.User;

@Repository
public interface UserDao {
    Integer insertUser(User user);

    Integer updateUserById(User user);

    User queryByOpenid(String openid);

    User queryByUserId(Integer userId);

    Integer updateLearningById(Integer userId, String learning);
}
