package com.mctoluene.productinformationmanagement.domain.request.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.mctoluene.productinformationmanagement.domain.enums.Status;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequestDto {

    @NotEmpty(message = "Brand name must be provided")
    private String brandName;
    private String description;
    private Status status;
    @NotEmpty(message = "createdBy email must be provided")
    private String createdBy;

    @NotNull(message = "manufacturerId must be provided")
    private UUID manufacturerId;
}
