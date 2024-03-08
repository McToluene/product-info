package com.mctoluene.productinformationmanagement.configuration.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.spel.spi.EvaluationContextExtension;
import org.springframework.stereotype.Component;

import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;

import java.util.*;

@Component
@RequiredArgsConstructor
public class HeaderEvaluation implements EvaluationContextExtension {

    private final LocationCacheInterfaceService locationCacheInterfaceService;

    @Override
    public String getExtensionId() {
        return "locale";
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>(EvaluationContextExtension.super.getProperties());

        var country = locationCacheInterfaceService.getLocationCache();

        if (Objects.isNull(RequestHeaderContextHolder.getContext().countryCode())) {
            properties.put("country", country.get(0).publicId());
            properties.put("countryZAR", country.get(1).publicId());
        } else {

            var countryInfo = country.stream().filter(
                    f -> f.threeLetterCode()
                            .equals(RequestHeaderContextHolder.getContext().countryCode().toUpperCase()))
                    .findFirst().get();

            properties.put("country", countryInfo.publicId());
            properties.put("countryZAR", countryInfo.publicId());
        }

        return properties;
    }

}
