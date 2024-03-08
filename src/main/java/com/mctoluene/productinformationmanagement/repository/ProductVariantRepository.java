package com.mctoluene.productinformationmanagement.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProductIdAndStatus(UUID productId, String name);

    Optional<ProductVariant> findByVariantNameIgnoreCaseAndProduct(String variantName, Product product);

    Optional<ProductVariant> findByPublicId(UUID publicId);

    List<ProductVariant> findAllByStatusNot(String status, Pageable pageable);

    Optional<ProductVariant> findByPublicIdAndStatusNot(UUID publicId, String status);

    List<ProductVariant> findByStatusAndPublicIdIn(String status, List<UUID> publicIds);

    @Query(nativeQuery = true, value = "select * from products_variant where product_variant_id = :productVariantId and status=:status")
    List<ProductVariant> findByProductVariantIdAndStatus(UUID productVariantId, String status);

    @Query(nativeQuery = true, value = "UPDATE product_variants SET status = 'INACTIVE' where product_id IN(:listOfProducts) RETURNING *")
    List<ProductVariant> archiveAllByProductIdIn(List<Product> listOfProducts);

    List<ProductVariant> findAllByStatusIn(List<String> listOfStatus);

    List<ProductVariant> findAllByProductInAndStatusIn(List<Product> productVariants, List<String> listOfStatus);

    @Query(nativeQuery = true, value = "UPDATE product_variants SET status = :status where product_id = :product RETURNING *")
    List<ProductVariant> updateProductVariantsArchiveStatusByProduct(Product product, String status);

    @Query(nativeQuery = true, value = "SELECT * FROM product_variants pv INNER JOIN variants_version vv " +
            "ON pv.id = vv.product_variant_id " +
            "INNER JOIN products p ON vv.product_id = p.id " +
            "INNER JOIN brands b on b.id = p.brand_id " +
            "INNER JOIN manufacturers m on m.id  = p.manufacturer_id " +
            "INNER JOIN product_categories pc on pc.id = p.category_id " +
            "WHERE pv.country_id = :countryId " +
            "AND pv.status IN (:listOfVariantStatus) AND vv.status = (:versionStatus) " +
            "AND (:isVated IS NULL OR pv.vated = :isVated) " +
            "AND (LOWER(vv.variant_name) LIKE :searchValue OR LOWER(vv.sku) LIKE :searchValue " +
            "OR LOWER(p.product_name) LIKE :searchValue) " +
            "AND (vv.approved_date BETWEEN :startDate and :endDate) " +
            "AND (vv.approval_status IS NULL OR vv.approval_status = :approvalStatus) " +
            "ORDER BY vv.approved_date DESC")
    Page<ProductVariant> findVariantsEdited(String searchValue, String approvalStatus, List<String> listOfVariantStatus,
            String versionStatus, LocalDateTime startDate, LocalDateTime endDate,
            Boolean isVated, UUID countryId, Pageable pageable);

    List<ProductVariant> findByCountryId(UUID countryId, Pageable pageable);

    List<ProductVariant> getProductVariantsByPublicIdInOrSkuIn(List<UUID> productVariantIds,
            List<String> productVariantSkus);

    List<ProductVariant> findProductVariantBySkuIn(@Param("skus") List<String> skus);
}
