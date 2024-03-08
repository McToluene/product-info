package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UpdateVariantResponseDto {

    private UUID variantTypePublicId;
    private UUID publicId;
    private String variantName;
    private String variantDescription;
    private BigDecimal costPrice;
    private String modifiedBy;
}
