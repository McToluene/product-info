package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.service.MeasuringUnitService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/measuring-unit")
public class MeasuringUnitController {

    private final TraceService traceService;

    private final MeasuringUnitService measuringUnitService;

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse> getMeasuringUnitById(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get Measuring Unit by public id {}", publicId);
        return ResponseEntity.ok()
                .body(measuringUnitService.getMeasuringUnitById(publicId));
    }

    @GetMapping
    public ResponseEntity<AppResponse> getAllMeasuringUnits(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get measuring units  {} {}", page, size);
        return ResponseEntity.ok()
                .body(measuringUnitService.getAllMeasuringUnits(page, size));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> editMeasuringUnit(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId,
            @RequestBody @Valid UpdateMeasuringUnitRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for edit measuring unit {}", requestDto);
        return ResponseEntity.ok()
                .body(measuringUnitService.editMeasuringUnit(publicId, requestDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse> deleteMeasuringUnit(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete measuring unit {}", publicId);
        return ResponseEntity.ok()
                .body(measuringUnitService.deleteMeasuringUnit(publicId));
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for download measuring unit on page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Measuring_Unit_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(measuringUnitService.download(page, size));
    }
}
