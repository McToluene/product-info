package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.*;

@Builder
public record ProductVersionResponseDto(UUID id, String productName, Product product, Brand brand,
        Manufacturer manufacturer,
        ProductCategory productCategory, String measurementUnit, String productListing, String defaultImageUrl,
        String productDescription, String productHighlights, String warrantyDuration,
        String warrantyCover, WarrantyType warrantyType, String warrantyAddress, String productCountry,
        BigInteger version, String approvedBy, LocalDateTime approvedDate, String status) {
}
