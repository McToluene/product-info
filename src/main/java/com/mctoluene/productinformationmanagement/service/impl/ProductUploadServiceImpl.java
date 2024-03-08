package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BulkProductUploadRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ProductVariantQueueMessage;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ProductVariantUploadTemplateRequest;
import com.mctoluene.productinformationmanagement.exception.DuplicateRecordException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.UtilsHelper;
import com.mctoluene.productinformationmanagement.helper.VariantHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductUploadService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;
import com.mctoluene.productinformationmanagement.service.internal.LocationClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalInternalService;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductUploadServiceImpl implements ProductUploadService {

    private final AzureBusMessageQueueService azureBusMessageQueueService;
    private final MessageSourceService messageSourceService;
    private final ProductInternalService productInternalService;
    private final VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;
    private final BrandInternalService brandInternalService;
    private final LocationClientInternalService locationClientInternalService;

    @Override
    public AppResponse uploadGenericProductFile(MultipartFile file, String uploadedBy, UUID traceId,
            String countryCode) {
        UtilsHelper.validateFile(file);
        BulkProductUploadRequest bulkProductUploadRequest = BulkProductUploadRequest.builder().build();

        List<ImageUploadTemplateRequest> requestContexts = parseFile(file);

        if (checkIfVariantDuplicateExist(requestContexts)) {
            throw new DuplicateRecordException(
                    messageSourceService.getMessageByKey("variant.contains.duplicate.record"));
        }

        buildBulkUploadRequest(uploadedBy, traceId, countryCode, bulkProductUploadRequest, requestContexts);

        sendMessageToQueue(bulkProductUploadRequest, TypeMessage.PRODUCT);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"), null, null);

    }

    private boolean checkIfVariantDuplicateExist(List<ImageUploadTemplateRequest> requestContexts) {
        Set<String> uniqueCombinations = new HashSet<>();
        for (ImageUploadTemplateRequest request : requestContexts) {
            if (StringUtils.isBlank(request.getManufacturerName()) || StringUtils.isBlank(request.getBrand())
                    || StringUtils.isBlank(request.getProductName()) || StringUtils.isBlank(request.getVariantName()))
                throw new ValidatorException(messageSourceService.getMessageByKey("product.mandatory.field.missing"));
            String combination = request.getManufacturerName() + "|" + request.getBrand() + "|"
                    + request.getProductName() + "|" + request.getVariantName();
            if (!uniqueCombinations.add(combination)) {
                log.info("duplicate combination for variant {} ", combination);
                return true;
            }
        }
        return false;
    }

    private boolean checkIfProductDuplicateExist(List<ImageUploadTemplateRequest> requestContexts) {
        Set<String> uniqueCombinations = new HashSet<>();
        for (ImageUploadTemplateRequest request : requestContexts) {
            if (StringUtils.isBlank(request.getManufacturerName()) || StringUtils.isBlank(request.getBrand())
                    || StringUtils.isBlank(request.getProductName()))
                throw new ValidatorException(messageSourceService.getMessageByKey("product.mandatory.field.missing"));
            String combination = request.getManufacturerName() + "|" + request.getBrand() + "|"
                    + request.getProductName();
            if (!uniqueCombinations.add(combination)) {
                log.info("duplicate combination for product {} ", combination);
                return true;
            }
        }
        return false;
    }

    private List<ImageUploadTemplateRequest> parseFile(MultipartFile file) {

        List<ImageUploadTemplateRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                    requestContexts.add(createRequestFromRow(row, dataFormatter));
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while processing ", e);
            throw new UnProcessableEntityException(messageSourceService.getMessageByKey("bulk.upload.of.brand.failed"));
        }
        return requestContexts;
    }

    private ImageUploadTemplateRequest createRequestFromRow(Row row, DataFormatter dataFormatter) {

        String manufacturerName = WordUtils
                .capitalizeFully(UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(0))));
        String brandName = WordUtils
                .capitalizeFully(UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(1))));
        String productCategory = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(2)));
        String productName = WordUtils
                .capitalizeFully(UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(3))));
        String variantType = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(4)));
        String variantName = WordUtils
                .capitalizeFully(UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(5))));
        String weight = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(6)));
        String imageUrl1 = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(7)));
        String imageUrl2 = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(8)));
        String measurementUnit = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(9)));
        String vated = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(10)));
        String vatValue = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(11)));
        String minVat = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(12)));
        String maxVat = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(13)));

        return buildImageUploadTemplateRequest("",
                manufacturerName, brandName, productCategory, productName,
                variantType, variantName, weight, imageUrl1, imageUrl2, measurementUnit, "", "",
                vated, vatValue, minVat, maxVat);
    }

    @Override
    public AppResponse uploadGenericProductFile(MultipartFile file, String uploadedBy, UUID traceId, String countryCode,
            UUID brandPublicId) {
        Brand brand = brandInternalService.findByPublicId(brandPublicId);

        UtilsHelper.validateFile(file);

        BulkProductUploadRequest bulkProductUploadRequest = BulkProductUploadRequest.builder().build();
        List<ImageUploadTemplateRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                        String productCategory = UtilsHelper
                                .validateAndTrim(dataFormatter.formatCellValue(row.getCell(0)));
                        String productName = WordUtils.capitalizeFully(
                                UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(1))));
                        String measurementUnit = UtilsHelper
                                .validateAndTrim(dataFormatter.formatCellValue(row.getCell(2)));
                        String manufacturerName = WordUtils.capitalizeFully(
                                UtilsHelper.validateAndTrim(brand.getManufacturer().getManufacturerName()));
                        String brandName = WordUtils.capitalizeFully(UtilsHelper.validateAndTrim(brand.getBrandName()));
                        String minVat = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(3)));
                        String maxVat = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(4)));
                        ImageUploadTemplateRequest imageUploadTemplateRequest = buildImageUploadTemplateRequest("",
                                manufacturerName, brandName, productCategory, productName, "", "", "",
                                "", "", measurementUnit, "", "", "", "",
                                minVat, maxVat);
                        requestContexts.add(imageUploadTemplateRequest);
                    }
                }
            }

            buildBulkUploadRequest(uploadedBy, traceId, countryCode, bulkProductUploadRequest, requestContexts);

            sendMessageToQueue(bulkProductUploadRequest, TypeMessage.PRODUCT_ONLY);
            return new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                    messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"), null, null);

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.upload.of.product.failed"));
        }
    }

    @Override
    public AppResponse uploadGenericProductVariantFile(MultipartFile file, String uploadedBy, UUID traceId,
            String countryCode, UUID productPublicId) {

        UtilsHelper.validateFile(file);
        ProductVariantQueueMessage queueMessage = ProductVariantQueueMessage.builder().build();
        List<ProductVariantUploadTemplateRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                        String variantType = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(0)));
                        String variantName = WordUtils.capitalizeFully(
                                UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(1))));
                        String weight = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(2)));
                        String imageUrl1 = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(3)));
                        String imageUrl2 = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(4)));

                        ProductVariantUploadTemplateRequest productVariantUploadTemplateRequest = buildProductVariantUploadTemplateRequest(
                                variantType, variantName, weight, imageUrl1, imageUrl2);
                        requestContexts.add(productVariantUploadTemplateRequest);
                    }
                }
            }

            queueMessage.setProductPublicId(productPublicId);
            queueMessage.setUploadTemplateRequestList(requestContexts);
            queueMessage.setTraceId(traceId);
            queueMessage.setCreatedBy(uploadedBy);

            var responseEntity = locationClientInternalService.findCountryByCode(countryCode);
            if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                    || responseEntity.getStatusCodeValue() != 200)
                throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

            queueMessage.setCountryId(responseEntity.getBody().getData().publicId());

            sendMessageToQueue(queueMessage, TypeMessage.VARIANT_ONLY);

            return new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                    messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                    null, null);

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.upload.of.product.failed"));
        }
    }

    private void buildBulkUploadRequest(String uploadedBy, UUID traceId, String countryCode,
            BulkProductUploadRequest bulkProductUploadRequest, List<ImageUploadTemplateRequest> requestContexts) {
        bulkProductUploadRequest.setImageUploadTemplateRequests(requestContexts);
        bulkProductUploadRequest.setCategoryUploadTemplateRequests(new HashMap<>());
        bulkProductUploadRequest.setPriceTemplateRequests(new ArrayList<>());
        bulkProductUploadRequest.setStockUpdateTemplateRequests(new ArrayList<>());
        bulkProductUploadRequest.setTraceId(traceId);
        bulkProductUploadRequest.setCreatedBy(uploadedBy);
        bulkProductUploadRequest.setCountryId(
                locationClientInternalService.findCountryByCode(countryCode).getBody().getData().publicId());
    }

    public static ImageUploadTemplateRequest buildImageUploadTemplateRequest(String businessName,
            String manufacturerName,
            String brandName, String productCategory, String productName,
            String variantType, String variantName,
            String weight, String imageUrl1, String imageUrl2,
            String measurementUnit, String costPrice,
            String listingPrice,
            String vated, String vatValue,
            String minVat, String maxVat) {
        ImageUploadTemplateRequest imageUploadTemplateRequest = new ImageUploadTemplateRequest();
        imageUploadTemplateRequest.setBusinessName(StringSanitizerUtils.sanitizeInput(businessName).trim());
        imageUploadTemplateRequest.setManufacturerName(StringSanitizerUtils.sanitizeInput(manufacturerName).trim());
        imageUploadTemplateRequest.setBrand(StringSanitizerUtils.sanitizeInput(brandName).trim());
        imageUploadTemplateRequest.setProductCategory(StringSanitizerUtils.sanitizeInput(productCategory).trim());
        imageUploadTemplateRequest.setProductName(StringSanitizerUtils.sanitizeInput(productName).trim());
        imageUploadTemplateRequest.setVariantType(StringSanitizerUtils.sanitizeInput(variantType).trim());
        imageUploadTemplateRequest.setVariantName(StringSanitizerUtils.sanitizeInput(variantName).trim());
        imageUploadTemplateRequest.setWeight(weight.isBlank() ? 0 : Double.valueOf(weight));
        imageUploadTemplateRequest.setCostPrice(BigDecimal.ZERO);
        imageUploadTemplateRequest.setListingPrice(BigDecimal.ZERO);
        imageUploadTemplateRequest.setImageUrl1(imageUrl1.trim());
        imageUploadTemplateRequest.setImageUrl2(imageUrl2.trim());
        imageUploadTemplateRequest
                .setCostPrice(costPrice.isBlank() ? BigDecimal.ZERO : BigDecimal.valueOf(Long.parseLong(costPrice)));
        imageUploadTemplateRequest.setListingPrice(
                listingPrice.isBlank() ? BigDecimal.ZERO : BigDecimal.valueOf(Long.parseLong(listingPrice)));
        imageUploadTemplateRequest.setMeasurementUnit(StringSanitizerUtils.sanitizeInput(measurementUnit));
        imageUploadTemplateRequest.setVated(!vated.isEmpty() ? Boolean.valueOf(vated) : false);

        BigDecimal vatValueBigDecimal = vatValue == null || vatValue.isBlank() ? BigDecimal.ZERO
                : BigDecimal.valueOf(Double.parseDouble(vatValue));
        imageUploadTemplateRequest.setVatValue(vatValueBigDecimal);

        BigDecimal minVatBigDecimal = minVat == null || minVat.isBlank() ? BigDecimal.ZERO
                : BigDecimal.valueOf(Double.parseDouble(minVat));
        imageUploadTemplateRequest.setMinVat(minVatBigDecimal);

        BigDecimal maxVatBigDecimal = maxVat == null || maxVat.isBlank() ? BigDecimal.ZERO
                : BigDecimal.valueOf(Double.parseDouble(maxVat));
        imageUploadTemplateRequest.setMaxVat(maxVatBigDecimal);

        return imageUploadTemplateRequest;
    }

    private ProductVariantUploadTemplateRequest buildProductVariantUploadTemplateRequest(String variantType,
            String variantName, String weight, String imageUrl1, String imageUrl2) {
        ProductVariantUploadTemplateRequest productVariantUploadTemplateRequest = new ProductVariantUploadTemplateRequest();
        productVariantUploadTemplateRequest.setVariantType(StringSanitizerUtils.sanitizeInput(variantType).trim());
        productVariantUploadTemplateRequest.setVariantName(StringSanitizerUtils.sanitizeInput(variantName).trim());
        productVariantUploadTemplateRequest.setWeight(weight.isBlank() ? 0 : Double.parseDouble(weight));
        productVariantUploadTemplateRequest.setImageUrl1(imageUrl1.trim());
        productVariantUploadTemplateRequest.setImageUrl2(imageUrl2.trim());

        return productVariantUploadTemplateRequest;
    }

    private void sendMessageToQueue(Object uploadTemplateRequest, String typeMessage) {
        log.info("about to send the request to the queue {} ", uploadTemplateRequest);

        MessageContentEvent contentEvent = new MessageContentEvent<>(typeMessage, uploadTemplateRequest);
        azureBusMessageQueueService.sendMessage(contentEvent);
    }
}
