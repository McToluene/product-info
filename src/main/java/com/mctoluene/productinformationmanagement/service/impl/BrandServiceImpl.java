package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BrandUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.brand.BrandManufacturerRequest;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.exception.DuplicateRecordException;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.helper.BrandHelper;
import com.mctoluene.productinformationmanagement.helper.UtilsHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.BrandService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandInternalService brandInternalService;
    private final ManufacturerInternalService manufacturerInternalService;
    private final MessageSourceService messageSourceService;
    private final ProductInternalService productInternalService;
    private final AzureBusMessageQueueService azureBusMessageQueueService;

    @Override
    public AppResponse<BrandResponseDto> createBrand(CreateBrandRequestDto requestDto) {
        var manufacturer = manufacturerInternalService.findByPublicId(requestDto.getManufacturerId()).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));

        Brand brand = BrandHelper.buildBrandEntity(requestDto, manufacturer);
        brandInternalService.saveNewBrand(brand);
        BrandResponseDto responseDto = BrandHelper.buildBrandResponse(brand);
        log.info("Brand created :: {}", responseDto);
        return new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("brand.created.successfully"),
                messageSourceService.getMessageByKey("brand.created.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse<BrandResponseDto> getBrandByPublicId(UUID publicId) {
        Brand brand = brandInternalService.findByPublicId(publicId);

        BrandResponseDto responseDto = BrandHelper.buildBrandResponse(brand);
        log.info("Fetched brand with public Id {}, response:: {}", publicId, responseDto);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.fetched.successfully"),
                messageSourceService.getMessageByKey("brand.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse<Page<BrandResponseDto>> getBrands(Integer page, Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("brands.brand_Name"));
        Page<Brand> brands = brandInternalService.findAllBy(pageable);
        log.info("Fetched {} brands on page {} ", size, page + 1);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                new PageImpl<>(brands.stream().map(BrandHelper::buildBrandResponse).toList(), pageable,
                        brands.getTotalElements()),
                null);
    }

    @Override
    public AppResponse<Page<BrandResponseDto>> getBrands(String brandName, UUID manufacturerId, Integer page,
            Integer size, SortCriteria sort) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;
        Pageable pageable;
        if (sort == SortCriteria.CREATED_DATE)
            pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        else
            pageable = PageRequest.of(page, size, Sort.by("brandName").ascending());

        Page<Brand> brands;
        if (manufacturerId != null) {
            var manufacturer = manufacturerInternalService.findByPublicId(manufacturerId).orElseThrow(
                    () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));
            brands = brandInternalService.findAllByCriteria(brandName, manufacturer, pageable);
        } else
            brands = brandInternalService.findAllByCriteria(brandName, pageable);

        log.info("Fetched {} brands on page {} ", size, page + 1);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                new PageImpl<>(brands.stream().map(BrandHelper::buildBrandResponse).toList(), pageable,
                        brands.getTotalElements()),
                null);
    }

    @Override
    public AppResponse<BrandResponseDto> editBrand(UUID publicId, EditBrandRequestDto requestDto) {
        Brand brand = brandInternalService.findByPublicIdAndBrandName(publicId, requestDto);
        BrandResponseDto brandResponseDto = BrandHelper
                .buildBrandResponse(updateBrand(brand, requestDto));

        log.info("Updated brand with publicId {}, response:: {}", publicId, brandResponseDto);

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                brandResponseDto, null);
    }

    private Brand updateBrand(Brand brand, EditBrandRequestDto requestDto) {
        if (!Objects.isNull(requestDto.getBrandName()) && !requestDto.getBrandName().isEmpty())
            brand.setBrandName(WordUtils.capitalizeFully(requestDto.getBrandName().trim()));

        if (!Objects.isNull(requestDto.getManufacturerId()))
            brand.setManufacturer(
                    manufacturerInternalService.findByPublicId(requestDto.getManufacturerId()).orElseThrow(
                            () -> new NoResultException(
                                    messageSourceService.getMessageByKey("manufacturer.not.found"))));

        if (!Objects.isNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty())
            brand.setDescription(requestDto.getDescription());

        if (!Objects.isNull(requestDto.getLastModifiedBy()) && !requestDto.getLastModifiedBy().isEmpty())
            brand.setLastModifiedBy(requestDto.getLastModifiedBy());

        return brandInternalService.updateBrand(brand);
    }

    @Override
    public AppResponse<Void> deleteBrand(UUID publicId) {
        Brand brand = brandInternalService.findByPublicId(publicId);

        if (Boolean.TRUE.equals(productInternalService.checkIfBrandIsInUse(brand.getId())))
            throw new ValidatorException(messageSourceService.getMessageByKey("brand.is.in.use"));

        if (brand.getStatus().equals(Status.DELETED)) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("brand.not.found"));
        }

        brand.setStatus(Status.DELETED);
        brand.setBrandName(brand.getBrandName() + LocalDateTime.now());

        brandInternalService.deleteBrand(brand);
        log.info("Brand has been deleted");

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.deleted.successfully"),
                messageSourceService.getMessageByKey("brand.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse<Void> uploadBrandFile(MultipartFile file, String uploadedBy, UUID traceId, UUID manufacturerId)
            throws IOException {
        UtilsHelper.validateFile(file);
        BrandUploadRequest brandUploadRequest = BrandUploadRequest.builder().build();
        List<BrandUploadRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                        String brand = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(0)));
                        String description = WordUtils.capitalizeFully(
                                UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(1))));

                        brandUploadRequest = buildImageUploadTemplateRequest(brand, description, uploadedBy,
                                manufacturerId);
                        requestContexts.add(brandUploadRequest);
                    }
                }
            }
            sendMessageToQueue(requestContexts);

            return new AppResponse<>(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("brand.image.request.created.successfully"),
                    messageSourceService.getMessageByKey("brand.image.request.created.successfully"),
                    null, null);
        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(messageSourceService.getMessageByKey("bulk.upload.of.brand.failed"));
        }

    }

    @Override
    public AppResponse<Page<BrandResponseDto>> getBrandsByManufacturer(UUID manufacturerPublicId, Integer page,
            Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("brands.brand_Name"));
        var manufacturer = manufacturerInternalService.findByPublicId(manufacturerPublicId).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));
        var brands = manufacturer.getBrands();
        log.info("Fetched {} brands on page {} ", size, page + 1);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                new PageImpl<>(brands.stream().map(BrandHelper::buildBrandResponse).toList(), pageable,
                        brands.size()),
                null);
    }

    public static BrandUploadRequest buildImageUploadTemplateRequest(String brandName, String description,
            String uploadedBy, UUID manufacturerId) {
        BrandUploadRequest brandUploadRequest = new BrandUploadRequest();
        brandUploadRequest.setDescription(description);
        brandUploadRequest.setBrandName(brandName);
        brandUploadRequest.setCreatedBy(uploadedBy);
        brandUploadRequest.setManufacturerId(manufacturerId);
        return brandUploadRequest;
    }

    private void sendMessageToQueue(List<BrandUploadRequest> brandUploadRequest) {
        log.info("about to send the request to the queue {} ", brandUploadRequest);
        var contentEvent = new MessageContentEvent<>(TypeMessage.BRAND, brandUploadRequest);
        azureBusMessageQueueService.sendMessage(contentEvent);
    }

    @Override
    public ByteArrayResource download(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);
        log.info("size is {}, page is", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<Brand> brands = brandInternalService.findAllBy(pageRequest);
        log.info("Fetched {} brands on page {} ", size, page + 1);
        var mappedBrands = brands.stream().map(BrandHelper::buildBrandResponse).toList();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Brands");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Created Date");
            headerRow.createCell(4).setCellValue("Created By");
            headerRow.createCell(5).setCellValue("Last Modified By");

            int rowNum = 1;
            for (BrandResponseDto data : mappedBrands) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.publicId().toString());
                row.createCell(1).setCellValue(data.brandName());
                row.createCell(2).setCellValue(data.description());
                row.createCell(3).setCellValue(data.createdDate());
                row.createCell(4).setCellValue(data.createdBy());
                row.createCell(5).setCellValue(data.lastModifiedBy());
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.download.of.brand.failed"));
        }
    }

    @Override
    public AppResponse uploadBrandManufacturerFile(MultipartFile file, String uploadedBy, UUID traceId) {
        UtilsHelper.validateFile(file);
        List<BrandManufacturerRequest> requestContexts = parseFile(file, uploadedBy);

        if (checkIfDuplicateExist(requestContexts)) {
            throw new DuplicateRecordException(messageSourceService.getMessageByKey("brand.contains.duplicate.record"));
        }

        sendMessageToQueue(requestContexts, traceId);

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.image.request.created.successfully"),
                messageSourceService.getMessageByKey("brand.image.request.created.successfully"),
                null, null);
    }

    @Override
    public AppResponse<Page<BrandResponse>> filterBrand(BrandFilter brandFilter, Pageable pageable) {
        QueryBuilder<Brand, BrandResponse> queryBuilder = QueryBuilder.build(Brand.class, BrandResponse.class);
        brandFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<BrandResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }

    private List<BrandManufacturerRequest> parseFile(MultipartFile file, String createdBy) {
        List<BrandManufacturerRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                    requestContexts.add(createRequestFromRow(row, dataFormatter, createdBy));
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while processing ", e);
            throw new UnProcessableEntityException(messageSourceService.getMessageByKey("bulk.upload.of.brand.failed"));
        }
        return requestContexts;
    }

    private BrandManufacturerRequest createRequestFromRow(Row row, DataFormatter dataFormatter, String createdBy) {
        String manufacturerName = StringSanitizerUtils.sanitizeInput(dataFormatter.formatCellValue(row.getCell(0)));
        String manufacturerDescription = StringSanitizerUtils
                .sanitizeInput(dataFormatter.formatCellValue(row.getCell(1)));
        String brandName = StringSanitizerUtils.sanitizeInput(dataFormatter.formatCellValue(row.getCell(2)));
        String description = StringSanitizerUtils
                .sanitizeInput(UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(3))));
        return new BrandManufacturerRequest(
                WordUtils.capitalizeFully(manufacturerName.trim()),
                WordUtils.capitalizeFully(manufacturerDescription).trim(),
                WordUtils.capitalizeFully(brandName).trim(),
                WordUtils.capitalizeFully(description).trim(), createdBy);
    }

    private void sendMessageToQueue(List<BrandManufacturerRequest> requestContexts, UUID traceId) {
        log.info("[{}] about to send message to queue {} ", traceId, requestContexts);
        var contentEvent = new MessageContentEvent<>(TypeMessage.BRAND_MANUFACTURER, requestContexts);
        azureBusMessageQueueService.sendMessage(contentEvent);
    }

    private boolean checkIfDuplicateExist(List<BrandManufacturerRequest> requestContexts) {
        Set<String> uniqueCombinations = new HashSet<>();
        for (BrandManufacturerRequest request : requestContexts) {
            if (StringUtils.isBlank(request.getManufacturerName()) || StringUtils.isBlank(request.getBrandName()))
                throw new ValidatorException(messageSourceService.getMessageByKey("brand.mandatory.field.missing"));

            String combination = request.getManufacturerName() + "|" + request.getBrandName();
            if (!uniqueCombinations.add(combination)) {
                log.info("duplicate combination {} ", combination);
                return true;
            }
        }
        return false;
    }

}
