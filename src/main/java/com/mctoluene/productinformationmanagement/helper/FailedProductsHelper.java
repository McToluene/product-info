package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.model.FailedProducts;

public class FailedProductsHelper {

    private FailedProductsHelper() {
    }

    public static FailedProducts buildFailedProductsUsingImageUploadTemplateRequest(String manufacturerName,
            String brandName,
            String categoryName, String productName,
            String createdBy, String reason) {
        FailedProducts failedProducts = new FailedProducts();
        failedProducts.setProductName(productName);
        failedProducts.setBrandName(brandName);
        failedProducts.setManufacturerName(manufacturerName);
        failedProducts.setProductCategoryName(categoryName);
        failedProducts.setPublicId(UUID.randomUUID());
        failedProducts.setCreatedBy(createdBy);
        failedProducts.setStatus(Status.ACTIVE.name());
        failedProducts.setCreatedDate(LocalDateTime.now());
        failedProducts.setLastModifiedBy(createdBy);
        failedProducts.setLastModifiedDate(LocalDateTime.now());
        failedProducts.setVersion(BigInteger.ZERO);
        failedProducts.setReason(reason);
        return failedProducts;
    }
}
