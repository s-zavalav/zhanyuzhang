package tech.yuliu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yuliu.dao.WordDao;
import tech.yuliu.utils.ResultJson;

@RestController
@RequestMapping({"/word"})
public class WordController {
    @Autowired
    WordDao wordDao;

    @GetMapping({"/first/{category}/{num}"})
    public ResultJson firstExtractWord(@PathVariable String category, @PathVariable Integer num) {
        return ResultJson.ok().data("words", this.wordDao.firstExtractWord(category, num));
    }
}
