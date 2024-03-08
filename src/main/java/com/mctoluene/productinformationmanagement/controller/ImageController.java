package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageResponseDto;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.productinformationmanagement.service.ImageService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/v1/image")
public class ImageController {

    private final TraceService traceService;

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<AppResponse<List<ImageResponseDto>>> uploadImage(@RequestHeader("x-trace-id") UUID traceId,
            @ModelAttribute BulkUploadImageRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for upload image {}", requestDto);
        return ResponseEntity.ok()
                .body(imageService.uploadImages(requestDto));
    }

    @DeleteMapping("/{imageName}")
    public ResponseEntity<AppResponse<String>> deleteImage(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable String imageName) {

        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete image {}", imageName);
        return ResponseEntity.ok()
                .body(imageService.deleteImage(imageName));
    }

    @PostMapping("/upload-image-from-uri")
    public ResponseEntity<AppResponse<Image>> uploadImageFromUrl(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody ImageRequestDto imageRequestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for upload image from url {}", imageRequestDto);
        return ResponseEntity.ok()
                .body(imageService.uploadImageFromUrl(imageRequestDto));
    }

    @PostMapping(value = "/upload/uploadedBy/{uploadedBy}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AppResponse<Void>> uploadImageFile(@RequestParam("file") MultipartFile file,
            @PathVariable("uploadedBy") String uploadedBy,
            @RequestHeader("x-trace-id") UUID traceId) {

        traceService.propagateSleuthFields(traceId);
        AppResponse<Void> productUploadResponseDto = imageService.uploadFile(file, uploadedBy);
        return ResponseEntity.ok(productUploadResponseDto);
    }

    @GetMapping()
    public ResponseEntity<AppResponse<Page<ImageResponseDto>>> findAll(
            @RequestParam(required = false, defaultValue = "") String imageName,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestHeader("x-trace-id") UUID traceId) {
        traceService.propagateSleuthFields(traceId);
        AppResponse<Page<ImageResponseDto>> productUploadResponseDto = imageService.findAllByNameAndCreatedDate(
                imageName,
                fromDate,
                toDate, page, size);
        return ResponseEntity.ok(productUploadResponseDto);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(
            @RequestParam(required = false, defaultValue = "") String imageName,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestHeader("x-trace-id") UUID traceId) {
        traceService.propagateSleuthFields(traceId);
        ByteArrayResource resource = imageService.download(
                imageName,
                fromDate,
                toDate, page, size);

        return ResponseEntity
                .ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Image_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

}
