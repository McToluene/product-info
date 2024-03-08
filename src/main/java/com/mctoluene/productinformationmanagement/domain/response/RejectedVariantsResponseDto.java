package com.mctoluene.productinformationmanagement.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectedVariantsResponseDto {

    private UUID id;

    private UUID publicID;

    private UUID variantTypeId;

    private String variantTypeName;

    private String variantTypeDescription;

    private String variantTypeStatus;

    private String variantName;

    private String defaultImageUrl;

    private String variantDescription;

    private String sku;

    private BigDecimal costPrice;

    private Integer leadTime;

    private Integer threshold;

    private String createdBy;

    private String completedBy;

    private LocalDateTime completedDate;

    private String status;

    private String approvalStatus;

    private BigInteger version;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;

    private UUID countryId;

    private String rejectedReason;

    private Double weight;
}
