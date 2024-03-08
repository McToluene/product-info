package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class CreateVariantRequestDto {
    @NotNull(message = "variant type cannot be empty")
    private UUID variantTypeId;

    @NotEmpty(message = "variant name cannot be empty")
    private String variantName;

    private String variantDescription;

    private BigDecimal costPrice;

    @NotEmpty(message = "created by cannot be empty")
    private String createdBy;
}
