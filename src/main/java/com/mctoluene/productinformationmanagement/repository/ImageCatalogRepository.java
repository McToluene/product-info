package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageCatalogRepository extends JpaRepository<ImageCatalog, UUID> {
    Optional<ImageCatalog> findByPublicId(UUID publicId);

    @Query(value = "select ic.* from image_catalog ic where upper(ic.image_catalog_image_name) = upper(:name) ", nativeQuery = true)
    List<ImageCatalog> findByImageName(String name);

    @Query(value = "select ic.* from image_catalog ic where ic.product_variant_id = :productVariantId and ic.image_url=:imageUrl", nativeQuery = true)
    Optional<ImageCatalog> findByproductVariantIdAndImageUrl(UUID productVariantId, String imageUrl);

    @Query(value = "select ic.* from image_catalog ic where ic.variant_await_approval_id = :variantAwaitApprovalId " +
            "and ic.image_url=:imageUrl", nativeQuery = true)
    Optional<ImageCatalog> findByVariantAwaitingApprovalIdAndImageUrl(UUID variantAwaitApprovalId, String imageUrl);

    @Query(value = "select v.* from image_catalog v where v.variant_await_approval_id =:variantAwaitingApprovalId " +
            "and v.status = :status", nativeQuery = true)
    List<ImageCatalog> findByVariantAwaitingApprovalId(UUID variantAwaitingApprovalId, String status);

    @Query(value = "select v.* from image_catalog v where v.product_variant_id =:productVariantId and v.status = :status", nativeQuery = true)
    List<ImageCatalog> findByProductVariantId(UUID productVariantId, String status);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update image_catalog SET product_variant_id = :productVariantId where " +
            "variant_await_approval_id = :variantAwaitingApprovalId")
    void setProductVariantImageCatalogueForAwaitingApproval(UUID productVariantId, UUID variantAwaitingApprovalId);

    @Query(value = "select ic.* from image_catalog ic where ic.product_variant_id = :productVariantId " +
            "and ic.image_url=:imageUrl and ic.public_id != :publicId", nativeQuery = true)
    Optional<ImageCatalog> findByProductVariantIdAndImageUrlAndPublicIdNot(UUID productVariantId, String imageUrl,
            UUID publicId);

    @Query(value = "select ic.* from image_catalog ic where upper(ic.image_catalog_image_name) = upper(:imageName) " +
            "and public_id != :publicId", nativeQuery = true)
    Optional<ImageCatalog> findByImageNameAndPublicIdNot(String imageName, UUID publicId);

    Optional<ImageCatalog> findByPublicIdAndProductVariantId(UUID publicId, UUID productVariantId);

    @Modifying
    @Query(value = "update image_catalog set status = ?1 where image_catalog_image_name = ?2", nativeQuery = true)
    void deleteByImageName(String status, String imageName);

    List<ImageCatalog> findByProductVariantInAndStatus(List<ProductVariant> productVariants, String status);

}
