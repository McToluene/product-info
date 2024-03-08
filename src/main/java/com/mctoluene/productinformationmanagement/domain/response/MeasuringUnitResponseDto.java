package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MeasuringUnitResponseDto(UUID publicId, String name, String description, String abbreviation,
        LocalDateTime createdDate,
        String status, LocalDateTime lastModifiedDate, String createdBy, String lastModifiedBy, BigInteger version) {

}
