package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ProductVariantQueueMessage;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.VariantAwaitingApprovalValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service(TypeMessage.VARIANT_ONLY)
@Slf4j
@RequiredArgsConstructor
public class ProductVariantModelMessageQueueConverter implements QueueStrategyInterface {

    private final Gson gson;

    private final MessageSourceService messageSourceService;

    private final VariantAwaitingApprovalValidationService validationService;

    @Override
    public void sendNotification(MessageContentEvent content) {
        var bulkProductUploadRequestJson = gson.toJson(content.getData());
        ProductVariantQueueMessage uploadTemplateRequest = gson.fromJson(bulkProductUploadRequestJson,
                ProductVariantQueueMessage.class);
        if (uploadTemplateRequest != null) {
            log.info("reading from queue: {}", content);
            log.info("Bulk Product upload Request {} ", uploadTemplateRequest);
            readMessage(uploadTemplateRequest);
        } else {
            log.warn("Received unexpected message type: {}", content.getTypeMessage());
        }
    }

    private void readMessage(ProductVariantQueueMessage uploadTemplateRequest) {
        log.info("message from queue {}", new GsonBuilder().setPrettyPrinting().create().toJson(uploadTemplateRequest));

        try {
            if (!CollectionUtils.isEmpty(uploadTemplateRequest.getUploadTemplateRequestList())) {
                validationService.validateDataFromUploadAndCreateModel(uploadTemplateRequest,
                        uploadTemplateRequest.getCreatedBy(),
                        uploadTemplateRequest.getCountryId());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("cannot.process.queue.message"));
        }
    }

}
