package tech.yuliu.utils;


public interface ResultCode {
    Integer Fail = 0;
    Integer Success = 200;
    Integer ParameterNotEntered = 400;
    Integer ParameterError = 401;
    Integer IncorrectPermissions = 403;
    Integer InternalServiceError = 500;
    Integer AlreadyExists = 1000;
    Integer NonExistent = 1001;
}
