package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.service.ProductUploadService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductUploadController {

    private final ProductUploadService productUploadService;

    private final TraceService traceService;

    @PostMapping("/upload")
    public ResponseEntity<AppResponse> uploadGenericProductFile(@RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader(value = "x-country-code") String code) {
        traceService.propagateSleuthFields(traceId);
        AppResponse productUploadResponseDto = productUploadService.uploadGenericProductFile(file, uploadedBy, traceId,
                code);

        return ResponseEntity.ok(productUploadResponseDto);
    }

    @PostMapping("/upload/brand/{brandPublicId}")
    public ResponseEntity<AppResponse> uploadProductsFileByBrandPublicId(@RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader(value = "x-country-code") String code,
            @PathVariable("brandPublicId") UUID brandPublicId) {
        traceService.propagateSleuthFields(traceId);
        AppResponse productUploadResponseDto = productUploadService.uploadGenericProductFile(file, uploadedBy, traceId,
                code, brandPublicId);

        return ResponseEntity.ok(productUploadResponseDto);
    }

}
