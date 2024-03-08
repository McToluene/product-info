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
public class VariantMarkupCompleteResponseDto {

    private UUID publicId;

    private UUID variantTypeId;

    private String variantTypeName;

    private UUID productId;

    private String productName;

    private String brandName;

    private String variantName;

    private String productCategoryName;

    private UUID productCategoryPublicId;

    private String variantDescription;

    private String sku;

    private BigDecimal costPrice;

    private BigDecimal markup;

    private UUID categoryPublicId;

    private String status;

    private BigInteger version;

    private String createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;
}
