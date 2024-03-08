package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class EditLiveInventoryRequestDto {

    @NotBlank(message = "{empty.product.sku}")
    private String sku;

    @Min(value = 1, message = "{error.lead.time}")
    @Max(value = 30)
    private Integer leadTime;

    @Min(value = 1, message = "{error.threshold.value}")
    private Integer threshold;

    @NotBlank(message = "{empty.modified.by}")
    private String modifiedBy;
}
