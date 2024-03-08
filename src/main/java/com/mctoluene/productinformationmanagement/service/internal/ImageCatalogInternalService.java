package com.mctoluene.productinformationmanagement.service.internal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

public interface ImageCatalogInternalService {
    List<ImageCatalog> saveImageCatalogsToDb(List<ImageCatalog> imageCatalogs);

    boolean checkIfNameExist(String name);

    boolean checkForImageProductDuplicateEntry(UUID productId, String imageUrl);

    boolean checkForImageVariantAwaitingApprovalDuplicateEntry(UUID variantAwaitingApproval, String imageUrl);

    void setProductVariantImageCatalogForVariantAwaitingApproval(UUID variantAwaitingApprovalId, UUID productVariantId);

    List<ImageCatalog> findByImageName(String imageName);

    ImageCatalog findByPublicId(UUID publicId);

    List<ImageCatalog> findByVariantAwaitingApprovalId(UUID productAwaitingApprovalId);

    List<ImageCatalog> findByProductVariantId(UUID productId);

    boolean checkForImageProductDuplicateEntry(UUID productId, String imageUrl, UUID publicId);

    ImageCatalog findByPublicIdAndProductVariantId(UUID imageCatalogPublicId, UUID productVariantId);

    List<ImageCatalog> findByProductVariants(List<ProductVariant> productVariants);

}
