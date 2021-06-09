package tech.yuliu.service;

import tech.yuliu.utils.ResultJson;

public interface UserService {
    ResultJson codeToSession(String code);
}
