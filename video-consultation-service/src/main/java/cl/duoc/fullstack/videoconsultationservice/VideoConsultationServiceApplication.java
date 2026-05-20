package cl.duoc.fullstack.videoconsultationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VideoConsultationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoConsultationServiceApplication.class, args);
    }
}