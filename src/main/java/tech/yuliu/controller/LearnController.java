package tech.yuliu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yuliu.bean.ReceiveLearn;
import tech.yuliu.service.LearnService;
import tech.yuliu.utils.ResultJson;

import java.util.List;

@RestController
@Slf4j
public class LearnController {

    @Autowired
    LearnService learnService;

    @RequestMapping("/learn")
    public ResultJson insertLearn(@RequestBody List<ReceiveLearn> receiveLearns) {
        log.info(String.format("接受参数: %s", receiveLearns));
        return learnService.insertManyLearnedWord(receiveLearns);
    }
}
