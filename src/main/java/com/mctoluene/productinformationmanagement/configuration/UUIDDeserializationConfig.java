package com.mctoluene.productinformationmanagement.configuration;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class UUIDDeserializationConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomUUIDDeserializer() {
        return builder -> builder.deserializerByType(UUID.class, new CustomUUIDDeserializer());
    }
}
