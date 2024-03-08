package com.mctoluene.productinformationmanagement.domain.request.manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateManufacturerRequestDto {

    private String description;

    @NotEmpty(message = "manufacturerName field cannot be empty")
    private String manufacturerName;

    @NotEmpty(message = "createdBy field cannot be empty")
    private String createdBy;

}
