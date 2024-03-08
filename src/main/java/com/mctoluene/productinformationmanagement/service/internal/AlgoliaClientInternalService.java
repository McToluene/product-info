package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "algolia-client", configuration = FeignClientConfig.class, url = "${algolia.service.url}")
public interface AlgoliaClientInternalService {

    @GetMapping("/algolia-product")
    ResponseEntity<AppResponse> searchProductByQuery(@RequestParam(value = "query", required = true) String query,
            @RequestParam(value = "stateId", required = false) UUID stateId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size);

    @DeleteMapping("/algolia-product/{objectID}")
    ResponseEntity<AppResponse> deleteProductInAlgolia(@PathVariable("objectID") String objectID);

}
