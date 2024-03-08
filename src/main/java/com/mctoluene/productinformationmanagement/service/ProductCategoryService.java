package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.UpdateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ProductCategoryFilter;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProductCategoryService {
    AppResponse createProductCategory(CreateProductCategoryRequestDto requestDto);

    AppResponse getProductCategoryByPublicId(UUID publicId);

    AppResponse getProductCategoriesByPublicIds(List<UUID> productCategoryPublicIds);

    AppResponse getAllProductCategories(Integer page, Integer size, Boolean isParent);

    AppResponse updateProductCategory(UUID publicId, UpdateProductCategoryRequestDto requestDto);

    AppResponse deleteProductCategory(UUID publicId);

    AppResponse getAllDirectSubcategoryOfProductCategory(UUID publicId, Integer page, Integer size);

    AppResponse getAllNestedSubcategoryOfProductCategory(UUID productCategoryProductId);

    AppResponse<List<ProductCategory>> getAllSubcategoryOfProductCategory(UUID productCategoryProductId);

    AppResponse getAllProductCategoriesFiltered(Integer page, Integer size, String productCategoryName,
            LocalDateTime startDate, LocalDateTime endDate);

    AppResponse archiveProductCategory(UUID productCategoryPublicId);

    AppResponse unArchiveProductCategory(UUID productCategoryPublicId);

    AppResponse getProductCategories(Integer page, Integer size);

    AppResponse getProductCategoryByCountryCode();

    AppResponse<Page<ProductCategoryResponse>> filterProductCategory(ProductCategoryFilter productCategoryFilter,
            Pageable pageable);

}
