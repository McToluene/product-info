package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.productinformationmanagement.domain.request.location.IdListDto;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "location-client", configuration = FeignClientConfig.class, url = "${location.service.url}")
public interface LocationClientInternalService {

    @GetMapping("/location/countries/{publicId}")
    ResponseEntity<AppResponse<CountryDto>> getCountryByPublicId(@PathVariable UUID publicId);

    @PostMapping("/location/countries/by-ids")
    ResponseEntity<AppResponse<List<CountryDto>>> findCountryByPublicIds(@RequestBody IdListDto data);

    @GetMapping("/location/countries/code/{code}")
    ResponseEntity<AppResponse<CountryDto>> findCountryByCode(@PathVariable String code);

    @GetMapping("/location/state-provinces/{publicId}")
    ResponseEntity<AppResponse> getStateProvinceByPublicId(@PathVariable UUID publicId);

    @GetMapping("/location/countries/")
    ResponseEntity<AppResponse> getAllCountriesLocation();
}
