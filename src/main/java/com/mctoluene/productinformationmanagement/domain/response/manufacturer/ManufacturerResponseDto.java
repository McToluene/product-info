package com.mctoluene.productinformationmanagement.domain.response.manufacturer;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ManufacturerResponseDto(UUID publicId, String description, String manufacturerName,
        LocalDateTime createdDate,
        String createdBy, String lastModifiedBy, LocalDateTime lastModifiedDate,
        String status, BigInteger version) {
}
