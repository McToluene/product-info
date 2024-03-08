package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.productinformationmanagement.domain.request.shoppingexperience.ShoppingExperienceCreateProductRequest;
import com.mctoluene.productinformationmanagement.domain.request.shoppingexperience.ShoppingExperienceUpdateProductRequest;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "shopping-experience-client", configuration = FeignClientConfig.class, url = "${shoppingexperience.service.url}")
public interface ShoppingExperienceClientInternalService {

    @PostMapping("/price-model/algolia")
    ResponseEntity<AppResponse> createAlgoliaRequest(
            @RequestBody List<ShoppingExperienceCreateProductRequest> shoppingExperienceCreateProductRequest);

    @PutMapping("/price-model/algolia")
    ResponseEntity<AppResponse> updateAlgoliaRequest(
            @RequestBody List<ShoppingExperienceUpdateProductRequest> shoppingExperienceCreateProductRequest);

    @PostMapping("/price-model/getBySkuList")
    ResponseEntity<AppResponse> getPriceModelBySkuList(@RequestBody List<String> listOfSku);
}
