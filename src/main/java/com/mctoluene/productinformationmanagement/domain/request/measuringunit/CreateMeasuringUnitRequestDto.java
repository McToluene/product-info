package com.mctoluene.productinformationmanagement.domain.request.measuringunit;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class CreateMeasuringUnitRequestDto {

    @NotBlank(message = "{measuring.unit.name.not.passed}")
    private String name;
    private String description;
    @NotBlank(message = "{measuring.unit.abbreviation.not.passed}")
    private String abbreviation;
    @NotBlank(message = "{measuring.unit.modified.by.not.passed}")
    private String createdBy;

}
