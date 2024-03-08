package com.mctoluene.productinformationmanagement.domain.response.variantType;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VariantTypeResponseDto(UUID publicId, String variantTypeName, String description,
        String status, BigInteger version, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, String createdBy, String lastModifiedBy) {
}
