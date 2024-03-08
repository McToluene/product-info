package com.mctoluene.productinformationmanagement.domain.request.productcategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryRequestDto {

    @NotEmpty(message = "product category name cannot be empty")
    private String productCategoryName;

    private List<UUID> productCategoryParentPublicIds;

    private String description;

    private String imageUrl;

    @NotEmpty(message = "created by cannot be empty")
    private String createdBy;

}
