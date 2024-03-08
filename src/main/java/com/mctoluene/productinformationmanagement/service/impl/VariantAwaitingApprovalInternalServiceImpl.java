package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.repository.VariantAwaitingApprovalRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalInternalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VariantAwaitingApprovalInternalServiceImpl implements VariantAwaitingApprovalInternalService {

    private final MessageSourceService messageSourceService;

    private final VariantAwaitingApprovalRepository variantAwaitingApprovalRepository;

    @Override
    public List<VariantAwaitingApproval> saveVariantAwaitingApprovalToDb(List<VariantAwaitingApproval> variants) {
        try {
            log.info("about to save variants awaiting approval {} ", variants);
            return variantAwaitingApprovalRepository.saveAll(variants);
        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("an.error.occurred.creating.variant"));
        }
    }

    @Override
    public VariantAwaitingApproval findByPublicId(UUID publicId) {
        log.info("find product awaiting approval by public id {}", publicId);
        return variantAwaitingApprovalRepository.findByPublicId(publicId)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    public VariantAwaitingApproval saveVariantAwaitApprovalToDb(VariantAwaitingApproval variantAwaitingApproval) {
        return variantAwaitingApprovalRepository.save(variantAwaitingApproval);
    }

    @Override
    public VariantAwaitingApproval findByVariantAwaitingApprovalByPublicId(UUID variantAwatingApprovalPublicId) {
        log.info("inside findByPublicId {}", variantAwatingApprovalPublicId);

        return variantAwaitingApprovalRepository.findByVariantAwatingApprovalPublicId(variantAwatingApprovalPublicId)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));

    }

    @Override
    public Page<VariantAwaitingApproval> searchVariantsAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, String status, Pageable pageable) {
        return variantAwaitingApprovalRepository.searchVariantAwaitingApproval("%" + searchValue.toLowerCase() + "%",
                countryId,
                startDate, endDate,
                approvalStatus, status, pageable);
    }

    @Override
    public Page<VariantAwaitingApproval> getVariantsWithMissingImages(String searchParam, LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable) {
        return variantAwaitingApprovalRepository
                .findVariantsWithMissingImages("%" + searchParam.toLowerCase() + "%", fromDate, toDate, pageable);
    }

    @Override
    public Optional<VariantAwaitingApproval> findVariantBySku(String sku) {
        return variantAwaitingApprovalRepository.findVariantAwaitingApprovalBySku(sku);
    }

    @Override
    public void setApprovalStatusByProductVariantId(UUID productVariantId, String name) {
        variantAwaitingApprovalRepository.updateApprovalStatusByProductVariantId(productVariantId,
                ApprovalStatus.APPROVED.name());
    }

    @Override
    public Page<VariantAwaitingApproval> searchVariantsAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate, LocalDateTime endDate, String approvalStatus, List<String> listOfStatus,
            Pageable pageable) {
        return variantAwaitingApprovalRepository.searchVariantAwaitingApproval("%" + searchValue.toLowerCase() + "%",
                countryId,
                startDate, endDate,
                approvalStatus, listOfStatus, pageable);
    }

    @Override
    public Page<VariantAwaitingApproval> getRejectedVariantsAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return variantAwaitingApprovalRepository.getRejectedVariantsAwaitingApproval(
                "%" + searchValue.toLowerCase() + "%", countryId, startDate, endDate, pageable);
    }

    @Override
    public VariantAwaitingApproval findBySkuAndApprovalStatus(String sku, String approvalStatus) {
        return variantAwaitingApprovalRepository.findBySkuAndApprovalStatus(sku, approvalStatus)
                .orElse(null);
    }

    @Override
    public List<VariantAwaitingApproval> findByVariantNameAndApprovalStatusNotAndProduct(String variantName,
            ApprovalStatus approvalStatus, Product product) {
        return variantAwaitingApprovalRepository
                .findByVariantNameIgnoreCaseAndApprovalStatusNotAndProduct(variantName, approvalStatus.name(), product);
    }

    @Override
    public Optional<List<VariantAwaitingApproval>> findByVariantNameIgnoreCaseAndProductProductNameIgnoreCase(
            String variantName, String productName) {
        return variantAwaitingApprovalRepository.findByVariantNameIgnoreCaseAndProductProductNameIgnoreCase(variantName,
                productName);
    }
}
