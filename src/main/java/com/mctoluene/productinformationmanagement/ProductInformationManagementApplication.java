package com.mctoluene.productinformationmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableCaching
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Product Information Management", version = "1.0", description = "Product Information Management APIS"))
public class ProductInformationManagementApplication implements CommandLineRunner {

    @Autowired
    private LocationCacheInterfaceService locationCacheInterfaceService;

    private ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(ProductInformationManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        locationCacheInterfaceService.getLocationCache();
    }

}
