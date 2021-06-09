package tech.yuliu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yuliu.service.ScheduleService;
import tech.yuliu.utils.ResultJson;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;


    @GetMapping("unlearned/{userId}")
    public ResultJson getUnlearnedWord(@PathVariable Integer userId) {
        return scheduleService.getUnlearnedWord(userId);
    }

    @GetMapping("review/{userId}")
    public ResultJson getReviewWord(@PathVariable Integer userId) {
        return scheduleService.getReviewWord(userId);
    }
}
