package cl.duoc.fullstack.notificationservice.config;

import cl.duoc.fullstack.notificationservice.model.Template;
import cl.duoc.fullstack.notificationservice.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TemplateRepository templateRepository;

    @Override
    public void run(String... args) {

        if (templateRepository.count() == 0) {

            templateRepository.save(
                    Template.builder()
                            .name("Appointment Reminder")
                            .content("Reminder: You have a medical appointment tomorrow.")
                            .build()
            );

            templateRepository.save(
                    Template.builder()
                            .name("Lab Result Ready")
                            .content("Your laboratory results are now available.")
                            .build()
            );

            templateRepository.save(
                    Template.builder()
                            .name("Payment Confirmation")
                            .content("Your payment has been processed successfully.")
                            .build()
            );
        }
    }
}
