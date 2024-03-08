package com.mctoluene.productinformationmanagement.service;

import java.util.List;

import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;

public interface LocationCacheInterfaceService {
    List<CountryDto> getLocationCache();

}
