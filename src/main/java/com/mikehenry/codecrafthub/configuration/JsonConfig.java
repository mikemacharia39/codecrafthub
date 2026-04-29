package com.mikehenry.codecrafthub.configuration;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModules(new JavaTimeModule())
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
                .build();
    }
}
