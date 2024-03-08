package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryInternalService {

    ProductCategory saveNewProductCategory(ProductCategory productCategory);

    ProductCategory saveProductCategoryToDb(ProductCategory productCategory);

    ProductCategory findProductCategoryByName(String name);

    Optional<ProductCategory> findProductCategoryByNameIgnoreCase(String name);

    ProductCategory findProductCategoryByPublicId(UUID publicId);

    List<ProductCategory> findProductCategoryByPublicIds(List<UUID> publicId);

    Page<ProductCategory> getAllProductCategories(Pageable pageable);

    ProductCategory updateExistingProductCategory(ProductCategory productCategory);

    void deleteProductCategory(ProductCategory productCategory);

    Page<ProductCategory> getAllParentProductCategories(Pageable pageable);

    Page<ProductCategory> getAllDirectChildrenOfProductCategory(UUID publicId, Pageable pageable);

    List<ProductCategory> getAllDirectChildrenOfProductCategoryUnpaged(UUID publicId);

    List<ProductCategory> getAllChildrenOfProductCategory(UUID productCategoryPublicId);

    Page<ProductCategory> findAllBySearchCrieteria(Pageable request, String productCategoryName,
            LocalDateTime startDate, LocalDateTime endDate);

    List<ProductCategory> archiveCategoryAndSubCategoriesByPublicIds(List<UUID> listOfPublicIds);

    ProductCategory findByPublicIdAndStatus(UUID publicId, Status status);

    ProductCategoryWithSubcategoryResponse getParentCategoryAndSubCategories(ProductCategory productCategory);

    List<ProductCategory> findProductCategoryByCountryCode();
}
