package tech.yuliu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.yuliu.utils.AliasMapping;
import tech.yuliu.utils.ResultJson;

@RestController
@RequestMapping("/properties")
public class ExposeProperties {

    @Autowired
    AliasMapping aliasMapping;

    @GetMapping("/category")
    public ResultJson getCategory() {
        return ResultJson.ok().data("category", aliasMapping.getCategory().keySet());
    }

    @GetMapping("/categoryZh")
    public ResultJson getCategoryZh() {
        return ResultJson.ok().data("categoryZh", aliasMapping.getCategoryZh().keySet());
    }

    @GetMapping("/categoryMap")
    public ResultJson getCategoryMapping() {
        return ResultJson.ok().data("categoryMap", aliasMapping.getCategory());
    }

    @GetMapping("/categoryZhMap")
    public ResultJson getCategoryZhMapping() {
        return ResultJson.ok().data("categoryZhMap", aliasMapping.getCategoryZh());
    }
}
