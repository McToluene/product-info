package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.CreateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.MeasuringUnitResponseDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.MeasuringUnitHelper;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;
import com.mctoluene.productinformationmanagement.service.MeasuringUnitService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.MeasuringUnitInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MeasuringUnitServiceImpl implements MeasuringUnitService {

    private final MeasuringUnitInternalService measuringUnitInternalService;

    private final MessageSourceService messageSourceService;

    @Override
    public AppResponse createMeasuringUnit(CreateMeasuringUnitRequestDto measuringUnitRequestDto) {

        this.validateMeasuringUnit(measuringUnitRequestDto);

        MeasuringUnit measuringUnit = MeasuringUnitHelper.buildMeasuringUnitEntity(measuringUnitRequestDto);

        measuringUnitInternalService.saveNewMeasuringUnit(measuringUnit);

        MeasuringUnitResponseDto responseDto = MeasuringUnitHelper.buildMeasuringUnitResponse(measuringUnit);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("measuring.unit.created.successfully"),
                messageSourceService.getMessageByKey("measuring.unit.created.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getMeasuringUnitById(UUID publicId) {
        MeasuringUnit measuringUnit = measuringUnitInternalService.findByPublicId(publicId);

        MeasuringUnitResponseDto responseDto = MeasuringUnitHelper.buildMeasuringUnitResponse(measuringUnit);

        log.info("Successfully retrieved measuring unit with publicId {}, {}", publicId, responseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("measuring.units.fetched.successfully"),
                messageSourceService.getMessageByKey("measuring.units.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getAllMeasuringUnits(Integer page, Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;

        Page<MeasuringUnit> listOfMeasuringUnits = measuringUnitInternalService
                .findAllBy(PageRequest.of(page, size, Sort.by("name")));

        log.info("Retrieving page {} and size {}", page + 1, size);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.measuring.units.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.measuring.units.retrieved.successfully"),
                listOfMeasuringUnits, null);
    }

    @Override
    public AppResponse editMeasuringUnit(UUID publicId, UpdateMeasuringUnitRequestDto requestDto) {
        MeasuringUnit measuringUnit = measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(publicId,
                requestDto);

        MeasuringUnitResponseDto responseDto = MeasuringUnitHelper
                .buildMeasuringUnitResponse(updateMeasuringUnit(measuringUnit, requestDto));

        log.info("measuring unit with publicId {} successfully updated.. response:: {}", publicId, requestDto);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("measuring.unit.updated.successfully"),
                messageSourceService.getMessageByKey("measuring.unit.updated.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse deleteMeasuringUnit(UUID publicId) {
        MeasuringUnit measuringUnit = measuringUnitInternalService.findByPublicId(publicId);
        if (measuringUnit.getStatus().equals(Status.DELETED.name()))
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.not.found"));

        String measuringUnitName = measuringUnit.getName();
        String abbreviation = measuringUnit.getAbbreviation();

        measuringUnit.setStatus(Status.DELETED.name());
        measuringUnit.setAbbreviation(abbreviation + LocalDateTime.now());
        measuringUnit.setName(measuringUnitName + LocalDateTime.now());

        MeasuringUnit deletedMeasuringUnit = measuringUnitInternalService.deleteMeasuringUnit(measuringUnit);
        log.info("Measuring unit deleted successfully");

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("measuring.unit.deleted.successfully"),
                messageSourceService.getMessageByKey("measuring.unit.deleted.successfully"),
                null, null);
    }

    private MeasuringUnit updateMeasuringUnit(MeasuringUnit measuringUnit, UpdateMeasuringUnitRequestDto requestDto) {
        if (!Objects.isNull(requestDto.getName()) && !requestDto.getName().isEmpty())
            measuringUnit.setName(requestDto.getName().trim());

        if (!Objects.isNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty())
            measuringUnit.setDescription(requestDto.getDescription());

        if (!Objects.isNull(requestDto.getAbbreviation()) && !requestDto.getAbbreviation().isEmpty())
            measuringUnit.setAbbreviation(requestDto.getAbbreviation());

        if (!Objects.isNull(requestDto.getModifiedBy()) && !requestDto.getModifiedBy().isEmpty())
            measuringUnit.setLastModifiedBy(requestDto.getModifiedBy());

        measuringUnit.setLastModifiedDate(LocalDateTime.now());

        return measuringUnitInternalService.updateMeasuringUnit(measuringUnit);
    }

    private void validateMeasuringUnit(CreateMeasuringUnitRequestDto measuringUnitRequestDto) {
        if (Objects.isNull(measuringUnitRequestDto.getName()) ||
                measuringUnitRequestDto.getName().isEmpty()) {
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.name.not.passed"));
        }
        if (Objects.isNull(measuringUnitRequestDto.getAbbreviation()) ||
                measuringUnitRequestDto.getAbbreviation().isEmpty()) {
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("measuring.unit.abbreviation.not.passed"));
        }
        if (Objects.isNull(measuringUnitRequestDto.getCreatedBy()) ||
                measuringUnitRequestDto.getCreatedBy().isEmpty()) {
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.created.by.not.passed"));
        }

    }

    @Override
    public ByteArrayResource download(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);
        log.info("size is {}, page is", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<MeasuringUnit> listOfMeasuringUnits = measuringUnitInternalService
                .findAllBy(PageRequest.of(page, size, Sort.by("name")));
        log.info("Fetched {} measuring unit  on page {} ", size, page + 1);
        var content = listOfMeasuringUnits.getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Brands");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Abbreviation");
            headerRow.createCell(4).setCellValue("Created Date");
            headerRow.createCell(5).setCellValue("Created By");
            headerRow.createCell(6).setCellValue("Last Modified By");

            int rowNum = 1;
            for (MeasuringUnit data : content) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getId().toString());
                row.createCell(1).setCellValue(data.getName());
                row.createCell(2).setCellValue(data.getDescription());
                row.createCell(3).setCellValue(data.getAbbreviation());
                row.createCell(4).setCellValue(data.getCreatedDate());
                row.createCell(5).setCellValue(data.getCreatedBy());
                row.createCell(6).setCellValue(data.getLastModifiedBy());
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.download.of.unit.failed"));
        }
    }

}
