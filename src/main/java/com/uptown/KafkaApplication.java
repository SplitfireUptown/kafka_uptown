package com.uptown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 *
 * @Author lixiaofei
 * @create 2020/10/22 3:20 下午
 */
@SpringBootApplication
@EnableAsync
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
        System.out.println("KafkaApplication Running");
    }

}
