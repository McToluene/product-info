package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.response.CustomPageImpl;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.internal.LocationClientInternalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hibernate.boot.model.source.internal.hbm.Helper.getValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationCacheInterfaceServiceImpl implements LocationCacheInterfaceService {

    private final ObjectMapper mapper;
    private final LocationClientInternalService locationClientInternalService;

    @Override
    @Cacheable(value = "locations")
    @TimeToLive(unit = TimeUnit.HOURS)
    public List<CountryDto> getLocationCache() {

        log.info("Cached location country executed");
        return mapper.convertValue(locationClientInternalService.getAllCountriesLocation().getBody().getData(),
                new TypeReference<CustomPageImpl<CountryDto>>() {
                }).getContent();

    }

}
