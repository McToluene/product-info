package com.mctoluene.productinformationmanagement.domain.request.brand;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class EditBrandRequestDto {

    private String brandName;

    private String description;

    @NotBlank(message = "{empty.modified.by}")
    private String lastModifiedBy;
    private UUID manufacturerId;
}
