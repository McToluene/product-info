package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "stock-one", configuration = FeignClientConfig.class, url = "${stockone.service.url}")
public interface StockOneProductInternalService {

    @PostMapping("/products")
    ResponseEntity<AppResponse> createProduct(@RequestBody ProductRequestDto productRequestDto);

    @GetMapping("/products")
    AppResponse getProducts(@RequestHeader("x-trace-id") String traceId,
            @RequestParam("warehouseName") String warehouseName,
            @RequestParam(value = "skuCode") String skuCode,
            @RequestParam(value = "limit") Integer limit,
            @RequestParam(value = "pageNum") Integer pageNum);

}
