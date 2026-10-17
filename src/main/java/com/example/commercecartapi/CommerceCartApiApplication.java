package com.example.commercecartapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CommerceCartApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceCartApiApplication.class, args);
    }

}
