package com.mctoluene.productinformationmanagement.domain.request.varianttype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVariantTypeRequestDto {

    @NotEmpty(message = "variant type name cannot be empty")
    private String variantTypeName;

    private String description;

    @NotEmpty(message = "created by cannot be empty")
    private String createdBy;
}
