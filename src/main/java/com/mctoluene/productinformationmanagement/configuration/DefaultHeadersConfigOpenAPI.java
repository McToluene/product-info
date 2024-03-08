package com.mctoluene.productinformationmanagement.configuration;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultHeadersConfigOpenAPI {

    @Bean
    public OpenApiCustomiser openApiCustomizer() {
        return openApi -> openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(
                        operation -> operation.addParametersItem(
                                new HeaderParameter()
                                        .required(false)
                                        .name("x-country-code"))
                                .addParametersItem(
                                        new HeaderParameter()
                                                .required(true)
                                                .name("x-trace-id")));
    }
}
