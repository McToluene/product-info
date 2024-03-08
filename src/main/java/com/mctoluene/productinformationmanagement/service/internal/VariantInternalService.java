package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantVersion;

import java.time.LocalDateTime;
import java.util.*;

public interface VariantInternalService {

    Page<VariantVersion> searchVariants(String searchValue, Pageable pageable);

    Page<VariantVersion> findAllVariants(String searchValue, LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfStatus, Pageable pageable);

    Page<VariantVersion> findAllVariantsPageable(String searchValue, LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfStatus, Pageable pageable);

    List<VariantVersion> findAllByStatusAndProductVariantIn(UUID productId);

    List<ProductVariant> findProductVariantsByPublicIds(List<UUID> variantPublicIdList);

    List<ProductVariant> findAllByProductId(UUID productId);

    ProductVariant saveProductVariantToDb(ProductVariant productVariant);

    ProductVariant deleteProductVariant(ProductVariant productVariant);

    Optional<ProductVariant> findProductVariantByPublicId(UUID publicId);

    Optional<ProductVariant> findByPublicIdAndStatusNot(UUID publicId, String status);

    Optional<VariantVersion> findVariantByProductVariant(ProductVariant productVariant);

    Page<VariantVersion> findAllVariantsByCategoryPublicIds(String searchValue, List<ProductCategory> productCategories,
            LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfStatus, Pageable pageable);

    List<VariantVersion> findProductVariantsByPublicIdsAndStatusAndFilter(List<UUID> publicIds, List<String> status,
            String searchValue, List<UUID> categoryPublicIds);

    List<VariantVersion> findByCategoryPublicIds(Set<UUID> categoryPublicIds);

    List<ProductVariant> updateProductVariantsArchiveStatus(Product product, String status);

    List<ProductVariant> getAllProductVariants();

    Map<String, ProductVariantVatResponseDto> getProductVariantVatValueRatio(List<UUID> productVariantPublicIds,
            List<String> productVariantSKUs);

    List<ProductVariant> findVariantPublicIdBySkus(List<String> skus);
}
