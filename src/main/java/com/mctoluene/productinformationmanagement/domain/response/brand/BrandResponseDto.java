package com.mctoluene.productinformationmanagement.domain.response.brand;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.Brand;

@Builder
public record BrandResponseDto(UUID publicId, String brandName, String description,
        LocalDateTime createdDate, LocalDateTime lastModifiedDate,
        String createdBy, String lastModifiedBy,
        String status, BigInteger version, UUID manufacturerId) {
}
