package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.UpdateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.VariantTypeFilter;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantTypeService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/variant-type")
@RequiredArgsConstructor
@Slf4j
public class VariantTypeController {
    private final TraceService traceService;
    private final VariantTypeService variantTypeService;

    @PostMapping
    public ResponseEntity<AppResponse> createVariantType(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid CreateVariantTypeRequestDto requestDto) {
        log.info("logging request for create variant type {}", requestDto);

        AppResponse variantType = variantTypeService.createVariantType(requestDto);

        log.info("Variant type created successfully, {}", variantType);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(variantType.getData())
                .toUri();
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.created(location).body(variantType);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse> getVariantTypeByPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant type by public id {}", publicId);
        return ResponseEntity.ok()
                .body(variantTypeService.getVariantTypeById(publicId));
    }

    @GetMapping
    public ResponseEntity<AppResponse> getVariantTypes(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "", required = false) String searchParam) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant types on page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(variantTypeService.getAllVariantTypes(searchParam, page, size));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> updateVariantType(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId,
            @RequestBody @Valid UpdateVariantTypeRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for update variant type {}", requestDto);
        return ResponseEntity.ok()
                .body(variantTypeService.updateVariantType(requestDto, publicId));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse> deleteVariantType(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete variant type {}", publicId);
        return ResponseEntity.ok()
                .body(variantTypeService.deleteVariantType(publicId));
    }

    @GetMapping("/by-name")
    public ResponseEntity<AppResponse> getVariantTypes(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam String name) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant types by name {} ", name);
        return ResponseEntity.ok()
                .body(variantTypeService.getVariantTypeByName(name));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<VariantTypeResponse>>> filterVariantType(
            @ModelAttribute(name = "variantTypeFilter") @ParameterObject VariantTypeFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(variantTypeService.filterVariantType(filter, pageable));
    }

}
