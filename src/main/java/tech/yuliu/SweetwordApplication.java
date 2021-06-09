package tech.yuliu;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("tech.yuliu.dao")
@SpringBootApplication
public class SweetwordApplication {

    public static void main(String[] args) {
        SpringApplication.run(SweetwordApplication.class, args);
    }

}
