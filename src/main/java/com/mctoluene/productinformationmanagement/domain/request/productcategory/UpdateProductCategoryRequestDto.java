package com.mctoluene.productinformationmanagement.domain.request.productcategory;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class UpdateProductCategoryRequestDto {

    private String productCategoryName;

    private String description;

    private String imageUrl;

    private List<UUID> productCategoryParentPublicIds;

    @NotEmpty(message = "modified by cannot be empty")
    private String modifiedBy;

}
