package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.repository.ImageCatalogRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ImageCatalogInternalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageCatalogInternalServiceImpl implements ImageCatalogInternalService {

    private final MessageSourceService messageSourceService;

    private final ImageCatalogRepository imageCatalogRepository;

    @Override
    @Transactional
    public List<ImageCatalog> saveImageCatalogsToDb(List<ImageCatalog> imageCatalogs) {
        log.info("logging request to save image catalogs to db {}", imageCatalogs);
        try {
            return imageCatalogRepository.saveAll(imageCatalogs);
        } catch (Exception e) {
            throw new UnProcessableEntityException(messageSourceService
                    .getMessageByKey("an.error.occurred.adding.image.to.catalog"));
        }
    }

    @Override
    public boolean checkIfNameExist(String name) {
        var response = imageCatalogRepository.findByImageName(name);
        return response.isEmpty();
    }

    @Override
    public boolean checkForImageProductDuplicateEntry(UUID productId, String imageUrl) {
        var response = imageCatalogRepository.findByproductVariantIdAndImageUrl(productId, imageUrl);
        return response.isPresent();
    }

    @Override
    public boolean checkForImageVariantAwaitingApprovalDuplicateEntry(UUID variantAwaitingApproval, String imageUrl) {
        var response = imageCatalogRepository.findByVariantAwaitingApprovalIdAndImageUrl(variantAwaitingApproval,
                imageUrl);
        return response.isPresent();
    }

    @Override
    @Transactional
    public void setProductVariantImageCatalogForVariantAwaitingApproval(UUID variantAwaitingApprovalId,
            UUID productVariantId) {
        imageCatalogRepository.setProductVariantImageCatalogueForAwaitingApproval(productVariantId,
                variantAwaitingApprovalId);
    }

    @Override
    public List<ImageCatalog> findByImageName(String imageName) {
        return imageCatalogRepository.findByImageName(imageName);

    }

    @Override
    public ImageCatalog findByPublicId(UUID publicId) {
        return imageCatalogRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService
                        .getMessageByKey("image.not.found")));
    }

    @Override
    public List<ImageCatalog> findByVariantAwaitingApprovalId(UUID productAwaitingApprovalPublicId) {
        return imageCatalogRepository.findByVariantAwaitingApprovalId(productAwaitingApprovalPublicId,
                Status.ACTIVE.name());
    }

    @Override
    public List<ImageCatalog> findByProductVariantId(UUID productVariantId) {
        return imageCatalogRepository.findByProductVariantId(productVariantId, Status.ACTIVE.name());
    }

    @Override
    public boolean checkForImageProductDuplicateEntry(UUID productId, String imageUrl, UUID publicId) {
        var response = imageCatalogRepository.findByProductVariantIdAndImageUrlAndPublicIdNot(productId, imageUrl,
                publicId);
        return response.isPresent();
    }

    @Override
    public ImageCatalog findByPublicIdAndProductVariantId(UUID publicId, UUID productVariantId) {
        return imageCatalogRepository.findByPublicIdAndProductVariantId(publicId, productVariantId)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService
                        .getMessageByKey("image.not.found")));
    }

    @Override
    public List<ImageCatalog> findByProductVariants(List<ProductVariant> productVariants) {
        return imageCatalogRepository.findByProductVariantInAndStatus(productVariants, Status.ACTIVE.name());
    }
}
