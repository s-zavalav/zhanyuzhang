package tech.yuliu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SweetwordApplicationTests {

    @Test
    void contextLoads() {
        Integer threshold = 50;
        Double attenuation = 0.0575D;
        System.out.println(computeDeltaTime(threshold, attenuation));
    }

    private static Integer computeDeltaTime(Integer threshold, Double attenuation) {
        // 计算公式：deltaTime = (threshold/100)^(-1/attenuation)
        // 计算指数
        double power = -Math.pow(attenuation, -1);
        // 计算底数
        double base = threshold / 100D;
        // 计算时间间隔（分钟）
        double doubleDelta = Math.pow(base, power);
        // 将分钟转换到秒
        return (int) doubleDelta * 60;
    }

}
