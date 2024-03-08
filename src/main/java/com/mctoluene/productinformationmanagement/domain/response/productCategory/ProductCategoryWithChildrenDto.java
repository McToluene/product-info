package com.mctoluene.productinformationmanagement.domain.response.productCategory;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductCategoryWithChildrenDto {
    private UUID publicId;
    private String productCategoryName;

    private String imageUrl;

    private String description;

    private List<ProductCategoryWithChildrenDto> children;

    private String status;

    private BigInteger version;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;
}
