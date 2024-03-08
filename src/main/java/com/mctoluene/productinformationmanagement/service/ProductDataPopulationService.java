package com.mctoluene.productinformationmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.internal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductDataPopulationService {

    private final BrandInternalService brandInternalService;
    private final ManufacturerInternalService manufacturerInternalService;
    private final ProductCategoryInternalService productCategoryInternalService;
    private final MeasuringUnitInternalService measuringUnitInternalService;

    public List<CreateProductRequestDto> buildProductModelFromUploadedData(
            List<ImageUploadTemplateRequest> imageUploadTemplateRequests,
            String createdBy) {

        List<CreateProductRequestDto> productRequestList = new ArrayList<>();

        imageUploadTemplateRequests.forEach(request -> {

            Brand brand = getBrandData(request.getBrand());
            Manufacturer manufacturer = getManufacturerData(brand.getManufacturer().getManufacturerName());
            ProductCategory productCategory = getSubCategoryData(request.getProductCategory());
            MeasuringUnit measuringUnit = getMeasurementUnitData(
                    request.getMeasurementUnit().isBlank() ? "KG" : request.getMeasurementUnit());

            CreateProductRequestDto productRequest = CreateProductRequestDto.builder()
                    .productName(request.getProductName())
                    .categoryPublicId(productCategory.getPublicId())
                    .brandPublicId(brand.getPublicId())
                    .manufacturerPublicId(manufacturer.getPublicId())
                    .measurementUnitPublicId(measuringUnit.getPublicId())
                    .createdBy(createdBy)
                    .productListings(Set.of(ProductListing.MERCHBUY))
                    .build();

            productRequestList.add(productRequest);

        });

        return productRequestList;
    }

    private Brand getBrandData(String brandName) {
        return brandInternalService.findByBrandName(brandName)
                .orElseThrow(() -> new ModelNotFoundException("brand name not found"));
    }

    private Manufacturer getManufacturerData(String manufacturerName) {
        return manufacturerInternalService.findByManufacturerName(manufacturerName)
                .orElseThrow(() -> new ModelNotFoundException("manufacturer name not found"));
    }

    private ProductCategory getSubCategoryData(String subcategory) {
        return productCategoryInternalService.findProductCategoryByName(subcategory);
    }

    private MeasuringUnit getMeasurementUnitData(String measurementUnit) {
        return measuringUnitInternalService.findByName(measurementUnit);
    }
}
