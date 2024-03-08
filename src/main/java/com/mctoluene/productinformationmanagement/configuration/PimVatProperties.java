package com.mctoluene.productinformationmanagement.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties("pim.vat.config")
@Configuration
@Data
public class PimVatProperties {
    private Map<String, CountryProperties> countries;
    private String url;
}
