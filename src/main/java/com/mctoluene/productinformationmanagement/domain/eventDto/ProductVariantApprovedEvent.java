package com.mctoluene.productinformationmanagement.domain.eventDto;

import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

public record ProductVariantApprovedEvent(ProductVariant productVariant, ProductRequestDto productRequestDto) {
}
