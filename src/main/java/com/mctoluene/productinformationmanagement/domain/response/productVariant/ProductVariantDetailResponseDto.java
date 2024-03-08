package com.mctoluene.productinformationmanagement.domain.response.productVariant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDetailResponseDto {

    private UUID publicId;

    private UUID variantTypeId;
    private String variantName;
    private String variantTypeName;

    private Double weight;

    private String variantDescription;
    private String sku;
    private BigDecimal costPrice;
    private String createdBy;

    private UUID countryPublicId;
    private String status;
    private String approvalStatus;
    private BigInteger version;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private List<String> imageUrls;

    private UUID productPublicId;
    private String productName;

    private ProductResponseDto product;

    private ProductCategoryWithSubcategoryResponse parentProductCategory;

    private boolean isVated;

    private BigDecimal vatValue;

}
