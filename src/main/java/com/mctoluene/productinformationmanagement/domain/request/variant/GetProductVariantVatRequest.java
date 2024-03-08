package com.mctoluene.productinformationmanagement.domain.request.variant;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class GetProductVariantVatRequest {

    private List<String> productVariantSkus;

    private List<UUID> productVariantPublicIds;
}
