package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VariantLocationResponseDto(UUID publicId, UUID statePublicId, UUID variantPublicId, String createdBy,
        BigInteger version, LocalDateTime lastModifiedDate, String status) {
}
