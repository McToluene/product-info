package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BulkProductUploadRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.CategoryUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service(TypeMessage.PRODUCT)
@Slf4j
@RequiredArgsConstructor
public class ProductMessageQueueConverter implements QueueStrategyInterface {

    private ProductService productService;
    private final Gson gson;
    private final MessageSourceService messageSourceService;

    @Autowired
    public void setProductService(@Lazy ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void sendNotification(MessageContentEvent content) {
        var bulkProductUploadRequestJson = gson.toJson(content.getData());
        BulkProductUploadRequest bulkProductUploadRequest = gson.fromJson(bulkProductUploadRequestJson,
                BulkProductUploadRequest.class);
        if (bulkProductUploadRequest != null) {
            log.info("reading from queue: {}", content);
            log.info("Bulk Product upload Request {} ", bulkProductUploadRequest);
            readMessage(bulkProductUploadRequest);
        } else {
            log.warn("Received unexpected message type: {}", content.getTypeMessage());
        }
    }

    private void readMessage(BulkProductUploadRequest bulkProductVariantRequest) {
        log.info("bulk upload product request processing {}", bulkProductVariantRequest);
        try {
            if (CollectionUtils.isEmpty(bulkProductVariantRequest.getImageUploadTemplateRequests()) &&
                    CollectionUtils.isEmpty(bulkProductVariantRequest.getPriceTemplateRequests()) &&
                    CollectionUtils.isEmpty(bulkProductVariantRequest.getStockUpdateTemplateRequests()) &&
                    CollectionUtils.isEmpty(bulkProductVariantRequest.getCategoryUploadTemplateRequests())) {

                throw new ValidatorException("bulk.upload.request.cannot.be.empty");
            }
            if (!CollectionUtils.isEmpty(bulkProductVariantRequest.getImageUploadTemplateRequests())) {
                try {
                    productService.saveUploadProductVariants(bulkProductVariantRequest.getImageUploadTemplateRequests(),
                            bulkProductVariantRequest.getCreatedBy(), bulkProductVariantRequest.getCountryId());
                } catch (Exception e) {
                    log.error("error while saving imageUploadTemplateRequest {}", e);
                }
            }
            if (!CollectionUtils.isEmpty(bulkProductVariantRequest.getPriceTemplateRequests())) {
                try {
                    productService.savePriceTemplateRequest(bulkProductVariantRequest.getPriceTemplateRequests(),
                            bulkProductVariantRequest.getCreatedBy());
                } catch (Exception e) {
                    log.error("error while saving priceTemplateRequest {}", e);
                }
            }
            if (!CollectionUtils.isEmpty(bulkProductVariantRequest.getStockUpdateTemplateRequests())) {
                try {
                    productService.saveStockUpdateTemplateRequest(
                            bulkProductVariantRequest.getStockUpdateTemplateRequests(),
                            bulkProductVariantRequest.getCreatedBy());
                } catch (Exception e) {
                    log.error("error while saving stockUpdateTemplateRequest {}", e);
                }
            }
            if (!CollectionUtils.isEmpty(bulkProductVariantRequest.getCategoryUploadTemplateRequests())) {

                saveCategory(bulkProductVariantRequest.getCategoryUploadTemplateRequests(),
                        bulkProductVariantRequest.getCreatedBy());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("cannot.process.queue.message"));
        }
    }

    private void saveCategory(Map<String, List<CategoryUploadTemplateRequest>> map, String createdBy) {
        for (Map.Entry<String, List<CategoryUploadTemplateRequest>> entry : map.entrySet()) {
            try {
                productService.saveCategoryUploadTemplateRequest(entry.getValue(), createdBy);
            } catch (Exception e) {
                log.error("error while saving categoryUploadTemplateRequest {}", e);
            }
        }
    }
}
