package com.mctoluene.productinformationmanagement.domain.stockone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSkuDetail {
    private String skuType;
    private String skuName;
    private String sku;
    private BigDecimal costPrice;
    private String defaultImageUrl;
    private String measuringUnit;
    private BigDecimal height;
    private BigDecimal breath;
    private BigDecimal weight;
    private BigDecimal length;
    private String skuCategoryName;
    private String skuDescription;
}
