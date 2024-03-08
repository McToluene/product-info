package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.variantlocation.VariantLocationRequestdto;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantLocationService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/variant-location")
public class VariantLocationController {

    private final VariantLocationService variantLocationService;
    private final TraceService traceService;

    @PostMapping()
    public ResponseEntity<AppResponse> linkVariantToLocation(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid VariantLocationRequestdto variantLocationRequestdto) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantLocationService.linkVariantToLocation(variantLocationRequestdto));
    }

    @GetMapping("/search/product")
    public ResponseEntity<AppResponse> searchProductByQuery(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "statePublicId", required = false) UUID statePublicId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantLocationService.searchProductByQuery(query, statePublicId, page, size));
    }
}
