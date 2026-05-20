package cl.duoc.fullstack.clinicalrecordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClinicalRecordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalRecordServiceApplication.class, args);
    }
}