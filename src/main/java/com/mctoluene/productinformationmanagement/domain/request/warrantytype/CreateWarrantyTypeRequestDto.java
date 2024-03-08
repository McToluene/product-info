package com.mctoluene.productinformationmanagement.domain.request.warrantytype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWarrantyTypeRequestDto {
    @NotEmpty(message = "{empty.warranty.type.name}")
    private String warrantyTypeName;

    private String description;

    @NotEmpty(message = "{empty.created.by}")
    private String createdBy;

}
