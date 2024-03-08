package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.image.FailedImageUploadDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageUploadRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageResponseDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.helper.ImageHelper;
import com.mctoluene.productinformationmanagement.helper.UtilsHelper;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.productinformationmanagement.service.ImagePublisherService;
import com.mctoluene.productinformationmanagement.service.ImageService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.commons.exceptions.ValidatorException;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageInternalService imageInternalService;

    private final MessageSourceService messageSourceService;

    private final ImagePublisherService imagePublisherService;

    private static final String IMAGE_UPLOAD_MESSAGE_KEY = "image.uploaded.successfully";
    private static final String IMAGE_NOT_FOUND_MESSAGE_KEY = "image.not.found";
    private static final String IMAGE_FETCHED_MESSAGE_KEY = "image.fetched.successfully";

    @Override
    public AppResponse<List<ImageResponseDto>> uploadImages(BulkUploadImageRequestDto requestDto) {

        Image[] imageDto = ImageHelper.buildImageEntities(requestDto);

        List<Image> response = imageInternalService.uploadImages(requestDto.getImage(), imageDto);

        ImageResponseDto[] responseDto = ImageHelper.buildImageResponses(response);

        log.info("Successfully uploaded image");
        return new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey(IMAGE_UPLOAD_MESSAGE_KEY),
                messageSourceService.getMessageByKey(IMAGE_UPLOAD_MESSAGE_KEY),
                Arrays.asList(responseDto), null);
    }

    @Override
    public AppResponse<String> deleteImage(String imageName) {

        String response = imageInternalService.deleteImage(imageName);

        if (response.equalsIgnoreCase(HttpStatus.NOT_FOUND.getReasonPhrase())) {
            log.info(IMAGE_NOT_FOUND_MESSAGE_KEY);
            return new AppResponse<>(HttpStatus.NOT_FOUND.value(),
                    messageSourceService.getMessageByKey(IMAGE_NOT_FOUND_MESSAGE_KEY),
                    messageSourceService.getMessageByKey(IMAGE_NOT_FOUND_MESSAGE_KEY),
                    response, null);
        }

        log.info("Successfully deleted image");
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("image.deleted.successfully"),
                messageSourceService.getMessageByKey("image.deleted.successfully"),
                response, null);
    }

    @Override
    public AppResponse<Image> uploadImageFromUrl(ImageRequestDto imageRequestDto) {
        Image image = imageInternalService.uploadImagesFromUrl(imageRequestDto.imageUrl(), imageRequestDto.imageName(),
                "");

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey(IMAGE_UPLOAD_MESSAGE_KEY),
                messageSourceService.getMessageByKey(IMAGE_UPLOAD_MESSAGE_KEY), image, null);
    }

    @Override
    public AppResponse<Void> uploadFile(MultipartFile file, String uploadedBy) {
        UtilsHelper.validateFile(file);
        List<ImageUploadRequestDto> requestContexts = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        List<FailedImageUploadDto> failedImageUploadDtos = new ArrayList<>();
        try {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows() + 1; j++) {
                    Row row = sheet.getRow(j);
                    if (row != null && StringUtils.isNotEmpty(dataFormatter.formatCellValue(row.getCell(0)))) {
                        String imageName = dataFormatter.formatCellValue(row.getCell(0));
                        String imageUrl = dataFormatter.formatCellValue(row.getCell(1));

                        requestContexts.add(new ImageUploadRequestDto(imageName, imageUrl));
                    }
                }
            }
            sendMessageToQueue(requestContexts, uploadedBy);
            return new AppResponse<>(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("image.upload.process"),
                    messageSourceService.getMessageByKey("image.upload.process.notification.message"), null, null);

        } catch (Exception e) {
            FailedImageUploadDto failedProductUploadDto = new FailedImageUploadDto(e.getMessage(), "", "");
            failedImageUploadDtos.add(failedProductUploadDto);
            log.error("Error occurred {} {} ", e, failedProductUploadDto);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.upload.of.product.failed"));

        }
    }

    private void sendMessageToQueue(List<ImageUploadRequestDto> imageUploadRequestDtos, String uploadedBy) {
        try {
            log.info("about to publish to Azure queue {} ", imageUploadRequestDtos);
            imagePublisherService.sendMessageToQueue(imageUploadRequestDtos, uploadedBy);
        } catch (Exception e) {
            log.error("Error occurred while publishing to Azure queue: {} ", e.getMessage());
            throw e;
        }
    }

    @Override
    public AppResponse<Page<ImageResponseDto>> findAllByNameAndCreatedDate(String imageName,
            String startDate, String endDate, Integer page, Integer size) {

        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);
        log.info("size is {}, page is", pageRequest.getPageSize(), pageRequest.getPageNumber());

        var pagedData = imageInternalService.findAllByNameAndCreatedDate(imageName, fromDate, toDate,
                pageRequest);

        var mappedData = pagedData.getContent().stream().map(ImageHelper::buildImageResponse).toList();
        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey(IMAGE_FETCHED_MESSAGE_KEY),
                messageSourceService.getMessageByKey(IMAGE_FETCHED_MESSAGE_KEY),
                new PageImpl<>(mappedData, pageRequest, pagedData.getTotalElements()),
                null);
    }

    @Override
    public ByteArrayResource download(String imageName, String startDate, String endDate, Integer page, Integer size) {
        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);
        log.info("size is {}, page is", pageRequest.getPageSize(), pageRequest.getPageNumber());

        var pagedData = imageInternalService.findAllByNameAndCreatedDate(imageName, fromDate, toDate,
                pageRequest);

        var mappedData = pagedData.getContent().stream().map(ImageHelper::buildImageResponse).toList();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Images");
            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Image Name");
            headerRow.createCell(1).setCellValue("Image Url");

            int rowNum = 1;
            for (ImageResponseDto data : mappedData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.imageName());
                row.createCell(1).setCellValue(data.url());
            }
            workbook.write(out);

            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.download.of.image.failed"));
        }
    }

}
