package tech.yuliu.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.yuliu.service.ChoiceService;
import tech.yuliu.utils.ResultJson;

import java.util.Map;

@RestController
@RequestMapping("/choice")
@Slf4j
public class ChoiceController {

    @Autowired
    ChoiceService choiceService;

    @GetMapping("/{userId}")
    public ResultJson getAllOption(@PathVariable Integer userId) {
        log.info(String.format("接受参数，userId: %d", userId));
        return choiceService.getAllOption(userId);
    }

    @PostMapping("/{userId}/{category}")
    public ResultJson addOneChoice(@PathVariable Integer userId,
                                   @PathVariable String category,
                                   @RequestBody Map<String, Object> data) {
        Integer learn = (Integer) data.getOrDefault("learn", null);
        log.info(String.format("接受参数，userId: %d， category: %s， learn: %d", userId, category, learn));
        return choiceService.addOneChoice(userId, category, learn);
    }

    @DeleteMapping("/{userId}/{category}")
    public ResultJson deleteOneChoice(@PathVariable Integer userId,
                                      @PathVariable String category) {
        log.info(String.format("接受参数，userId: %d， category: %s", userId, category));
        return choiceService.deleteOneChoice(userId, category);
    }
}
