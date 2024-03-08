package com.mctoluene.productinformationmanagement.domain.request.product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class ApproveRequestDto {
    @NotBlank(message = "approved by cannot be empty")
    private String approvedBy;

}
