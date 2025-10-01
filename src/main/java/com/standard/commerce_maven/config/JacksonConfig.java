package com.standard.commerce_maven.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.core.jackson.SwaggerModule;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.featuresToDisable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS,
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.modules(new Hibernate5JakartaModule(), new JavaTimeModule(),  new SwaggerModule());
        };
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        // The builder is already customized by the jacksonCustomizer bean.
        // We just need to build it.
        return builder.build();
    }
}