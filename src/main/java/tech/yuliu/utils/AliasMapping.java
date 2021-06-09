package tech.yuliu.utils;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@ConfigurationProperties(prefix = "alias-mapping")
@Component
@Data
public class AliasMapping {
    private HashMap<String, String> category;
    private HashMap<String, String> categoryZh;

    public HashMap<String, String> getCategory() {
        return this.category;
    }

    public HashMap<String, String> getCategoryZh() {
        return this.categoryZh;
    }
}
