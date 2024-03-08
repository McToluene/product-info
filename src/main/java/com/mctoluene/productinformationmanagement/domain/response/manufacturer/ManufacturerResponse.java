package com.mctoluene.productinformationmanagement.domain.response.manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.Manufacturer;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ManufacturerResponse {
    private UUID publicId;
    private String manufacturerName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;

    public ManufacturerResponse(Manufacturer manufacturer) {
        this(manufacturer.getPublicId(), manufacturer.getManufacturerName(), manufacturer.getDescription(),
                manufacturer.getCreatedDate(),
                manufacturer.getLastModifiedDate(),
                manufacturer.getCreatedBy(), manufacturer.getLastModifiedBy(), manufacturer.getStatus().name(),
                manufacturer.getVersion());
    }
}
