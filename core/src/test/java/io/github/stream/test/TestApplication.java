package io.github.stream.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wendy512@yeah.net
 * @date 2023-05-22 15:38:51
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.github.stream")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
