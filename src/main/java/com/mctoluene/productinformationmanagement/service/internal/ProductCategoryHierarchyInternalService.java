package com.mctoluene.productinformationmanagement.service.internal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;

public interface ProductCategoryHierarchyInternalService {

    ProductCategoryHierarchy createNewProductCategoryHierarchy(ProductCategoryHierarchy hierarchy);

    ProductCategoryHierarchy saveProductCategoryHierarchyToDb(ProductCategoryHierarchy hierarchy);

    Optional<ProductCategoryHierarchy> findByCategoryPublicIdAndParentCategoryPublicId(UUID categoryPublicId,
            UUID parentPublicId);

    List<ProductCategoryHierarchy> getAllProductCategory();
}
