package com.mctoluene.productinformationmanagement.domain.response.variantType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.VariantType;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VariantTypeResponse {
    private UUID publicId;
    private String variantTypeName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;

    public VariantTypeResponse(VariantType variantType) {
        this(variantType.getPublicId(), variantType.getVariantTypeName(), variantType.getDescription(),
                variantType.getCreatedDate(), variantType.getLastModifiedDate(), variantType.getCreatedBy(),
                variantType.getLastModifiedBy(), variantType.getStatus(), variantType.getVersion());
    }
}
