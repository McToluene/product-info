package com.mctoluene.productinformationmanagement.domain.response.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.Brand;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BrandResponse {

    private UUID publicId;
    private String brandName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;
    private UUID manufacturerPublicId;

    public BrandResponse(Brand brand) {
        this(brand.getPublicId(), brand.getBrandName(), brand.getDescription(), brand.getCreatedDate(),
                brand.getLastModifiedDate(),
                brand.getCreatedBy(), brand.getLastModifiedBy(), brand.getStatus().name(), brand.getVersion(),
                brand.getManufacturer().getPublicId());
    }
}
