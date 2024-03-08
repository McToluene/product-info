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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantCompleteResponseDto {
    private UUID publicId;
    private UUID variantTypeId;
    private String variantTypeName;
    private UUID productId;
    private String productName;
    private String brandName;
    private String measuringUnitName;
    private String measuringUnitAbbreviation;
    private String variantName;
    private String productCategoryName;
    private UUID countryPublicId;
    private UUID productCategoryPublicId;
    private String variantDescription;
    private String sku;
    private BigDecimal costPrice;
    private Integer leadTime;
    private Integer threshold;
    private String status;
    private BigInteger version;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
}
