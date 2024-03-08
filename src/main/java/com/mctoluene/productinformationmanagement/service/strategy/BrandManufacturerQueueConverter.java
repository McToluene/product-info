package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ManufactureUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.brand.BrandManufacturerRequest;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.BrandHelper;
import com.mctoluene.productinformationmanagement.helper.ManufacturerHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(TypeMessage.BRAND_MANUFACTURER)
@Slf4j
@RequiredArgsConstructor
public class BrandManufacturerQueueConverter implements QueueStrategyInterface {

    private final Gson gson;
    private final BrandInternalService brandService;
    private final MessageSourceService messageSourceService;
    private final ManufacturerInternalService manufacturerInternalService;

    @Override
    public void sendNotification(MessageContentEvent content) {

        var jsonBrand = gson.toJson(content.getData());
        Type listType = new TypeToken<List<BrandManufacturerRequest>>() {
        }.getType();
        List<BrandManufacturerRequest> bulkBrandUploadRequest = gson.fromJson(jsonBrand, listType);
        if (null == bulkBrandUploadRequest || bulkBrandUploadRequest.isEmpty()) {
            log.warn("Received unexpected message type: {}", content.getTypeMessage());
            throw new ValidatorException(messageSourceService.getMessageByKey("brand.upload.failed"));
        }
        log.info("reading Brand Manufacturer upload from queue: {}", content);
        executeProcess(bulkBrandUploadRequest);
    }

    private void executeProcess(List<BrandManufacturerRequest> bulkBrandUploadRequest) {
        Map<String, List<BrandManufacturerRequest>> groupedByManufacturer = bulkBrandUploadRequest.stream()
                .collect(Collectors.groupingBy(BrandManufacturerRequest::getManufacturerName));

        groupedByManufacturer.forEach(this::processGroupedByManufacturer);

        log.info("brand manufacturer processing completed ...");
    }

    private void processGroupedByManufacturer(String manufacturerName, List<BrandManufacturerRequest> brandRequests) {
        Manufacturer manufacturer = manufacturerInternalService.findByManufacturerName(manufacturerName)
                .orElseGet(() -> createAndSaveManufacturer(brandRequests.get(0)));

        processNewBrand(brandRequests, manufacturer);
    }

    private Manufacturer createAndSaveManufacturer(BrandManufacturerRequest brandManufacturerRequest) {
        Manufacturer manufacturer = ManufacturerHelper.buildManufacturerEntity(ManufactureUploadRequest.builder()
                .createdBy(brandManufacturerRequest.getCreatedBy())
                .manufacturerName(
                        StringSanitizerUtils.sanitizeInput(brandManufacturerRequest.getManufacturerName()).trim())
                .description(brandManufacturerRequest.getManufacturerDescription().trim())
                .build());

        return manufacturerInternalService.saveNewManufacturerToDb(manufacturer);
    }

    private void processNewBrand(List<BrandManufacturerRequest> brandRequests, Manufacturer manufacturer) {
        brandRequests.forEach(
                brandRequest -> brandService.findByBrandNameAndManufacturer(brandRequest.getBrandName(), manufacturer)
                        .ifPresentOrElse(
                                brand -> log.info("Brand {} already linked to manufacturer {} ", brand.getBrandName(),
                                        manufacturer.getManufacturerName()),
                                () -> createAndSaveBrand(brandRequest, manufacturer)));
    }

    private void createAndSaveBrand(BrandManufacturerRequest brandRequest, Manufacturer manufacturer) {
        Brand brand = BrandHelper.buildBrandEntity(CreateBrandRequestDto.builder()
                .manufacturerId(manufacturer.getId())
                .brandName(StringSanitizerUtils.sanitizeInput(brandRequest.getBrandName()))
                .createdBy(brandRequest.getCreatedBy())
                .description(brandRequest.getBrandDescription().trim())
                .status(Status.ACTIVE)
                .build(), manufacturer);

        brand = brandService.saveBrandToDb(brand);
        log.info("Brand {} saved successfully with id {} ", brand.getBrandName(), brand.getId());
    }
}
