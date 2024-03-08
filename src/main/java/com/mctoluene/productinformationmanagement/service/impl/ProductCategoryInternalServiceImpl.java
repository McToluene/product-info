package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.repository.ProductCategoryRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ProductCategoryInternalService;

import static com.mctoluene.productinformationmanagement.helper.ProductCategoryHelper.buildCategoryResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryInternalServiceImpl implements ProductCategoryInternalService {

    private final MessageSourceService messageSourceService;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    @Override
    public ProductCategory saveNewProductCategory(ProductCategory productCategory) {
        log.info("about to save new product category");
        Optional<ProductCategory> categoryNameExists = productCategoryRepository
                .findByProductCategoryName(productCategory.getProductCategoryName().trim());

        if (categoryNameExists.isPresent())
            throw new ValidatorException(messageSourceService.getMessageByKey("Category.name.not.unique"));

        return saveProductCategoryToDb(productCategory);
    }

    @Override
    public ProductCategory saveProductCategoryToDb(ProductCategory productCategory) {
        try {
            return productCategoryRepository.save(productCategory);
        } catch (Exception e) {
            throw new UnProcessableEntityException("Could.not.process.request");
        }
    }

    @Override
    public ProductCategory findProductCategoryByName(String name) {

        return productCategoryRepository.findByProductCategoryName(name)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService
                        .getMessageByKey("product.category.not.found")));

    }

    @Override
    public Optional<ProductCategory> findProductCategoryByNameIgnoreCase(String name) {
        return productCategoryRepository.findByProductCategoryNameIgnoreCase(name);
    }

    @Override
    public ProductCategory findProductCategoryByPublicId(UUID publicId) {
        return productCategoryRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService
                        .getMessageByKey("product.category.not.found")));

    }

    @Override
    public List<ProductCategory> findProductCategoryByPublicIds(List<UUID> publicIds) {
        return productCategoryRepository.findByPublicIdIn(publicIds);
    }

    @Override
    public Page<ProductCategory> getAllProductCategories(Pageable pageable) {
        return productCategoryRepository.findAll(pageable);
    }

    @Override
    public Page<ProductCategory> getAllParentProductCategories(Pageable pageable) {
        return productCategoryRepository.findAllParentProductCategory(pageable);
    }

    @Override
    public Page<ProductCategory> getAllDirectChildrenOfProductCategory(UUID publicId, Pageable pageable) {
        return productCategoryRepository.findAllDirectChildrenOfProductCategory(publicId, pageable);
    }

    @Override
    public List<ProductCategory> getAllDirectChildrenOfProductCategoryUnpaged(UUID publicId) {
        return productCategoryRepository.findAllDirectChildrenOfProductCategoryUnpaged(publicId);
    }

    @Override
    public List<ProductCategory> getAllChildrenOfProductCategory(UUID productCategoryPublicId) {

        HashSet<ProductCategory> seen = new HashSet<>();

        Queue<ProductCategory> categories = new LinkedList<>();

        Optional<ProductCategory> productCategory = productCategoryRepository.findByPublicId(productCategoryPublicId);
        if (productCategory.isEmpty())
            throw new ModelNotFoundException("product category not found");

        categories.add(productCategory.get());

        while (!categories.isEmpty()) {
            ProductCategory currentCategory = categories.poll();

            if (currentCategory != null && !seen.contains(currentCategory)) {
                List<ProductCategory> directChildrenOfProductCategory = getAllDirectChildrenOfProductCategoryUnpaged(
                        currentCategory.getPublicId());

                seen.add(currentCategory);

                for (ProductCategory category : directChildrenOfProductCategory) {
                    if (!seen.contains(category))
                        categories.add(category);
                }
            }
        }

        return seen.stream().toList();

    }

    @Override
    public Page<ProductCategory> findAllBySearchCrieteria(Pageable request,
            String productCategoryName,
            LocalDateTime startDate, LocalDateTime endDate) {
        return productCategoryRepository
                .findAllByProductCategoryNameIgnoreCaseContainingLike(request, productCategoryName.toLowerCase(),
                        startDate, endDate);

    }

    @Override
    public List<ProductCategory> archiveCategoryAndSubCategoriesByPublicIds(List<UUID> listOfPublicIds) {
        return productCategoryRepository.archiveCategoryAndSubCategoriesByPublicIds(listOfPublicIds);
    }

    @Override
    public ProductCategory findByPublicIdAndStatus(UUID publicId, Status status) {
        return productCategoryRepository.findByPublicIdAndStatus(publicId, status)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("product.category.not.found")));
    }

    @Transactional
    @Override
    public ProductCategory updateExistingProductCategory(ProductCategory productCategory) {
        log.info("about to update product category {}", productCategory.getProductCategoryName());
        return saveProductCategoryToDb(productCategory);
    }

    @Transactional
    @Override
    public void deleteProductCategory(ProductCategory productCategory) {
        saveProductCategoryToDb(productCategory);
    }

    public ProductCategoryWithSubcategoryResponse getParentCategoryAndSubCategories(ProductCategory productCategory) {
        Optional<ProductCategory> parentProductCategory = productCategoryRepository
                .findParentProductCategory(productCategory.getPublicId());

        if (parentProductCategory.isEmpty())
            return null;

        List<ProductCategory> subCategories = getAllChildrenOfProductCategory(
                parentProductCategory.get().getPublicId());
        Set<String> subCategoriesNames = subCategories.stream()
                .map(ProductCategory::getProductCategoryName)
                .collect(Collectors.toSet());

        subCategoriesNames.remove(parentProductCategory.get().getProductCategoryName());

        int count = subCategoriesNames.size();

        String commaSeparatedNames = String.join(", ", subCategoriesNames);

        return buildCategoryResponse(parentProductCategory.get(), commaSeparatedNames, count);
    }

    @Override
    public List<ProductCategory> findProductCategoryByCountryCode() {
        return productCategoryRepository.findByCountryId();
    }

}
