package com.mctoluene.productinformationmanagement.domain.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDto {

    private String productName;

    private String productDescription;

    private UUID measurementUnitPublicId;

    private String productListing;

    private String defaultImageUrl;

    private UUID brandPublicId;

    private UUID manufacturerPublicId;

    private UUID categoryPublicId;

    private UUID warrantyTypePublicId;

    private String productHighlights;

    private String warrantyDuration;

    private String warrantyCover;

    private String warrantyAddress;

    private UUID countryId;

    @NotEmpty(message = "{empty.modified.by}")
    private String modifiedBy;

    private BigDecimal minVat;

    private BigDecimal maxVat;

}
