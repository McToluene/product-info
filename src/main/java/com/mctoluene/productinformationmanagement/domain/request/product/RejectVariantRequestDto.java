package com.mctoluene.productinformationmanagement.domain.request.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RejectVariantRequestDto {

    @NotBlank(message = "rejected by cannot be empty")
    private String rejectedBy;

    @NotBlank(message = "rejection reason cannot be empty")
    private String rejectionReason;

}
