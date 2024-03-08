package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.service.BrandService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/brand")
@Slf4j
public class BrandController {

    private final BrandService brandService;

    private final TraceService traceService;

    @PostMapping
    public ResponseEntity<AppResponse<BrandResponseDto>> createBrand(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid CreateBrandRequestDto requestDto) {

        traceService.propagateSleuthFields(traceId);
        log.info("logging request for create brand {}", requestDto);
        return ResponseEntity.ok()
                .body(brandService.createBrand(requestDto));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse<BrandResponseDto>> getBrand(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get brand by public id {}", publicId);
        return ResponseEntity.ok()
                .body(brandService.getBrandByPublicId(publicId));
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadBrands(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for download brands on page {} and size of {}", page, size);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Brand_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(brandService.download(page, size));
    }

    @GetMapping
    public ResponseEntity<AppResponse<Page<BrandResponseDto>>> getBrands(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get brand by public id on page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .body(brandService.getBrands(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<AppResponse<Page<BrandResponseDto>>> getBrands(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "manufacturerId", required = false) UUID manufacturerId,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "sortBy", required = true) SortCriteria sort) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get brands page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .body(brandService.getBrands(name, manufacturerId, page, size, sort));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse<BrandResponseDto>> editBrand(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId,
            @RequestBody @Valid EditBrandRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for edit brand {}", requestDto);
        return ResponseEntity.ok()
                .body(brandService.editBrand(publicId, requestDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse<Void>> deleteBrand(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete brand {}", publicId);
        return ResponseEntity.ok()
                .body(brandService.deleteBrand(publicId));
    }

    @GetMapping("/manufacture/{manufacturePublicId}")
    public ResponseEntity<AppResponse<Page<BrandResponseDto>>> getBrandsByManufacture(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @PathVariable(name = "manufacturePublicId") UUID manufacturePublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get brand by manufacture public id {}", manufacturePublicId);
        return ResponseEntity.ok()
                .body(brandService.getBrandsByManufacturer(manufacturePublicId, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<BrandResponse>>> filterBrand(
            @ModelAttribute(name = "brandFilter") @ParameterObject BrandFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(brandService.filterBrand(filter, pageable));
    }

}
