package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ImageCatalogResponseDto(UUID productVariantPublicId, UUID publicId, String imageName, String imageUrl,
        String imageDescription,
        String status, BigInteger version, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, String createdBy, String lastModifiedBy) {
}
