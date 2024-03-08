package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.UpdateImageCatalogRequestDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateVariantRequestDto {

    private UUID variantTypePublicId;
    @NotEmpty(message = "variant name cannot be empty")
    private String variantName;
    private String variantDescription;
    @NotNull(message = "cost price cannot be null")
    private BigDecimal costPrice;
    @NotEmpty(message = "modified by cannot be empty")
    private String modifiedBy;
    private List<UpdateImageCatalogRequestDto> imageCatalogs;

    @NotNull(message = "country cannot be empty")
    private UUID countryPublicId;

    private BigDecimal vatValue;
}
