package com.mctoluene.productinformationmanagement.domain.stockone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    private String warehouseName;

    private List<ProductSkuDetail> skuDetails;
}
