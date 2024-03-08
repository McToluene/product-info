package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.service.BrandService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brand")
@RequiredArgsConstructor
public class BrandUploadController {

    private final TraceService traceService;
    private final BrandService brandService;

    @PostMapping("/upload")
    public ResponseEntity<AppResponse> uploadBrandFile(@RequestBody MultipartFile file,
            @RequestParam("createdBy") String uploadedBy,
            @RequestParam("manufacturerId") UUID manufacturerId,
            @RequestHeader("x-trace-id") UUID traceId) throws IOException {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok(brandService.uploadBrandFile(file, uploadedBy, traceId, manufacturerId));
    }

    @PostMapping("/manufacturer/upload")
    public ResponseEntity<AppResponse> uploadBrandManufacturerFile(@RequestBody MultipartFile file,
            @RequestParam("createdBy") String uploadedBy,
            @RequestHeader("x-trace-id") UUID traceId) throws IOException {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok(brandService.uploadBrandManufacturerFile(file, uploadedBy, traceId));
    }
}
