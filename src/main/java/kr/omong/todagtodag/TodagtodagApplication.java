package kr.omong.todagtodag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TodagtodagApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodagtodagApplication.class, args);
    }

}
