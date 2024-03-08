package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;

@Builder
public record ProductAwaitingApprovalResponseDto(UUID publicId, String productName, String productCategoryName,
        UUID productCategoryPublicId, String manufacturerName, UUID manufacturerPublicId, String brandName,
        UUID brandPublicId, String measurementUnit, List<ProductListing> productListings, String defaultImageUrl,
        String productDescription, String productHighlights,
        String warrantyDuration, String warrantyCover, UUID warrantyTypePublicId, String warrantyTypeName,
        String warrantyAddress, String productCountry, String approvalStatus,
        String status, BigInteger version, LocalDateTime createdDate, LocalDateTime lastModifiedDate, String createdBy,
        String lastModifiedBy) {
}
