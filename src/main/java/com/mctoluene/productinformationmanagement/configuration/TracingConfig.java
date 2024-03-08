package com.mctoluene.productinformationmanagement.configuration;

import brave.baggage.BaggageField;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    @Qualifier("traceId")
    public BaggageField traceIdTraceField() {
        return BaggageField.create("traceId");
    }

}
