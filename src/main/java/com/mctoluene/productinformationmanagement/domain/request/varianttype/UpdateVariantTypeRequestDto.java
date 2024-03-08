package com.mctoluene.productinformationmanagement.domain.request.varianttype;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UpdateVariantTypeRequestDto {

    private String variantTypeName;

    private String description;

    @NotBlank(message = "lastModifiedBy by cannot be empty")
    private String lastModifiedBy;
}
