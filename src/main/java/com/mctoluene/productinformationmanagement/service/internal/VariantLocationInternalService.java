package com.mctoluene.productinformationmanagement.service.internal;

import java.util.List;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.VariantLocation;

public interface VariantLocationInternalService {

    VariantLocation saveProductToDb(VariantLocation variantLocation);

    boolean checkIfProductLocationExist(UUID variantId, UUID locationId);

    List<VariantLocation> findByVariantPublicId(UUID variantPublicId);

}
