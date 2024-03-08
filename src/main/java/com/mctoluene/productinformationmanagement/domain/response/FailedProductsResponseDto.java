package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record FailedProductsResponseDto(UUID publicId, String productCategoryName, String manufacturerName,
        String brandName, String measurementUnit, String productListing,
        String defaultImageUrl, String productDescription, String productHighlights,
        String warrantyDuration, String warrantyCover, String warrantyType, String warrantyAddress,
        String productCountry,
        String status, BigInteger version, LocalDateTime createdDate, LocalDateTime lastModifiedDate, String createdBy,
        String lastModifiedBy) {
}
