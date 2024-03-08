package com.mctoluene.productinformationmanagement.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mctoluene.productinformationmanagement.domain.enums.ProductInventoryType;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCatalogueResponseDto {

    private UUID warehouseId;
    private String warehouseName;
    private String variantName;
    private String sku;
    private String variantDescription;
    private Integer availableQuantity;
    private BigInteger minimumOrderQuantity;
    private BigInteger maximumOrderQuantity;
    private Double individualPricing;
    private List<VolumePricing> volumePricing;
    private String supplierName;
    private UUID supplierId;
    private UUID productPublicId;
    private UUID variantPublicId;
    private List<FulFilmentTypeResponse> fulfillmentType;
    private String stateName;
    private String lgaName;
    private ProductInventoryType productInventoryType;
    private List<String> imageUrls;

}
