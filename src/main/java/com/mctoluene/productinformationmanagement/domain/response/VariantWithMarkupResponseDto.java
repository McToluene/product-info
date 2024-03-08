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
public class VariantWithMarkupResponseDto {

    private UUID publicId;

    private UUID variantTypeId;

    private UUID productPublicId;

    private String productName;

    private String variantName;

    private String variantDescription;

    private String sku;

    private BigDecimal costPrice;

    private String createdBy;

    private String status;

    private String approvalStatus;

    private BigInteger version;

    private BigDecimal markup;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;
}
