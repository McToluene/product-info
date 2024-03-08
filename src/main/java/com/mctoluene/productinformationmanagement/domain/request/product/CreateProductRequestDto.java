package com.mctoluene.productinformationmanagement.domain.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.variant.CreateVariantAwaitingApprovalRequestDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequestDto {
    @NotEmpty(message = "product name cannot be empty")
    private String productName;

    @NotNull(message = "brand cannot be empty")
    private UUID brandPublicId;

    @NotNull(message = "manufacturer cannot be empty")
    private UUID manufacturerPublicId;

    @NotNull(message = "brand cannot be empty")
    private UUID categoryPublicId;

    private String warrantyTypePublicId;

    @NotNull(message = "measurement unit cannot be empty")
    private UUID measurementUnitPublicId;

    private Set<ProductListing> productListings;

    private String productDescription;

    private String productHighlights;

    private String warrantyDuration;

    private String warrantyCover;

    private String note;

    private String warrantyAddress;

    private List<CreateVariantAwaitingApprovalRequestDto> variants;

    @NotEmpty(message = "created by cannot be empty")
    private String createdBy;

    private Status status;

    private Boolean vated = Boolean.FALSE;

    private BigDecimal minVat = BigDecimal.ZERO;

    private BigDecimal maxVat = BigDecimal.ZERO;

}
