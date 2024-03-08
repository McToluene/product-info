package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.CreateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogVariantAwaitingApprovalDto;
import com.mctoluene.commons.response.AppResponse;

import java.util.UUID;

public interface ImageCatalogService {
    AppResponse createImageCatalog(CreateImageCatalogRequestDto requestDto);

    AppResponse createImageCatalogForVariantAwaitingApproval(ImageCatalogVariantAwaitingApprovalDto requestDto);

    AppResponse getImageCatalogByImageName(String imageName);

    AppResponse getImageById(UUID publicId);
}
