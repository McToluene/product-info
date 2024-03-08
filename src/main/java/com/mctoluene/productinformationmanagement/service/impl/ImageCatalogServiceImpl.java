package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.CreateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogVariantAwaitingApprovalDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageCatalogResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.ImageCatalogHelper;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.service.ImageCatalogService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ImageCatalogInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ProductVariantInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageCatalogServiceImpl implements ImageCatalogService {
    private final ImageCatalogInternalService imageCatalogInternalService;

    private final MessageSourceService messageSourceService;

    private final ProductVariantInternalService productVariantInternalService;

    private final VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    private final ImageInternalService imageInternalService;

    @Override
    public AppResponse createImageCatalog(CreateImageCatalogRequestDto requestDto) {
        List<ImageCatalog> imageCatalogList;
        if (requestDto.getImageCatalogs() == null) {
            throw new ValidatorException(messageSourceService.getMessageByKey("image.catalog.model.cannot.be.null"));
        }
        ProductVariant productVariant = productVariantInternalService
                .findByPublicId(requestDto.getProductVariantPublicId());

        String validationResponse = validateImageCatalog(requestDto, productVariant.getId());

        if (!validationResponse.isEmpty()) {
            throw new ValidatorException(validationResponse);
        }

        List<ImageCatalog> imageCatalogs = ImageCatalogHelper.buildImageCatalog(requestDto.getImageCatalogs(),
                productVariant, Status.ACTIVE.name());

        imageCatalogList = imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogs);

        List<ImageCatalogResponseDto> imageCatalogResponseDtoList = ImageCatalogHelper
                .buildImageCatalogResponseDto(imageCatalogList);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                imageCatalogResponseDtoList, null);
    }

    @Override
    public AppResponse createImageCatalogForVariantAwaitingApproval(ImageCatalogVariantAwaitingApprovalDto requestDto) {
        List<ImageCatalog> imageCatalogList;
        if (requestDto.getImageCatalogs() == null) {
            throw new ValidatorException(messageSourceService.getMessageByKey("image.catalog.model.cannot.be.null"));
        }

        VariantAwaitingApproval variantAwaitingApproval = variantAwaitingApprovalInternalService
                .findByPublicId(requestDto.getPublicVariantAwaitingApprovalId());

        String validationResponse = validateImageCatalogForVariantAwaiting(requestDto, variantAwaitingApproval.getId());

        if (!validationResponse.isEmpty()) {
            throw new ValidatorException(validationResponse);
        }

        List<ImageCatalog> imageCatalogs = ImageCatalogHelper.buildImageCatalog(requestDto.getImageCatalogs(),
                variantAwaitingApproval, Status.ACTIVE.name());

        imageCatalogList = imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogs);

        List<ImageCatalogResponseDto> imageCatalogResponseDtoList = ImageCatalogHelper
                .buildImageCatalogResponseDto(imageCatalogList);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                imageCatalogResponseDtoList, null);
    }

    @Override
    public AppResponse getImageCatalogByImageName(String imageName) {
        List<ImageCatalog> imageCatalog = imageCatalogInternalService.findByImageName(imageName);

        if (imageCatalog.isEmpty())
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("image.not.found"));

        List<ImageCatalogResponseDto> responseDto = ImageCatalogHelper.buildImageCatalogResponseDto(imageCatalog);
        log.info("Image catalog successfully retrieved {}", responseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getImageById(UUID publicId) {
        List<ImageCatalog> imageCatalogs = new ArrayList<>();
        imageCatalogs.add(imageCatalogInternalService.findByPublicId(publicId));

        List<ImageCatalogResponseDto> responseDto = (ImageCatalogHelper.buildImageCatalogResponseDto(imageCatalogs));

        log.info("Image catalog successfully retrieved {}", responseDto);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                responseDto, null);
    }

    public String validateImageCatalog(CreateImageCatalogRequestDto requestDto, UUID productId) {
        StringBuilder responseMessage = new StringBuilder();
        String message;
        for (ImageCatalogRequestDto request : requestDto.getImageCatalogs()) {
            if (!imageInternalService.checkIfNameExist(request.getImageName().trim())) {
                message = messageSourceService.getMessageByKey("image.not.uploaded.yet");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (!imageInternalService.checkIfUrlExist(request.getImageUrl().trim())) {
                message = messageSourceService.getMessageByKey("image.url.not.correct");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (!imageCatalogInternalService.checkIfNameExist(request.getImageName().trim())) {
                message = messageSourceService.getMessageByKey("image.catalog.name.already.exist");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (imageCatalogInternalService.checkForImageProductDuplicateEntry(productId, request.getImageUrl())) {
                message = messageSourceService.getMessageByKey("image.already.assigned");
                responseMessage.append(request.getImageUrl()).append(" ").append(message);
                break;
            }
        }
        return responseMessage.toString();
    }

    public String validateImageCatalogForVariantAwaiting(ImageCatalogVariantAwaitingApprovalDto requestDto,
            UUID variantAwaitingApprovalId) {
        StringBuilder responseMessage = new StringBuilder();
        String message;
        for (ImageCatalogRequestDto request : requestDto.getImageCatalogs()) {

            if (!imageInternalService.checkIfNameExist(request.getImageName().trim())) {
                message = messageSourceService.getMessageByKey("image.not.uploaded.yet");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (!imageInternalService.checkIfUrlExist(request.getImageUrl().trim())) {
                message = messageSourceService.getMessageByKey("image.url.not.correct");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (!imageCatalogInternalService.checkIfNameExist(request.getImageName().trim())) {
                message = messageSourceService.getMessageByKey("image.catalog.name.already.exist");
                responseMessage.append(request.getImageName()).append(" ").append(message);
                break;
            }
            if (imageCatalogInternalService.checkForImageVariantAwaitingApprovalDuplicateEntry(
                    variantAwaitingApprovalId, request.getImageUrl())) {
                message = messageSourceService.getMessageByKey("image.already.assigned");
                responseMessage.append(request.getImageUrl()).append(" ").append(message);
                break;
            }
        }
        return responseMessage.toString();
    }

}
