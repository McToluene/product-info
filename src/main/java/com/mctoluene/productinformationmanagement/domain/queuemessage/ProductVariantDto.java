package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {

    private String manufacturerName;

    private String brandName;

    private String productName;

    private String productCategoryName;

    private String variantTypeName;

    private String variantName;

    private BigDecimal costPrice;

    private String createdBy;

    private String productVariantDetails;

    private UUID countryId;

    private Double weight;

    private String measurementUnit;

    private Boolean vated;

    private BigDecimal vatValue;

}
