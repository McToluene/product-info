package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "inventory-client", configuration = FeignClientConfig.class, url = "${inventory.service.url}")
public interface InventoryClientInternalService {

    @GetMapping("/warehouse/by-name")
    AppResponse getWarehouseByName(@RequestHeader("x-trace-id") String traceId,
            @RequestParam(value = "warehouseName") String warehouseName);

    @GetMapping("/warehouse/stockone-live-inventory")
    AppResponse getStockOneLiveInventoryProducts(@RequestHeader("x-trace-id") String traceId,
            @RequestParam(value = "stateId") UUID stateId,
            @RequestParam(value = "warehouseId") UUID warehouseId);

    @GetMapping("/inventory/virtual/filter")
    AppResponse filterVirtualStorageProducts(@RequestParam(value = "stateId") UUID stateId,
            @RequestParam(value = "cityId") UUID cityId,
            @RequestParam(value = "lgaId") UUID lgaId);

}
