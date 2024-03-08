package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponseDto;
import com.mctoluene.productinformationmanagement.model.VariantType;

public class VariantTypeHelper {

    private VariantTypeHelper() {
    }

    public static VariantTypeResponseDto buildVariantTypeResponseDto(VariantType variantType) {
        return VariantTypeResponseDto.builder()
                .publicId(variantType.getPublicId())
                .variantTypeName(variantType.getVariantTypeName().trim())
                .description(variantType.getDescription())
                .createdDate(variantType.getCreatedDate())
                .createdBy(variantType.getCreatedBy())
                .lastModifiedBy(variantType.getLastModifiedBy())
                .lastModifiedDate(variantType.getLastModifiedDate())
                .status(variantType.getStatus())
                .version(variantType.getVersion())
                .build();
    }

    public static VariantType buildVariantType(CreateVariantTypeRequestDto requestDto) {
        VariantType variantType = new VariantType();
        variantType.setPublicId(UUID.randomUUID());
        variantType.setVariantTypeName(requestDto.getVariantTypeName().toUpperCase().trim());
        variantType.setDescription(requestDto.getDescription());
        variantType.setStatus(Status.ACTIVE.name());
        variantType.setCreatedBy(requestDto.getCreatedBy());
        variantType.setLastModifiedBy(requestDto.getCreatedBy());
        variantType.setCreatedDate(LocalDateTime.now());
        variantType.setLastModifiedDate(LocalDateTime.now());
        variantType.setVersion(BigInteger.ZERO);
        return variantType;
    }

}
