package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.commons.response.AppResponse;

import java.util.UUID;

public interface VariantLocationService {
    AppResponse linkVariantToLocation(VariantLocationRequestdto variantLocationRequestdto);

    AppResponse searchProductByQuery(String query, UUID statePublicId, int page, int size);
}
