package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.BrandHelper;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Service(TypeMessage.BRAND)
@Slf4j
@RequiredArgsConstructor
public class BrandMessageQueueConverter implements QueueStrategyInterface {

    private final Gson gson;
    private final BrandInternalService brandService;
    private final MessageSourceService messageSourceService;
    private final ManufacturerInternalService manufacturerInternalService;

    @Override
    public void sendNotification(MessageContentEvent content) {
        var jsonBrand = gson.toJson(content.getData());
        Type listType = new TypeToken<List<CreateBrandRequestDto>>() {
        }.getType();
        List<CreateBrandRequestDto> bulkBrandUploadRequest = gson.fromJson(jsonBrand, listType);
        if (null == bulkBrandUploadRequest || bulkBrandUploadRequest.isEmpty()) {
            log.warn("Received unexpected message type: {}", content.getTypeMessage());
            throw new ValidatorException(messageSourceService.getMessageByKey("brand.upload.failed"));
        }
        log.info("reading Brand upload from queue: {}", content);
        executeProcess(bulkBrandUploadRequest);
    }

    public void executeProcess(List<CreateBrandRequestDto> bulkBrandUploadRequest) {
        var listFiltered = bulkBrandUploadRequest.stream().filter(
                brand -> !brand.getBrandName().isEmpty()).toList();

        for (CreateBrandRequestDto brand : listFiltered) {

            String brandName = StringSanitizerUtils.sanitizeInput(brand.getBrandName());
            String description = StringSanitizerUtils.sanitizeInput(brand.getDescription());

            var manufacturerEntity = manufacturerInternalService.findByPublicId(brand.getManufacturerId()).orElseThrow(
                    () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));

            if (brandService.findByBrandNameAndManufacturer(brand.getBrandName(), manufacturerEntity).isPresent()) {
                messageSourceService.getMessageByKey("brand.name.already.exist");
                continue;
            }
            brand.setBrandName(brandName.trim());
            brand.setDescription(description);
            var helperBrand = BrandHelper.buildBrandEntity(brand, manufacturerEntity);
            brandService.saveBrandToDb(helperBrand);
        }
        log.info("Brand(s) created with success ");
    }

}
