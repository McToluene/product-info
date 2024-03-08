package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantInternalService {
    List<ProductVariant> saveProductVariantToDb(List<ProductVariant> variant);

    Optional<ProductVariant> findProductVariantByNameAndProduct(String variantName, Product product);

    ProductVariant saveProductVariantToDb(ProductVariant productVariant);

    ProductVariant findByPublicId(UUID productVariantPublicId);

    List<ProductVariant> archiveAllByProductIdIn(List<Product> listOfProducts);

    Page<ProductVariant> findAllProductVariantsPageable(String searchValue, LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, List<String> listOfVariantStatus,
            Boolean isVated, UUID countryId, Pageable pageable);
}
