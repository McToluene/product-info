package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.productinformationmanagement.domain.response.VariantLocationResponseDto;
import com.mctoluene.productinformationmanagement.model.VariantLocation;

public class VariantLocationHelper {

    private VariantLocationHelper() {
    }

    public static VariantLocation buildVariantLocation(VariantLocationRequestdto variantLocationRequestdto) {
        VariantLocation variantLocation = VariantLocation.builder()
                .locationPublicId(variantLocationRequestdto.getStatePublicId())
                .variantPublicId(variantLocationRequestdto.getVariantPublicId())
                .build();
        variantLocation.setCreatedBy(variantLocationRequestdto.getLinkedBy());
        variantLocation.setPublicId(UUID.randomUUID());
        variantLocation.setLastModifiedBy(variantLocationRequestdto.getLinkedBy());
        variantLocation.setLastModifiedDate(LocalDateTime.now());
        variantLocation.setCreatedDate(LocalDateTime.now());
        variantLocation.setVersion(BigInteger.ZERO);
        variantLocation.setStatus(Status.ACTIVE.name());
        return variantLocation;
    }

    public static VariantLocationResponseDto buildResponseDto(VariantLocation variantLocation) {
        return VariantLocationResponseDto.builder()
                .statePublicId(variantLocation.getLocationPublicId())
                .status(variantLocation.getStatus())
                .variantPublicId(variantLocation.getVariantPublicId())
                .createdBy(variantLocation.getCreatedBy())
                .publicId(variantLocation.getPublicId())
                .version(variantLocation.getVersion())
                .lastModifiedDate(variantLocation.getLastModifiedDate())
                .build();
    }
}
