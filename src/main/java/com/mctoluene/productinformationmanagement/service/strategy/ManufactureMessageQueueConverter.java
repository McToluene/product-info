package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ManufactureUploadRequest;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.ManufacturerHelper;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Service(TypeMessage.MANUFACTURE)
@Slf4j
@RequiredArgsConstructor
public class ManufactureMessageQueueConverter implements QueueStrategyInterface {

    private final Gson gson;

    private final MessageSourceService messageSourceService;
    private final ManufacturerInternalService manufacturerInternalService;

    @Override
    public void sendNotification(MessageContentEvent content) {
        var jsonBrand = gson.toJson(content.getData());
        Type listType = new TypeToken<List<ManufactureUploadRequest>>() {
        }.getType();
        List<ManufactureUploadRequest> manufactureUploadRequests = gson.fromJson(jsonBrand, listType);
        if (null == manufactureUploadRequests || manufactureUploadRequests.isEmpty()) {
            log.warn("Received unexpected message type: {}", content.getTypeMessage());
            throw new ValidatorException(messageSourceService.getMessageByKey("manufacturer.upload.failed"));
        }
        log.info("reading manufacturer upload from queue: {}", content);
        executeProccess(manufactureUploadRequests);
    }

    public void executeProccess(List<ManufactureUploadRequest> manufactureUploadRequests) {
        var listFiltered = manufactureUploadRequests.stream().filter(
                manufacturerRequestDto -> !manufacturerRequestDto.getManufacturerName().isEmpty()).toList();

        for (ManufactureUploadRequest manufacturerRequestDto : listFiltered) {
            String manufacturerName = StringSanitizerUtils.sanitizeInput(manufacturerRequestDto.getManufacturerName());
            String description = StringSanitizerUtils.sanitizeInput(manufacturerRequestDto.getDescription());
            var manufacturerOptional = manufacturerInternalService.findByManufacturerName(manufacturerName.trim());
            if (manufacturerOptional.isPresent()) {
                logMessage(messageSourceService.getMessageByKey("manufacturer.already.exists"));
                continue;
            }
            manufacturerRequestDto.setManufacturerName(manufacturerName.trim());
            manufacturerRequestDto.setDescription(description.trim());
            var manufacturer = ManufacturerHelper.buildManufacturerEntity(manufacturerRequestDto);
            manufacturerInternalService.saveNewManufacturerToDb(manufacturer);
        }
        logMessage("Manufacturer(s) created with success ");
    }

    public void logMessage(String message) {
        log.info(message);
    }

}
