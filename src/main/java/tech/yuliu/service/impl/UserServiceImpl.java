package tech.yuliu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.yuliu.bean.User;
import tech.yuliu.dao.UserDao;
import tech.yuliu.service.UserService;
import tech.yuliu.utils.ResultJson;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Value("${wechat.appid}")
    private String appid;
    @Value("${wechat.appSecret}")
    private String appSecret;

    @Override
    public ResultJson codeToSession(String code) {
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", appid, appSecret, code);
        JSONObject res = JSON.parseObject(Forest.get(url).executeAsString());
        Integer errcode = res.getInteger("errcode");
        // 如果有errcode，说明出错了，返回错误代码
        if (errcode != null) {
            return ResultJson.ok().data("errcode", errcode).data("errmsg", res.getString("errmsg"));
        }
        // 没出错
        String openid = res.getString("openid");
        String sessionKey = res.getString("session_key");
        User user = new User(null, openid, sessionKey, null);
        User localUser = userDao.queryByOpenid(openid);
        // 如果localUser为null，说明数据库中不存在，那么写入
        if (localUser == null) {
            userDao.insertUser(user);
            localUser = userDao.queryByOpenid(openid);
        } else {
            // 存在则更新
            user.setLearning(localUser.getLearning());
            userDao.updateUserById(user);
        }
        return ResultJson.ok().data("userId", localUser.getId()).data("learning", localUser.getLearning());
    }
}
