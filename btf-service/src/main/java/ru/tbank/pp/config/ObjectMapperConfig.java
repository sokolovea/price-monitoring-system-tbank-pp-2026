package ru.tbank.pp.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    ObjectMapper getObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//todo разобраться с датами
        return mapper;
    }
}
