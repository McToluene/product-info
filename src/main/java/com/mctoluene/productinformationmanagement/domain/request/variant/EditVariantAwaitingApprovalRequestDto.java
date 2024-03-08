package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class EditVariantAwaitingApprovalRequestDto {

    private UUID variantTypeId;

    private String variantName;

    private String variantDescription;

    private String defaultImageUrl;

    private BigDecimal costPrice;

    @NotEmpty(message = "{empty.modified.by}")
    private String lastModifiedBy;
}
