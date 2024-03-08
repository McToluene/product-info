package com.mctoluene.productinformationmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ProductVariantQueueMessage;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalInternalService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantAwaitingApprovalValidationService {

    private final VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    private final ProductInternalService productInternalService;

    private ProductService productService;

    @Autowired
    public void setProductService(@Lazy ProductService productService) {
        this.productService = productService;
    }

    public void validateDataFromUploadAndCreateModel(ProductVariantQueueMessage queueMessage, String createdBy,
            UUID countryId) {
        log.info("logging requests for image upload {}", queueMessage);

        Product product = productInternalService.findByPublicId(queueMessage.getProductPublicId());
        Brand brand = product.getBrand();
        Manufacturer manufacturer = product.getBrand().getManufacturer();
        ProductCategory productCategory = product.getProductCategory();

        if (queueMessage.getUploadTemplateRequestList() == null)
            throw new ValidatorException("Payload for variant creation is not valid");

        List<ImageUploadTemplateRequest> imageUploadTemplateRequestList = queueMessage.getUploadTemplateRequestList()
                .stream().map(a -> ImageUploadTemplateRequest.builder()
                        .productCategory(productCategory.getProductCategoryName())
                        .brand(brand.getBrandName())
                        .manufacturerName(manufacturer.getManufacturerName())
                        .productName(product.getProductName())
                        .imageUrl1(a.getImageUrl1())
                        .imageUrl2(a.getImageUrl2())
                        .variantName(a.getVariantName())
                        .variantType(a.getVariantType())
                        .costPrice(BigDecimal.ZERO)
                        .measurementUnit(a.getMeasurementUnit())
                        .weight(a.getWeight())
                        .vated(product.getVated())
                        .build())
                .toList();
        try {
            productService.saveUploadProductVariants(imageUploadTemplateRequestList, createdBy, countryId, product);

        } catch (Exception e) {
            log.info("Error occurred while processing request ", e);
        }

    }

}
