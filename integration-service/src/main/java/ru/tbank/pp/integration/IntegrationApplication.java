package ru.tbank.pp.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import ru.tbank.config.KafkaConfig;

@EnableKafka
@SpringBootApplication
@Import(KafkaConfig.class)
@ConfigurationPropertiesScan
public class IntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }
}
