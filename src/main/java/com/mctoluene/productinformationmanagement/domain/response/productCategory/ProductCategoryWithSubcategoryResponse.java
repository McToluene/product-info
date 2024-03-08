package com.mctoluene.productinformationmanagement.domain.response.productCategory;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductCategoryWithSubcategoryResponse {

    private UUID publicId;

    private BigInteger version;

    private LocalDateTime createdDate;

    private String createdBy;

    private String lastModifiedBy;

    private String productCategoryName;

    private String description;

    private String imageUrl;

    private String status;

    private int subcategoryCount;

    private String subcategories;

}
