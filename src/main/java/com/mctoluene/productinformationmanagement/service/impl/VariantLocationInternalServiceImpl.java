package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.model.VariantLocation;
import com.mctoluene.productinformationmanagement.repository.VariantLocationRepository;
import com.mctoluene.productinformationmanagement.service.internal.VariantLocationInternalService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantLocationInternalServiceImpl implements VariantLocationInternalService {
    private final VariantLocationRepository variantLocationRepository;

    @Override
    public VariantLocation saveProductToDb(VariantLocation variantLocation) {
        return variantLocationRepository.save(variantLocation);
    }

    @Override
    public boolean checkIfProductLocationExist(UUID variantId, UUID locationId) {
        var response = variantLocationRepository.findByVariantPublicIdAndLocationPublicIdAndStatus(variantId,
                locationId, Status.ACTIVE.toString());
        return response.isPresent();
    }

    @Override
    public List<VariantLocation> findByVariantPublicId(UUID variantPublicId) {
        return variantLocationRepository.findAllByVariantPublicIdAndStatus(variantPublicId, Status.ACTIVE.toString());
    }

}
