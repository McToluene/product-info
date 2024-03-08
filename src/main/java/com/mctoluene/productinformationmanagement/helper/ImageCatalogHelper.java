package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.UpdateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageCatalogResponseDto;
import com.mctoluene.productinformationmanagement.model.*;

public class ImageCatalogHelper {

    ImageCatalogHelper() {

    }

    public static List<ImageCatalog> buildImageCatalog(List<ImageCatalogRequestDto> requestDto,
            ProductVariant productVariant, String status) {
        List<ImageCatalog> imageCatalogList = new ArrayList<>();

        for (ImageCatalogRequestDto request : requestDto) {
            ImageCatalog imageCatalog = new ImageCatalog();
            imageCatalog.setPublicId(UUID.randomUUID());
            imageCatalog.setImageName(request.getImageName().trim());
            imageCatalog.setProductVariant(productVariant);
            imageCatalog.setImageDescription(request.getImageDescription());
            imageCatalog.setImageUrl(request.getImageUrl());
            imageCatalog.setStatus(status);
            imageCatalog.setCreatedBy(request.getCreatedBy());
            imageCatalog.setLastModifiedBy(request.getCreatedBy());
            imageCatalog.setCreatedDate(LocalDateTime.now());
            imageCatalog.setLastModifiedDate(LocalDateTime.now());
            imageCatalog.setVersion(BigInteger.ZERO);

            imageCatalogList.add(imageCatalog);

        }
        return imageCatalogList;
    }

    public static ImageCatalog buildImageCatalog(UpdateImageCatalogRequestDto requestDto, ProductVariant productVariant,
            String status) {
        ImageCatalog imageCatalog = new ImageCatalog();
        imageCatalog.setPublicId(UUID.randomUUID());
        imageCatalog.setImageName(requestDto.getImageName().trim());
        imageCatalog.setProductVariant(productVariant);
        imageCatalog.setImageDescription(requestDto.getImageDescription());
        imageCatalog.setImageUrl(requestDto.getImageUrl());
        imageCatalog.setStatus(status);
        imageCatalog.setCreatedBy(requestDto.getModifiedBy());
        imageCatalog.setLastModifiedBy(requestDto.getModifiedBy());
        imageCatalog.setCreatedDate(LocalDateTime.now());
        imageCatalog.setLastModifiedDate(LocalDateTime.now());
        imageCatalog.setVersion(BigInteger.ZERO);
        return imageCatalog;
    }

    public static List<ImageCatalog> buildImageCatalog(List<ImageCatalogRequestDto> requestDto,
            VariantAwaitingApproval variantAwaitingApproval, String status) {
        List<ImageCatalog> imageCatalogList = new ArrayList<>();

        for (ImageCatalogRequestDto request : requestDto) {
            ImageCatalog imageCatalog = new ImageCatalog();
            imageCatalog.setPublicId(UUID.randomUUID());
            imageCatalog.setImageName(request.getImageName().trim());
            imageCatalog.setVariantAwaitingApproval(variantAwaitingApproval);
            imageCatalog.setImageDescription(request.getImageDescription());
            imageCatalog.setImageUrl(request.getImageUrl());
            imageCatalog.setStatus(status);
            imageCatalog.setCreatedBy(request.getCreatedBy());
            imageCatalog.setLastModifiedBy(request.getCreatedBy());
            imageCatalog.setCreatedDate(LocalDateTime.now());
            imageCatalog.setLastModifiedDate(LocalDateTime.now());
            imageCatalog.setVersion(BigInteger.ZERO);
            imageCatalogList.add(imageCatalog);
        }
        return imageCatalogList;
    }

    public static List<ImageCatalog> buildImageCatalogAwaitingApproval(List<ImageCatalogRequestDto> requestDto,
            VariantAwaitingApproval variantAwaitingApproval, String status) {
        List<ImageCatalog> imageCatalogList = new ArrayList<>();

        for (ImageCatalogRequestDto request : requestDto) {
            ImageCatalog imageCatalog = new ImageCatalog();
            imageCatalog.setPublicId(UUID.randomUUID());
            imageCatalog.setImageName(request.getImageName().trim());
            imageCatalog.setVariantAwaitingApproval(variantAwaitingApproval);
            imageCatalog.setImageDescription(request.getImageDescription());
            imageCatalog.setImageUrl(request.getImageUrl());
            imageCatalog.setStatus(status);
            imageCatalog.setCreatedBy(request.getCreatedBy());
            imageCatalog.setLastModifiedBy(request.getCreatedBy());
            imageCatalog.setCreatedDate(LocalDateTime.now());
            imageCatalog.setLastModifiedDate(LocalDateTime.now());
            imageCatalog.setVersion(BigInteger.ZERO);
            imageCatalogList.add(imageCatalog);
        }
        return imageCatalogList;
    }

    public static List<ImageCatalogResponseDto> buildImageCatalogResponseDto(List<ImageCatalog> imageCatalogs) {
        List<ImageCatalogResponseDto> imageCatalogResponseList = new ArrayList<>();

        for (ImageCatalog response : imageCatalogs) {
            ImageCatalogResponseDto responseDto = ImageCatalogResponseDto.builder()
                    .publicId(response.getPublicId())
                    .productVariantPublicId(
                            Optional.ofNullable(response.getProductVariant()).map(BaseEntity::getPublicId).orElse(null))
                    .imageName(response.getImageName().trim())
                    .imageDescription(response.getImageDescription())
                    .imageUrl(response.getImageUrl())
                    .createdDate(response.getCreatedDate())
                    .createdBy(response.getCreatedBy())
                    .lastModifiedBy(response.getLastModifiedBy())
                    .lastModifiedDate(response.getLastModifiedDate())
                    .status(response.getStatus())
                    .version(response.getVersion())
                    .build();

            imageCatalogResponseList.add(responseDto);

        }
        return imageCatalogResponseList;
    }

    public static ImageCatalog buildImageCatalog(String imageUrl, String createdBy,
            VariantAwaitingApproval variantAwaitingApproval) {

        ImageCatalog imageCatalog = new ImageCatalog();
        imageCatalog.setPublicId(UUID.randomUUID());
        imageCatalog.setImageUrl(imageUrl);
        imageCatalog.setImageName(getImageNameFromImageUrl(imageUrl));
        imageCatalog.setStatus(Status.ACTIVE.name());
        imageCatalog.setCreatedBy(createdBy);
        imageCatalog.setCreatedDate(LocalDateTime.now());
        imageCatalog.setLastModifiedBy(createdBy);
        imageCatalog.setLastModifiedDate(LocalDateTime.now());
        imageCatalog.setVariantAwaitingApproval(variantAwaitingApproval);
        imageCatalog.setVersion(BigInteger.ZERO);

        return imageCatalog;
    }

    public static String getImageNameFromImageUrl(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf('/');
        int lastDotIndex = imageUrl.lastIndexOf('.');
        return imageUrl.substring(lastSlashIndex + 1, lastDotIndex);
    }

}
