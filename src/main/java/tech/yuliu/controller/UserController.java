package tech.yuliu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yuliu.service.UserService;
import tech.yuliu.utils.ResultJson;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/login/{code}")
    public ResultJson login(@PathVariable String code) {
        log.info(String.format("接受参数，code: %s", code));
        return userService.codeToSession(code);
    }
}
