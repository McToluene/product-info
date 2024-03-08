package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ArchivedProductResponseDto(UUID publicId, BigInteger version, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, String createdBy, String lastModifiedBy,
        String status) {
}
