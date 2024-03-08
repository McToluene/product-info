package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantVersion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface VariantVersionRepository extends JpaRepository<VariantVersion, UUID> {
    List<VariantVersion> findVariantVersionById(UUID id);

    @Query(nativeQuery = true, value = " SELECT vv.* FROM variants_version vv " +
            "LEFT JOIN products p ON vv.product_id = p.id " +
            "WHERE (LOWER(vv.variant_name) LIKE :searchValue OR LOWER(vv.sku) " +
            "LIKE :searchValue OR LOWER(p.product_name) LIKE :searchValue) " +
            "AND (vv.approved_date BETWEEN :startDate and :endDate) " +
            "AND (vv.approval_status IS NULL OR vv.approval_status = :approvalStatus) " +
            "AND (vv.status IN (:status)) " +
            "AND (vv.product_variant_id IN (:productVariants)) " +
            "ORDER BY vv.approved_date DESC")
    Page<VariantVersion> findAllByStatusAndProductVariantIn(String searchValue, LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, String status, List<ProductVariant> productVariants,
            Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM product_variants pv INNER JOIN variants_version vv " +
            "ON pv.id = vv.product_variant_id " +
            "INNER JOIN products p ON vv.product_id = p.id " +
            "INNER JOIN brands b on b.id = p.brand_id " +
            "INNER JOIN manufacturers m on m.id  = p.manufacturer_id " +
            "INNER JOIN product_categories pc on pc.id = p.category_id " +
            "WHERE pv.status IN (:listOfVariantStatus) AND vv.status = (:versionStatus) " +
            "AND (LOWER(vv.variant_name) LIKE :searchValue OR LOWER(vv.sku) LIKE :searchValue " +
            "OR LOWER(p.product_name) LIKE :searchValue) " +
            "AND (vv.approved_date BETWEEN :startDate and :endDate) " +
            "AND (vv.approval_status IS NULL OR vv.approval_status = :approvalStatus) " +
            "ORDER BY vv.approved_date DESC")
    Page<VariantVersion> findVariantsEdited(String searchValue, String approvalStatus, List<String> listOfVariantStatus,
            String versionStatus, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<VariantVersion> findAllByStatusAndProductVariantIn(String status, List<ProductVariant> productVariants);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "JOIN brands b ON b.id = p.brand_id " +
            "WHERE (LOWER(v.sku) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(p.product_name) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(b.brand_name) LIKE CONCAT('%', LOWER(:searchValue), '%')) " +
            "AND v.status = 'ACTIVE'", nativeQuery = true)
    Page<VariantVersion> searchVariant(@Param("searchValue") String searchValue, Pageable pageable);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "JOIN brands b ON b.id = p.brand_id " +
            "WHERE (LOWER(v.sku) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(p.product_name) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(b.brand_name) LIKE CONCAT('%', LOWER(:searchValue), '%')) " +
            "AND v.status = 'ACTIVE' AND (product_variant_id IN (:productVariantIds))", nativeQuery = true)
    List<VariantVersion> searchVariantVersionByProductVariantsIn(@Param("searchValue") String searchValue,
            List<UUID> productVariantIds);

    Optional<VariantVersion> findByStatusAndProductVariant(String status, ProductVariant productVariant);

    @Query(nativeQuery = true, value = "select * from variants_version where product_variant_id = :productVariantId and status = 'ACTIVE'")
    Optional<VariantVersion> findActiveByProductVariantId(UUID productVariantId);

    @Query(nativeQuery = true, value = "select * from variants_version where product_variant_id = :productVariantId")
    Optional<VariantVersion> findByProductVariantId(UUID productVariantId);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update variants_version SET status = :status where product_variant_id = :productVariantId")
    void updateVersionAndStatusByProductVariantId(UUID productVariantId, @Param("status") String status);

    Optional<VariantVersion> findBySkuAndStatus(String sku, String status);

    @Query(value = "select * from variants_version where sku IN (:skuList) and status='ACTIVE'", nativeQuery = true)
    List<VariantVersion> findAllBySku(List<String> skuList);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "JOIN brands b ON b.id = p.brand_id " +
            "JOIN product_variants pv ON pv.product_id = v.product_id " +
            "JOIN product_categories pc ON pc.id = p.category_id " +
            "WHERE (LOWER(v.sku) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(p.product_name) LIKE CONCAT('%', LOWER(:searchValue), '%') " +
            "OR LOWER(b.brand_name) LIKE CONCAT('%', LOWER(:searchValue), '%')) " +
            "AND ( COALESCE(:categoryPublicIds) IS NULL OR pc.public_id IN (:categoryPublicIds) )" +
            "AND v.status ='ACTIVE' " +
            "AND pv.status IN (:status) AND pv.public_id IN (:variantPublicIds)", nativeQuery = true)
    List<VariantVersion> findByPublicIdInAndStatusAndFilter(List<UUID> variantPublicIds, List<String> status,
            String searchValue, List<UUID> categoryPublicIds);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "JOIN product_categories pc ON pc.id = p.category_id " +
            "where pc.public_id IN (:categoryPublicIds)" +
            "AND v.status ='ACTIVE' ", nativeQuery = true)
    List<VariantVersion> findByCategoryPublicIds(Set<UUID> categoryPublicIds);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "JOIN product_categories pc ON pc.id = p.category_id " +
            "where pc.id =:categoryPublicId" +
            "AND v.status ='ACTIVE' ", nativeQuery = true)
    Page<VariantVersion> findByCategoryId(UUID categoryPublicId, Pageable pageable);

    Optional<VariantVersion> findByVariantTypeIdAndStatus(UUID variantTypeId, String status);

    Optional<VariantVersion> findByProductIdAndStatus(UUID variantTypeId, String status);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN products p ON v.product_id = p.id " +
            "where v.sku In (:skuList) and v.status='ACTIVE'" +
            "AND LOWER(p.product_name) LIKE CONCAT('%', LOWER(:searchValue), '%')", nativeQuery = true)
    Page<VariantVersion> findBySkuListAndProductName(List<String> skuList, String searchValue, Pageable pageable);

    Optional<VariantVersion> findByProductVariantIdAndStatus(UUID productVariantId, String status);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN product_variants pv ON v.product_variant_id = pv.id " +
            "where v.sku In (:skuList) and v.status='ACTIVE' and pv.status='ACTIVE'", nativeQuery = true)
    List<VariantVersion> findBySkuList(List<String> skuList);

    @Query(value = "SELECT v.* FROM variants_version v " +
            "JOIN product_variants pv ON v.product_variant_id = pv.id " +
            "WHERE v.sku LIKE :sku AND v.status='ACTIVE' AND pv.status='ACTIVE'", nativeQuery = true)
    Optional<VariantVersion> findBySku(String sku);

}
