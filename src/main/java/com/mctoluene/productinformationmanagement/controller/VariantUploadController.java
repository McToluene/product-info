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
@RequestMapping("api/v1/variant")
@RequiredArgsConstructor
@Slf4j
public class VariantUploadController {

    private final ProductUploadService productUploadService;

    private final TraceService traceService;

    @PostMapping("/upload/{productPublicId}/variants")
    public ResponseEntity<AppResponse> uploadVariantFileByProductPublicId(@RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @PathVariable("productPublicId") UUID productPublicId) {
        traceService.propagateSleuthFields(traceId);
        AppResponse productVariantUploadResponseDto = productUploadService.uploadGenericProductVariantFile(file,
                uploadedBy,
                traceId, countryCode, productPublicId);

        return ResponseEntity.ok(productVariantUploadResponseDto);
    }
}
