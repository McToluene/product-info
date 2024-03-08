package com.mctoluene.productinformationmanagement.domain.request.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandManufacturerRequest {

    private String manufacturerName;
    private String manufacturerDescription;
    private String brandName;
    private String brandDescription;
    private String createdBy;
}
