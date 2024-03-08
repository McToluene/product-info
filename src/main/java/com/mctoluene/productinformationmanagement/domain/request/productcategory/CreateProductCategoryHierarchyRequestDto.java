package com.mctoluene.productinformationmanagement.domain.request.productcategory;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CreateProductCategoryHierarchyRequestDto {

    private UUID productCategoryPublicId;

    private UUID publicCategoryParentPublicId;

    private String createdBy;
}
