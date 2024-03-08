package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.WarrantyTypeFilter;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.WarrantyTypeService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/warranty-type")
@Slf4j
public class WarrantyTypeController {

    private final WarrantyTypeService warrantyTypeService;
    private final TraceService traceService;

    @PostMapping
    public ResponseEntity<AppResponse> createWarrantyType(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid CreateWarrantyTypeRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for create warranty type {}", requestDto);
        return ResponseEntity.ok()
                .body(warrantyTypeService.createWarrantyType(requestDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse> deleteWarrantyType(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete warranty-type {}", publicId);
        return ResponseEntity.ok()
                .body(warrantyTypeService.deleteWarrantyType(publicId));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse> getWarrantyTypeByPublicId(@PathVariable(name = "publicId") UUID publicId,
            @RequestHeader("x-trace-id") UUID traceId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get warranty type by public id {}", publicId);
        return ResponseEntity.ok()
                .body(warrantyTypeService.getWarrantyTypeById(publicId));

    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> updateWarrantyType(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable UUID publicId,
            @RequestBody @Valid UpdateWarrantyTypeRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for edit warranty type {}", requestDto);
        return ResponseEntity.ok()
                .body(warrantyTypeService.updateWarrantyType(publicId, requestDto));
    }

    @GetMapping
    public ResponseEntity<AppResponse> getWarrantyTypes(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get all warranty types page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(warrantyTypeService.getAllWarrantyTypes(page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<WarrantyTypeResponse>>> filterWarrantyType(
            @ModelAttribute(name = "warrantyTypeFilter") @ParameterObject WarrantyTypeFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(warrantyTypeService.filterWarrantyType(filter, pageable));
    }

}
