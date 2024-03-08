package com.mctoluene.productinformationmanagement.domain.request.manufacturer;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UpdateManufacturerRequestDto {

    private String description;

    @NotBlank(message = "manufacturerName field cannot be empty")
    private String manufacturerName;

    @NotBlank(message = "lastModifiedBy field cannot be empty")
    private String lastModifiedBy;

}
