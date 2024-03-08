package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.model.VariantVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantVersionInternalService {
    VariantVersion saveVariantVersionToDb(VariantVersion requestDto);

    List<VariantVersion> saveVariantVersionsToDb(List<VariantVersion> variantVersions);

    List<VariantVersion> getProductInUse(UUID id);

    VariantVersion findByProductVariantId(UUID productVariantId);

    VariantVersion findMostRecentVariantVersion(UUID productVariantId);

    void setVersionToStatusByProductVariantId(UUID productVariantId, String status);

    VariantVersion findBySku(String sku);

    List<VariantVersion> findAllBySkuIn(List<String> variantSkuList);

    List<VariantVersion> searchVariantVersionByProductVariantsIn(String searchParam, List<UUID> toList);

    Optional<VariantVersion> findByVariantTypeId(UUID id);

    Optional<VariantVersion> findByProductId(UUID id);

    Page<VariantVersion> searchVariantBySkuListAndProductName(List<String> skuList, String searchValue,
            Pageable pageable);

    Page<VariantVersion> findVariantsByProductCategoryId(UUID productCategoryId, Pageable pageable);

    VariantVersion findByProductVariantIdAndStatus(UUID productVariantId, String status);

    List<VariantVersion> findVariantBySkuList(List<String> skuList);

    VariantVersion findVariantBySku(String sku);

}
