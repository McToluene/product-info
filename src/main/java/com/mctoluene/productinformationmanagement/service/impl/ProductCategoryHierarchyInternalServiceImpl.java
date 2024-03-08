package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;
import com.mctoluene.productinformationmanagement.repository.ProductCategoryHierarchyRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ProductCategoryHierarchyInternalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryHierarchyInternalServiceImpl implements ProductCategoryHierarchyInternalService {

    private final ProductCategoryHierarchyRepository productCategoryHierarchyRepository;

    private final MessageSourceService messageSourceService;

    @Transactional
    @Override
    public ProductCategoryHierarchy createNewProductCategoryHierarchy(ProductCategoryHierarchy hierarchy) {
        log.info("about to save new product category hierarchy");
        return saveProductCategoryHierarchyToDb(hierarchy);
    }

    @Override
    public ProductCategoryHierarchy saveProductCategoryHierarchyToDb(ProductCategoryHierarchy hierarchy) {
        try {
            return productCategoryHierarchyRepository.save(hierarchy);
        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("Entity.could.not.be.processed"));
        }
    }

    @Override
    public Optional<ProductCategoryHierarchy> findByCategoryPublicIdAndParentCategoryPublicId(UUID categoryPublicId,
            UUID parentPublicId) {
        return productCategoryHierarchyRepository
                .findByProductCategoryPublicIdAndProductCategoryParentPublicId(categoryPublicId, parentPublicId);
    }

    @Override
    public List<ProductCategoryHierarchy> getAllProductCategory() {
        return productCategoryHierarchyRepository.findAll();
    }
}
