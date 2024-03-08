package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ManufactureUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ManufacturerFilter;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.helper.ManufacturerHelper;
import com.mctoluene.productinformationmanagement.helper.UtilsHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.ManufacturerService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;

import static com.mctoluene.productinformationmanagement.util.StringSanitizerUtils.sanitizeInput;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerInternalService manufacturerInternalService;

    private final ProductInternalService productInternalService;

    private final MessageSourceService messageSourceService;

    private final AzureBusMessageQueueService azureBusMessageQueueService;

    @Override
    public AppResponse<ManufacturerResponseDto> createManufacturer(
            CreateManufacturerRequestDto manufacturerRequestDto) {

        Manufacturer manufacturer = ManufacturerHelper.buildManufacturerEntity(manufacturerRequestDto);
        manufacturerInternalService.saveNewManufacturer(manufacturer);

        ManufacturerResponseDto responseDto = ManufacturerHelper.buildManufacturerResponse(manufacturer);
        return new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("manufacturer.created.successfully"),
                messageSourceService.getMessageByKey("manufacturer.created.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse<ManufacturerResponseDto> getManufacturer(UUID publicId) {
        Manufacturer manufacturer = manufacturerInternalService.findByPublicId(publicId).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));
        ManufacturerResponseDto responseDto = ManufacturerHelper.buildManufacturerResponse(manufacturer);

        log.info("Manufacturer with publicId {} fetched retrieved successfully {}", publicId, responseDto);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse<Page<ManufacturerResponseDto>> getAllManufacturers(Integer page, Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("manufacturers.manufacturer_name"));
        Page<Manufacturer> listOfManufacturers = manufacturerInternalService.findAllBy(pageable);

        log.info("{} Manufacturers on page {} retrieved successfully", size, page + 1);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                new PageImpl<>(listOfManufacturers
                        .stream()
                        .map(ManufacturerHelper::buildManufacturerResponse)
                        .toList(), pageable,
                        listOfManufacturers.getTotalElements()),
                null);
    }

    @Override
    public AppResponse updateManufacturer(UUID publicId, UpdateManufacturerRequestDto requestDto) {

        if (Objects.isNull(requestDto.getManufacturerName()) || StringUtils.isBlank(requestDto.getManufacturerName())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("manufacturer.not.null.or.empty"));
        }
        Manufacturer manufacturer = manufacturerInternalService.findByPublicIdAndManufacturer(publicId, requestDto);

        ManufacturerResponseDto manufacturerResponseDto = ManufacturerHelper
                .buildManufacturerResponse(updateManufacturer(manufacturer, requestDto));

        log.info("Manufacturer updated successfully :: {}", manufacturerResponseDto);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.updated.successfully"),
                messageSourceService.getMessageByKey("manufacturer.updated.successfully"),
                manufacturerResponseDto, null);
    }

    @Override
    public AppResponse<Void> deleteManufacturer(UUID publicId) {
        Manufacturer manufacturer = manufacturerInternalService.findByPublicId(publicId).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));

        if (Boolean.TRUE.equals(productInternalService.checkIfManufacturerIsInUse(manufacturer.getId())))
            throw new ValidatorException(messageSourceService.getMessageByKey("manufacturer.is.in.use"));

        String status = manufacturer.getStatus().toString();
        if (status.equals(Status.DELETED.toString())) {
            throw new ModelNotFoundException("manufacturer not found");
        }
        manufacturer.setManufacturerName((manufacturer.getManufacturerName() + LocalDateTime.now()));
        manufacturer.setStatus(Status.DELETED);

        Manufacturer deletedManufacturer = manufacturerInternalService
                .deleteManufacturer(manufacturer);
        log.info("Manufacturer has been deleted {}", deletedManufacturer.getStatus());

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.deleted.successfully"),
                messageSourceService.getMessageByKey("manufacturer.deleted.successfully"),
                null, null);

    }

    @Override
    public AppResponse<ManufacturerResponseDto> enableManufacturerStatus(UUID publicId) {
        Manufacturer manufacturer = manufacturerInternalService.findByPublicId(publicId).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));
        String status = manufacturer.getStatus().toString();

        if (!status.equals(Status.ACTIVE.toString())) {
            manufacturer.setStatus(Status.ACTIVE);
            manufacturerInternalService.saveNewManufacturerToDb(manufacturer);

            log.info("Manufacturer status enabled successfully");
            return new AppResponse<>(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("manufacturer.status.enabled.successfully"),
                    messageSourceService.getMessageByKey("manufacturer.status.enabled.successfully"),
                    null,
                    null);
        }

        log.info("Manufacturer status is already enabled");
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.status.already.enabled"),
                messageSourceService.getMessageByKey("manufacturer.status.already.enabled"),
                null, null);
    }

    @Override
    public AppResponse<ManufacturerResponseDto> disableManufacturerStatus(UUID publicID) {
        Manufacturer manufacturer = manufacturerInternalService.findByPublicId(publicID).orElseThrow(
                () -> new NoResultException(messageSourceService.getMessageByKey("manufacturer.not.found")));
        String status = manufacturer.getStatus().toString();

        if (!status.equals(Status.INACTIVE.toString())) {
            manufacturer.setStatus(Status.INACTIVE);
            manufacturerInternalService.saveNewManufacturerToDb(manufacturer);

            log.info("Manufacturer status disabled successfully");

            return new AppResponse<>(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("manufacturer.status.disabled.successfully"),
                    messageSourceService.getMessageByKey("manufacturer.status.disabled.successfully"),
                    null, null);
        }

        log.info("Manufacturer status is already disabled");
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.status.already.disabled"),
                messageSourceService.getMessageByKey("manufacturer.status.already.disabled"),
                null, null);
    }

    @Override
    public AppResponse uploadManufacturerUsingExcel(MultipartFile file, String createdBy, UUID traceId) {
        UtilsHelper.validateFile(file);
        List<ManufactureUploadRequest> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        try {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && StringUtils.isNotBlank(dataFormatter.formatCellValue(row.getCell(0)))) {
                        String manufacture = UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(0)));
                        String description = WordUtils.capitalizeFully(
                                UtilsHelper.validateAndTrim(dataFormatter.formatCellValue(row.getCell(1))));
                        requestContexts.add(buildManufactureUploadRequest(manufacture, description, createdBy));
                    }
                }
            }
            sendMessageToQueue(requestContexts);

            return new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("manufacture.upload.request.in.process"),
                    messageSourceService.getMessageByKey("manufacture.upload.request.in.process"),
                    null, null);
        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.upload.of.manufacture.failed"));
        }
    }

    private Manufacturer updateManufacturer(Manufacturer manufacturer, UpdateManufacturerRequestDto requestDto) {
        String manufacturerName = sanitizeInput(requestDto.getManufacturerName());
        manufacturer.setManufacturerName(WordUtils.capitalize(manufacturerName.trim()));

        if (!Objects.isNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty())
            manufacturer.setDescription(requestDto.getDescription());

        if (!Objects.isNull(requestDto.getLastModifiedBy()) && !requestDto.getLastModifiedBy().isEmpty())
            manufacturer.setLastModifiedBy(requestDto.getLastModifiedBy());

        return manufacturerInternalService.updateManufacturer(manufacturer);
    }

    public static ManufactureUploadRequest buildManufactureUploadRequest(String manufacturerName, String description,
            String uploadedBy) {
        ManufactureUploadRequest manufactureUploadRequest = new ManufactureUploadRequest();
        manufactureUploadRequest.setDescription(description);
        manufactureUploadRequest.setManufacturerName(manufacturerName);
        manufactureUploadRequest.setCreatedBy(uploadedBy);
        return manufactureUploadRequest;
    }

    private void sendMessageToQueue(List<ManufactureUploadRequest> manufactureUploadRequests) {
        log.info("about to send the request to the queue {} ", manufactureUploadRequests);
        MessageContentEvent contentEvent = new MessageContentEvent(TypeMessage.MANUFACTURE, manufactureUploadRequests);
        azureBusMessageQueueService.sendMessage(contentEvent);
    }

    @Override
    public ByteArrayResource download(Integer page, Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("manufacturers.manufacturer_name"));
        Page<Manufacturer> listOfManufacturers = manufacturerInternalService.findAllBy(pageable);

        log.info("{} Manufacturers on page {} retrieved successfully", size, page + 1);
        var content = listOfManufacturers.getContent();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Manufacturer");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Created Date");
            headerRow.createCell(4).setCellValue("Created By");
            headerRow.createCell(5).setCellValue("Last Modified By");

            int rowNum = 1;
            for (Manufacturer data : content) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getPublicId().toString());
                row.createCell(1).setCellValue(data.getManufacturerName());
                row.createCell(2).setCellValue(data.getDescription());
                row.createCell(3).setCellValue(data.getCreatedDate());
                row.createCell(4).setCellValue(data.getCreatedBy());
                row.createCell(5).setCellValue(data.getLastModifiedBy());
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.download.of.manufacturer.failed"));
        }
    }

    @Override
    public AppResponse<Page<ManufacturerResponse>> filterBrand(ManufacturerFilter manufacturerFilter,
            Pageable pageable) {
        QueryBuilder<Manufacturer, ManufacturerResponse> queryBuilder = QueryBuilder.build(Manufacturer.class,
                ManufacturerResponse.class);
        manufacturerFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<ManufacturerResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);

    }

    @Override
    public AppResponse<Page<ManufacturerResponseDto>> getAllManufacturers(String name, Integer page, Integer size,
            SortCriteria sort) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;
        Pageable pageable = PageRequest.of(page, size);
        if (sort != null) {
            if (sort == SortCriteria.CREATED_DATE)
                pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
            else if (sort == SortCriteria.NAME)
                pageable = PageRequest.of(page, size, Sort.by("manufacturerName").ascending());
        }

        Page<Manufacturer> listOfManufacturers = manufacturerInternalService.findAllByCriteria(name, pageable);
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                new PageImpl<>(listOfManufacturers
                        .stream()
                        .map(ManufacturerHelper::buildManufacturerResponse)
                        .toList(), pageable,
                        listOfManufacturers.getTotalElements()),
                null);
    }
}
