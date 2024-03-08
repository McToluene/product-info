package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ImageResponseDto(UUID publicId, String imageName, String url, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, String createdBy,
        String lastModifiedBy, String status, BigInteger version) {
}
