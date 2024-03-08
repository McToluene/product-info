package com.mctoluene.productinformationmanagement.domain.request.warrantytype;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UpdateWarrantyTypeRequestDto {

    @NotEmpty(message = "{empty.warranty.type.name}")
    private String warrantyTypeName;

    private String description;

    @NotEmpty(message = "{empty.lastModified.by}")
    private String lastModifiedBy;
}
