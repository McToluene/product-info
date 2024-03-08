package com.mctoluene.productinformationmanagement.domain.request.measuringunit;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UpdateMeasuringUnitRequestDto {

    private String name;

    private String description;

    private String abbreviation;

    @NotBlank(message = "{measuring.unit.modified.by.not.passed}")
    private String modifiedBy;

}
