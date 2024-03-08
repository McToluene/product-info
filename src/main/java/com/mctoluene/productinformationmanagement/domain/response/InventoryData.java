package com.mctoluene.productinformationmanagement.domain.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InventoryData(String sku_code, Double available_quantity, Integer reserved_quantity,
        Integer total_quantity, Integer open_order_quantity, Integer putaway_qty, Double non_sellable_quantity,
        List<BatchDetail> batch_details) {
}
