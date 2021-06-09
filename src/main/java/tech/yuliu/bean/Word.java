package tech.yuliu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {
    private Integer id;
    private String content, category, tran, pronUk, pronUs, sentence, sentenceCn;
}