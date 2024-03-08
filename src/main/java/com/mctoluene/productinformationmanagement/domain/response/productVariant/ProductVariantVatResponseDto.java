package com.mctoluene.productinformationmanagement.domain.response.productVariant;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
public class ProductVariantVatResponseDto implements Serializable {
    UUID getPublicId;
    String getSku;
    boolean getIsVated;
    BigDecimal getVatValue;
}
