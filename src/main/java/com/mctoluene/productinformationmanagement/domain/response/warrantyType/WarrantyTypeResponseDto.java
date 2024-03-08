package com.mctoluene.productinformationmanagement.domain.response.warrantyType;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record WarrantyTypeResponseDto(UUID publicId, String warrantyTypeName, String description,
        LocalDateTime createdDate, LocalDateTime lastModifiedDate,
        String createdBy, String lastModifiedBy,
        String status, BigInteger version) {
}
