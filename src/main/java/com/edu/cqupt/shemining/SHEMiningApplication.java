package com.edu.cqupt.shemining;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.edu.cqupt.shemining.mapper")
public class SHEMiningApplication {
    public static void main(String[] args) {
        SpringApplication.run(SHEMiningApplication.class, args);
    }
}
