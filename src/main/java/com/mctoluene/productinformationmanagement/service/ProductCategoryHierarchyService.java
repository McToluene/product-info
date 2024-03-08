package com.mctoluene.productinformationmanagement.service;

import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.ProductCategory;

public interface ProductCategoryHierarchyService {

    void createProductCategoryHierarchyWithParentCategory(ProductCategory productCategory, UUID parentId);

    void createProductParentCategoryHierarchyWithoutParentCategory(ProductCategory productCategory);
}
