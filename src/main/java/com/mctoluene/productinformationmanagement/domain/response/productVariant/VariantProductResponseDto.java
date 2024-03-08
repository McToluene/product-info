package com.mctoluene.productinformationmanagement.domain.response.productVariant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.response.ImageResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.MeasuringUnitResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;
import com.mctoluene.productinformationmanagement.model.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantProductResponseDto {
    private UUID publicId;
    private UUID variantTypeId;
    private String variantName;
    private String variantDescription;
    private String sku;
    private BigDecimal costPrice;
    private String createdBy;
    private String status;
    private String approvalStatus;
    private BigInteger version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private List<ImageResponseDto> imageCatalogs;
    private UUID productPublicId;
    private UUID countryPublicId;
    private BigInteger productVersion;
    private String productName;
    private Brand brand;
    private ManufacturerResponseDto manufacturer;
    private ProductCategoryResponseDto productCategory;
    private MeasuringUnitResponseDto measurementUnit;
    private String productListing;
    private String defaultImageUrl;
    private String productDescription;
    private String productHighlights;
    private String warrantyDuration;
    private String warrantyCover;
    private WarrantyTypeResponseDto warrantyType;
    private String warrantyAddress;
    private String productStatus;
    private String note;
    private ProductCategoryWithSubcategoryResponse parentCategory;
    private boolean isVated;
    private BigDecimal vatValue;
    private Double weight;

}
