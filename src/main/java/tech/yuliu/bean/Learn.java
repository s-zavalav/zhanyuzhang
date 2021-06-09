package tech.yuliu.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Learn {
    private Integer id, userId, wordId, count, time, memory;
    private Double attenuation;
}
