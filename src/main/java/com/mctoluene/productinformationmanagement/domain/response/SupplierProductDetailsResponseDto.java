package com.mctoluene.productinformationmanagement.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierProductDetailsResponseDto {
    private UUID publicId;
    private UUID supplierId;
    private String stateName;
    private String lgaName;
    private UUID productId;
    private String productName;
    private BigInteger quantity;
    private String sku;
    private String status;
    private String supplierName;
    private String variantName;
    private UUID variantPublicId;
    private int productThreshold;
    private List<FulFilmentTypeResponse> fulFilmentTypes;
    private UUID warehouseId;
    private String warehouseName;
}
