package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryHierarchyRepository extends JpaRepository<ProductCategoryHierarchy, UUID> {

    Optional<ProductCategoryHierarchy> findByProductCategoryPublicIdAndProductCategoryParentPublicId(UUID categoryId,
            UUID parentId);
}
