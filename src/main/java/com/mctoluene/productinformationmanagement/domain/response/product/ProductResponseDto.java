package com.mctoluene.productinformationmanagement.domain.response.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDto {
    private UUID publicId;
    private BigInteger version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String productName;

    private BrandResponseDto brand;
    private ManufacturerResponseDto manufacturer;
    private ProductCategoryResponseDto productCategory;
    private WarrantyTypeResponseDto warrantyType;

    private String measurementUnit;
    private String productListing;
    private String defaultImageUrl;
    private String productDescription;
    private String productHighlights;
    private String warrantyDuration;

    private String warrantyCover;

    private String warrantyAddress;

    private String status;
    private Long variantCount;

    private String note;

    private boolean isVated;

    private BigDecimal minVat;

    private BigDecimal maxVat;
    private ProductCategoryWithSubcategoryResponse parentCategory;

}
