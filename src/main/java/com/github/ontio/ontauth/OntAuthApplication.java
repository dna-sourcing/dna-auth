package com.github.ontio.ontauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@tk.mybatis.spring.annotation.MapperScan("com.github.ontio.ontauth.mapper")
public class OntAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OntAuthApplication.class, args);
    }

}
