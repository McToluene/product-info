package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantAwaitingApprovalRepository extends JpaRepository<VariantAwaitingApproval, UUID> {
    Optional<VariantAwaitingApproval> findByPublicId(UUID publicId);

    @Query(value = "select p.* from variants_awaiting_approval p where p.public_id =" +
            " :variantAwatingApprovalPublicId", nativeQuery = true)
    Optional<VariantAwaitingApproval> findByVariantAwatingApprovalPublicId(UUID variantAwatingApprovalPublicId);

    @Query(nativeQuery = true, value = "SELECT * FROM variants_awaiting_approval WHERE (LOWER(variant_name) LIKE :searchValue"
            +
            " or LOWER(sku) LIKE :searchValue)" +
            "AND (created_date BETWEEN :startDate and :endDate) " +
            "AND country_id = :countryId" +
            "AND (approval_status IS NULL OR approval_status = :approvalStatus)" +
            "AND (status IS NULL OR status = :status) ORDER BY created_date DESC")
    Page<VariantAwaitingApproval> searchVariantAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, String status, Pageable pageable);

    @Query(value = """
                select * from variants_awaiting_approval
                where (LOWER(variant_name)  like :searchParam or LOWER(variant_description)  like :searchParam)
                and (
                    id not in (
                        select variant_await_approval_id  from image_catalog where variant_await_approval_id is not null))
                and
                (created_date between :fromDate and :toDate) ORDER BY created_date DESC
            """, nativeQuery = true)
    Page<VariantAwaitingApproval> findVariantsWithMissingImages(String searchParam, LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable);

    Optional<VariantAwaitingApproval> findVariantAwaitingApprovalBySku(String sku);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update variants_awaiting_approval SET approval_status = :status where product_variant_id = :productVariantId")
    void updateApprovalStatusByProductVariantId(UUID productVariantId, String status);

    @Query(nativeQuery = true, value = """
            SELECT distinct vaa.* FROM variants_awaiting_approval vaa INNER join image_catalog imageCatalog
            on imageCatalog.variant_await_approval_id = vaa.id
             WHERE (LOWER(vaa.variant_name) LIKE :searchValue
                                 or LOWER(vaa.sku) LIKE :searchValue)
                                AND (vaa.created_date BETWEEN :startDate and :endDate)
            					AND imageCatalog.status = 'ACTIVE'
                                AND country_id = :countryId
                                AND (vaa.approval_status IS NULL OR vaa.approval_status = :approvalStatus)
                                AND (vaa.status IS NULL OR vaa.status in :status) ORDER BY vaa.created_date DESC
            """)
    Page<VariantAwaitingApproval> searchVariantAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, List<String> status, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM variants_awaiting_approval WHERE (LOWER(variant_name) LIKE :searchValue"
            +
            " or LOWER(sku) LIKE :searchValue)" +
            "AND (created_date BETWEEN :startDate and :endDate) " +
            "AND (approval_status = 'REJECTED')" +
            "AND country_id = :countryId" +
            "ORDER BY created_date DESC")
    Page<VariantAwaitingApproval> getRejectedVariantsAwaitingApproval(String searchValue,
            UUID countryId,
            LocalDateTime startDate,
            LocalDateTime endDate, Pageable pageable);

    Optional<VariantAwaitingApproval> findBySkuAndApprovalStatus(String sku, String approvalStatus);

    List<VariantAwaitingApproval> findByVariantNameIgnoreCaseAndApprovalStatusNotAndProduct(String variantName,
            String approvalStatus, Product product);

    Optional<List<VariantAwaitingApproval>> findByVariantNameIgnoreCaseAndProductProductNameIgnoreCase(
            String variantName, String productName);
}
