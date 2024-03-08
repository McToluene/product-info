package com.mctoluene.productinformationmanagement.service.strategy;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BulkProductUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryService;
import com.mctoluene.productinformationmanagement.service.ProductDataPopulationService;
import com.mctoluene.productinformationmanagement.service.ProductService;
import com.mctoluene.productinformationmanagement.service.internal.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service(TypeMessage.PRODUCT_ONLY)
@Slf4j
@RequiredArgsConstructor
public class ProductModelMessageQueueConverter implements QueueStrategyInterface {

    private ProductService productService;
    private final Gson gson;
    private final MessageSourceService messageSourceService;
    private final ProductDataPopulationService productDataPopulationService;
    private final LocationClientInternalService locationClientInternalService;

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
        try {
            if (!CollectionUtils.isEmpty(bulkProductVariantRequest.getImageUploadTemplateRequests())) {

                // need to retrieve IDs for subcategory, measurement unit, brand, manufacturer
                // to create product
                List<CreateProductRequestDto> productList = productDataPopulationService
                        .buildProductModelFromUploadedData(bulkProductVariantRequest.getImageUploadTemplateRequests(),
                                bulkProductVariantRequest.getCreatedBy());

                var responseEntity = locationClientInternalService
                        .getCountryByPublicId(bulkProductVariantRequest.getCountryId());

                if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                        || responseEntity.getStatusCodeValue() != 200)
                    throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

                String countryCode = responseEntity.getBody().getData().threeLetterCode();

                for (CreateProductRequestDto request : productList) {
                    try {
                        productService.createNewProduct(request, Boolean.TRUE, countryCode);
                    } catch (Exception e) {
                        log.info("Error occurred while creating product {} ", request);
                    }
                }

            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("cannot.process.queue.message"));
        }
    }
}
