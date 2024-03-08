package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.helper.ProductCategoryHierarchyHelper;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryHierarchyService;
import com.mctoluene.productinformationmanagement.service.internal.ProductCategoryHierarchyInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryHierarchyServiceImpl implements ProductCategoryHierarchyService {

    private final ProductCategoryHierarchyInternalService productCategoryHierarchyInternalService;

    private final MessageSourceService messageSourceService;

    @Override
    public void createProductCategoryHierarchyWithParentCategory(ProductCategory productCategory,
            UUID parentId) {

        Optional<ProductCategoryHierarchy> hierarchyExist = productCategoryHierarchyInternalService
                .findByCategoryPublicIdAndParentCategoryPublicId(productCategory.getPublicId(), parentId);

        if (hierarchyExist.isPresent())
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("product.and.hierarchy.already.exist"));

        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchyHelper
                .buildProductCategoryHierarchy(productCategory, parentId);

        productCategoryHierarchyInternalService.createNewProductCategoryHierarchy(productCategoryHierarchy);

    }

    @Override
    public void createProductParentCategoryHierarchyWithoutParentCategory(ProductCategory productCategory) {

        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchyHelper
                .buildProductCategoryHierarchy(productCategory);

        productCategoryHierarchyInternalService.createNewProductCategoryHierarchy(productCategoryHierarchy);
    }

}
