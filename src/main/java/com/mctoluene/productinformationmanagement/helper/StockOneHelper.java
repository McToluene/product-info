package com.mctoluene.productinformationmanagement.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductSkuDetail;

public class StockOneHelper {

    private StockOneHelper() {
    }

    public static ProductSkuDetail buildProductSkuDetail(BigDecimal breath, BigDecimal height,
            BigDecimal weight, BigDecimal length,
            String skuName, String skuType, String sku,
            String defaultImageUrl, String measuringUnit,
            String skuCategoryName, String skuDescription,
            BigDecimal costPrice) {
        return ProductSkuDetail.builder()
                .breath(breath)
                .height(height)
                .weight(weight)
                .length(length)
                .skuName(skuName)
                .skuType(skuType)
                .sku(sku)
                .defaultImageUrl(defaultImageUrl)
                .measuringUnit(measuringUnit)
                .skuCategoryName(skuCategoryName)
                .skuDescription(skuDescription)
                .costPrice(costPrice)
                .build();
    }

    public static ProductRequestDto buildProductRequestDto(String warehouseName, BigDecimal breath, BigDecimal height,
            BigDecimal weight, BigDecimal length,
            String skuName, String skuType, String sku,
            String defaultImageUrl, String measuringUnit,
            String skuCategoryName, String skuDescription,
            BigDecimal costPrice) {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName(warehouseName);
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        ProductSkuDetail productSkuDetail = buildProductSkuDetail(breath, height, weight,
                length, skuName, skuType, sku, defaultImageUrl, measuringUnit, skuCategoryName,
                skuDescription, costPrice);

        productSkuDetails.add(productSkuDetail);
        productRequestDto.setSkuDetails(productSkuDetails);
        return productRequestDto;
    }
}
