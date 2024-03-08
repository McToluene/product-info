package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantAwaitingApprovalInternalService {

    List<VariantAwaitingApproval> saveVariantAwaitingApprovalToDb(List<VariantAwaitingApproval> variants);

    VariantAwaitingApproval findByPublicId(UUID publicId);

    VariantAwaitingApproval saveVariantAwaitApprovalToDb(VariantAwaitingApproval variantAwaitingApproval);

    VariantAwaitingApproval findByVariantAwaitingApprovalByPublicId(UUID publicId);

    Page<VariantAwaitingApproval> searchVariantsAwaitingApproval(String searchValue, UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, String status, Pageable pageable);

    Page<VariantAwaitingApproval> getVariantsWithMissingImages(String searchParam, LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable);

    Optional<VariantAwaitingApproval> findVariantBySku(String sku);

    void setApprovalStatusByProductVariantId(UUID productVariantId, String name);

    Page<VariantAwaitingApproval> searchVariantsAwaitingApproval(String searchValue, UUID countryId,
            LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfstatus, Pageable pageable);

    Page<VariantAwaitingApproval> getRejectedVariantsAwaitingApproval(String searchValue, UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate, Pageable pageable);

    VariantAwaitingApproval findBySkuAndApprovalStatus(String sku, String approvalStatus);

    List<VariantAwaitingApproval> findByVariantNameAndApprovalStatusNotAndProduct(String variantName,
            ApprovalStatus approvalStatus, Product product);

    Optional<List<VariantAwaitingApproval>> findByVariantNameIgnoreCaseAndProductProductNameIgnoreCase(
            String variantName, String productName);
}
