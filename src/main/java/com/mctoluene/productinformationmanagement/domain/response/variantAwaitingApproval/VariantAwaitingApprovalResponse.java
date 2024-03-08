package com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAwaitingApprovalResponse {

    private UUID publicId;

    private String variantTypeName;

    private String variantName;

    private String defaultImageUrl;

    private String variantDescription;

    private String sku;

    private BigDecimal costPrice;

    private String createdBy;

    private String completedBy;

    private LocalDateTime completedDate;

    private String status;

    private String approvalStatus;

    private BigInteger version;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;

    private String manufacturer;

    private String brandName;

    private String productName;

    private Double weight;

    public VariantAwaitingApprovalResponse(VariantAwaitingApproval vaa) {
        this(vaa.getPublicId(), vaa.getVariantType().getVariantTypeName(), vaa.getVariantName(),
                vaa.getDefaultImageUrl(),
                vaa.getVariantDescription(), vaa.getSku(), vaa.getCostPrice(), vaa.getCreatedBy(), vaa.getCompletedBy(),
                vaa.getCompletedDate(), vaa.getStatus(), vaa.getApprovalStatus(), vaa.getVersion(),
                vaa.getCreatedDate(),
                vaa.getLastModifiedDate(), vaa.getLastModifiedBy(),
                vaa.getProduct().getManufacturer().getManufacturerName(),
                vaa.getProduct().getBrand().getBrandName(), vaa.getProduct().getProductName(), vaa.getWeight());
    }
}
