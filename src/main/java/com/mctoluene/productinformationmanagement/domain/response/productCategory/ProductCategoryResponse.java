package com.mctoluene.productinformationmanagement.domain.response.productCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.ProductCategory;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductCategoryResponse {
    private UUID publicId;
    private String productCategoryName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;
    private UUID countryId;

    public ProductCategoryResponse(ProductCategory productCategory) {
        this(productCategory.getPublicId(), productCategory.getProductCategoryName(), productCategory.getDescription(),
                productCategory.getCreatedDate(), productCategory.getLastModifiedDate(),
                productCategory.getCreatedBy(), productCategory.getLastModifiedBy(),
                productCategory.getStatus().name(), productCategory.getVersion(), productCategory.getCountryId());
    }

}
