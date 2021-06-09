
package tech.yuliu.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResultJson {
    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap();

    private ResultJson() {
    }

    public static ResultJson ok() {
        ResultJson r = new ResultJson();
        r.setSuccess(true);
        r.setCode(ResultCode.Success);
        r.setMessage("成功");
        return r;
    }

    public static ResultJson fail() {
        ResultJson r = new ResultJson();
        r.setSuccess(false);
        r.setCode(ResultCode.Fail);
        r.setMessage("失败");
        return r;
    }

    public static ResultJson parameterError() {
        ResultJson r = new ResultJson();
        r.setSuccess(true);
        r.setCode(ResultCode.ParameterError);
        r.setMessage("参数错误");
        return r;
    }

    public static ResultJson parameterNotEntered() {
        ResultJson r = new ResultJson();
        r.setSuccess(true);
        r.setCode(ResultCode.ParameterError);
        r.setMessage("参数缺失");
        return r;
    }

    public static ResultJson alreadyExists() {
        ResultJson r = new ResultJson();
        r.setSuccess(true);
        r.setCode(ResultCode.AlreadyExists);
        r.setMessage("已存在");
        return r;
    }

    public static ResultJson nonExists() {
        ResultJson r = new ResultJson();
        r.setSuccess(true);
        r.setCode(ResultCode.NonExistent);
        r.setMessage("不存在");
        return r;
    }

    public ResultJson success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public ResultJson message(String message) {
        this.setMessage(message);
        return this;
    }

    public ResultJson code(Integer code) {
        this.setCode(code);
        return this;
    }

    public ResultJson data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public ResultJson data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
