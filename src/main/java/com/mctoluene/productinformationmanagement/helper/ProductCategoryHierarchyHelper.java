package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;

public class ProductCategoryHierarchyHelper {

    private ProductCategoryHierarchyHelper() {
    }

    public static ProductCategoryHierarchy buildProductCategoryHierarchy(ProductCategory productCategory,
            UUID parentId) {
        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchy.builder()
                .productCategoryPublicId(productCategory.getPublicId())
                .productCategoryParentPublicId(parentId)
                .build();

        buildBaseEntity(productCategory, productCategoryHierarchy);

        return productCategoryHierarchy;
    }

    public static ProductCategoryHierarchy buildProductCategoryHierarchy(ProductCategory productCategory) {
        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchy.builder()
                .productCategoryPublicId(productCategory.getPublicId())
                .build();

        buildBaseEntity(productCategory, productCategoryHierarchy);

        return productCategoryHierarchy;
    }

    private static void buildBaseEntity(ProductCategory productCategory,
            ProductCategoryHierarchy productCategoryHierarchy) {
        productCategoryHierarchy.setPublicId(UUID.randomUUID());
        productCategoryHierarchy.setCreatedBy(productCategory.getCreatedBy());
        productCategoryHierarchy.setLastModifiedDate(LocalDateTime.now());
        productCategoryHierarchy.setLastModifiedBy(productCategory.getCreatedBy());
        productCategoryHierarchy.setCreatedDate(LocalDateTime.now());
        productCategoryHierarchy.setVersion(BigInteger.ZERO);
    }
}
