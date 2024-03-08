package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantVersion;
import com.mctoluene.productinformationmanagement.repository.VariantVersionRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.VariantVersionInternalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VariantVersionInternalServiceImpl implements VariantVersionInternalService {

    private final MessageSourceService messageSourceService;

    private final VariantVersionRepository variantVersionRepository;

    @Override
    public VariantVersion saveVariantVersionToDb(VariantVersion variantVersion) {
        try {
            return variantVersionRepository.save(variantVersion);
        } catch (Exception e) {
            throw new UnProcessableEntityException("Could not process request");
        }
    }

    @Override
    public List<VariantVersion> saveVariantVersionsToDb(List<VariantVersion> variantVersions) {
        try {

            return variantVersionRepository.saveAll(variantVersions);
        } catch (Exception e) {
            throw new UnProcessableEntityException(messageSourceService.getMessageByKey("Could not process request"));
        }
    }

    @Override
    public List<VariantVersion> getProductInUse(UUID id) {
        return variantVersionRepository.findVariantVersionById(id);
    }

    @Override
    public VariantVersion findByProductVariantId(UUID productVariantId) {
        return variantVersionRepository.findByProductVariantId(productVariantId)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    public VariantVersion findMostRecentVariantVersion(UUID productVariantId) {
        // The assumption here is tha the most recent variant version is the ACTIVE one.
        return variantVersionRepository.findActiveByProductVariantId(productVariantId)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    public void setVersionToStatusByProductVariantId(UUID productVariantId, String status) {
        variantVersionRepository.updateVersionAndStatusByProductVariantId(productVariantId, Status.INACTIVE.name());
    }

    @Override
    public VariantVersion findBySku(String sku) {
        return variantVersionRepository.findBySkuAndStatus(sku, Status.ACTIVE.name())
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    public List<VariantVersion> findAllBySkuIn(List<String> variantSkuList) {
        return variantVersionRepository.findAllBySku(variantSkuList);
    }

    @Override
    public List<VariantVersion> searchVariantVersionByProductVariantsIn(String searchParam,
            List<UUID> productVariantIds) {
        return variantVersionRepository.searchVariantVersionByProductVariantsIn(searchParam, productVariantIds);
    }

    @Override
    public Optional<VariantVersion> findByVariantTypeId(UUID variantTypeId) {
        return variantVersionRepository.findByVariantTypeIdAndStatus(variantTypeId, Status.ACTIVE.name());
    }

    @Override
    public Optional<VariantVersion> findByProductId(UUID productId) {
        return variantVersionRepository.findByProductIdAndStatus(productId, Status.ACTIVE.name());
    }

    @Override
    public Page<VariantVersion> searchVariantBySkuListAndProductName(List<String> skuList, String searchValue,
            Pageable pageable) {
        return variantVersionRepository.findBySkuListAndProductName(skuList, searchValue, pageable);
    }

    @Override
    public Page<VariantVersion> findVariantsByProductCategoryId(UUID productCategoryId, Pageable pageable) {
        return variantVersionRepository.findByCategoryId(productCategoryId, pageable);
    }

    @Override
    public VariantVersion findByProductVariantIdAndStatus(UUID productVariantId, String status) {
        return variantVersionRepository.findByProductVariantIdAndStatus(productVariantId, status)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    public List<VariantVersion> findVariantBySkuList(List<String> skuList) {
        return variantVersionRepository.findBySkuList(skuList);
    }

    @Override
    public VariantVersion findVariantBySku(String sku) {
        return variantVersionRepository.findBySku(sku)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

}
