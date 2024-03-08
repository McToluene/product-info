package com.mctoluene.productinformationmanagement.domain.response.productCategory;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductCategoryResponseDto(UUID publicId, String productCategoryName, String imageUrl, String description,
        String status, BigInteger version, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, String createdBy, String lastModifiedBy) {
}
