package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;
import com.mctoluene.productinformationmanagement.model.WarrantyType;

public class WarrantyTypeHelper {
    private WarrantyTypeHelper() {
    }

    public static WarrantyType buildWarrantyTypeEntity(CreateWarrantyTypeRequestDto requestDto) {
        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName(requestDto.getWarrantyTypeName().trim())
                .description(requestDto.getDescription())
                .status(Status.ACTIVE)
                .build();
        warrantyType.setPublicId(UUID.randomUUID());
        warrantyType.setCreatedBy(requestDto.getCreatedBy());
        warrantyType.setLastModifiedDate(LocalDateTime.now());
        warrantyType.setLastModifiedBy(requestDto.getCreatedBy());
        warrantyType.setCreatedDate(LocalDateTime.now());
        warrantyType.setVersion(BigInteger.ZERO);
        return warrantyType;
    }

    public static WarrantyTypeResponseDto buildWarrantyTypeResponse(WarrantyType warrantyType) {
        return WarrantyTypeResponseDto.builder()
                .publicId(warrantyType.getPublicId())
                .warrantyTypeName(warrantyType.getWarrantyTypeName().trim())
                .description(warrantyType.getDescription())
                .createdDate(warrantyType.getCreatedDate())
                .createdBy(warrantyType.getCreatedBy())
                .lastModifiedBy(warrantyType.getLastModifiedBy())
                .lastModifiedDate(warrantyType.getLastModifiedDate())
                .status(warrantyType.getStatus().name())
                .version(warrantyType.getVersion())
                .build();
    }
}
