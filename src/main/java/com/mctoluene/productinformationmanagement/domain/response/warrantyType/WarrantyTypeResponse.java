package com.mctoluene.productinformationmanagement.domain.response.warrantyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.WarrantyType;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WarrantyTypeResponse {
    private UUID publicId;
    private String warrantyTypeName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;

    public WarrantyTypeResponse(WarrantyType warrantyType) {
        this(warrantyType.getPublicId(), warrantyType.getWarrantyTypeName(), warrantyType.getDescription(),
                warrantyType.getCreatedDate(), warrantyType.getLastModifiedDate(), warrantyType.getCreatedBy(),
                warrantyType.getLastModifiedBy(), warrantyType.getStatus().name(), warrantyType.getVersion());
    }
}
