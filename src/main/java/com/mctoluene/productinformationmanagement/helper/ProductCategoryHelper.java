package com.mctoluene.productinformationmanagement.helper;

import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithChildrenDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductCategoryHelper {

    private ProductCategoryHelper() {
    }

    public static ProductCategoryResponseDto buildProductCategoryResponseDto(ProductCategory productCategory) {
        return ProductCategoryResponseDto.builder()
                .publicId(productCategory.getPublicId())
                .productCategoryName(productCategory.getProductCategoryName().trim())
                .description(productCategory.getDescription())
                .imageUrl(productCategory.getImageUrl())
                .createdDate(productCategory.getCreatedDate())
                .createdBy(productCategory.getCreatedBy())
                .lastModifiedBy(productCategory.getLastModifiedBy())
                .lastModifiedDate(productCategory.getLastModifiedDate())
                .status(productCategory.getStatus().name())
                .version(productCategory.getVersion())
                // .countryCode(countryCode)
                .build();
    }

    public static ProductCategory buildProductCategory(CreateProductCategoryRequestDto requestDto, String url,
            UUID countryId) {
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName(WordUtils.capitalizeFully(
                        StringSanitizerUtils.sanitizeInput(requestDto.getProductCategoryName().trim())))
                .description(requestDto.getDescription().isEmpty() ? ""
                        : StringSanitizerUtils.sanitizeInput(requestDto.getDescription().trim()))
                .imageUrl(url)
                .status(Status.ACTIVE).build();

        productCategory.setPublicId(UUID.randomUUID());
        productCategory.setCreatedBy(requestDto.getCreatedBy());
        productCategory.setLastModifiedDate(LocalDateTime.now());
        productCategory.setLastModifiedBy(requestDto.getCreatedBy());
        productCategory.setCreatedDate(LocalDateTime.now());
        productCategory.setVersion(BigInteger.ZERO);
        productCategory.setCountryId(countryId);

        return productCategory;
    }

    public static ProductCategoryWithChildrenDto buildProductCategoryWithChildren(ProductCategory productCategory) {
        return ProductCategoryWithChildrenDto.builder()
                .publicId(productCategory.getPublicId())
                .productCategoryName(productCategory.getProductCategoryName().trim())
                .description(productCategory.getDescription())
                .imageUrl(productCategory.getImageUrl())
                .createdDate(productCategory.getCreatedDate())
                .createdBy(productCategory.getCreatedBy())
                .lastModifiedBy(productCategory.getLastModifiedBy())
                .lastModifiedDate(productCategory.getLastModifiedDate())
                .status(productCategory.getStatus().name())
                .version(productCategory.getVersion())
                .build();
    }

    public static ProductCategoryWithSubcategoryResponse buildCategoryResponse(ProductCategory productCategory,
            String commaSeparatedNames, int count) {
        return ProductCategoryWithSubcategoryResponse.builder()
                .status(productCategory.getStatus().name())
                .publicId(productCategory.getPublicId())
                .imageUrl(productCategory.getImageUrl())
                .productCategoryName(productCategory.getProductCategoryName())
                .description(productCategory.getDescription())
                .version(productCategory.getVersion())
                .subcategoryCount(count)
                .createdBy(productCategory.getCreatedBy())
                .createdDate(productCategory.getCreatedDate())
                .subcategories(commaSeparatedNames)
                .build();

    }
}
