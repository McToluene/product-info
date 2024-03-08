package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.filter.search.ManufacturerFilter;
import com.mctoluene.productinformationmanagement.service.ManufacturerService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/manufacturer")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    private final TraceService traceService;

    @PostMapping()
    public ResponseEntity<AppResponse<ManufacturerResponseDto>> createManufacturer(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid CreateManufacturerRequestDto manufacturerRequestDto) {

        log.info("logging request for manufacturer {}", manufacturerRequestDto);

        AppResponse<ManufacturerResponseDto> manufacturer = manufacturerService
                .createManufacturer(manufacturerRequestDto);

        log.info("Manufacturer created successfully {}", manufacturer.getData());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(manufacturer.getData())
                .toUri();
        traceService.propagateSleuthFields(traceId);

        return ResponseEntity.created(location).body(manufacturer);

    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse<ManufacturerResponseDto>> getManufacturerByPublicId(
            @RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get Manufacturer by public id {}", publicId);
        return ResponseEntity.ok()
                .body(manufacturerService.getManufacturer(publicId));
    }

    @GetMapping
    public ResponseEntity<AppResponse<Page<ManufacturerResponseDto>>> getAllManufacturers(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get manufacturers on page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .body(manufacturerService.getAllManufacturers(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<AppResponse<Page<ManufacturerResponseDto>>> getAllManufacturers(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "name", defaultValue = "", required = false) String name,
            @RequestParam(name = "sortBy", required = true) SortCriteria sort) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get manufacturers on page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .body(manufacturerService.getAllManufacturers(name, page, size, sort));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse<ManufacturerResponseDto>> updateManufacturer(
            @RequestHeader("x-trace-id") UUID traceId,
            @PathVariable UUID publicId,
            @RequestBody @Valid UpdateManufacturerRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for update manufacturer {}", requestDto);
        return ResponseEntity.ok()
                .body(manufacturerService.updateManufacturer(publicId, requestDto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse<Void>> deleteManufacturer(
            @RequestHeader("x-trace-id") UUID traceId,
            @PathVariable UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete manufacturer {}", publicId);
        return ResponseEntity.ok()
                .body(manufacturerService.deleteManufacturer(publicId));
    }

    @PatchMapping("/{publicId}/disable")
    public ResponseEntity<AppResponse<ManufacturerResponseDto>> disableManufacturerStatus(
            @RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("publicId") UUID publicID) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request to disable status {}", publicID);
        return ResponseEntity.ok().body(manufacturerService.disableManufacturerStatus(publicID));
    }

    @PatchMapping("/{publicId}/enable")
    public ResponseEntity<AppResponse<ManufacturerResponseDto>> enableManufacturerStatus(
            @RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for enable manufacturer {}", publicId);
        return ResponseEntity.ok()
                .body(manufacturerService.enableManufacturerStatus(publicId));
    }

    @PostMapping("/upload")
    public ResponseEntity<AppResponse> uploadManufacturer(@RequestBody MultipartFile file,
            @RequestParam("createdBy") String createdBy,
            @RequestHeader("x-trace-id") UUID traceId) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok(manufacturerService.uploadManufacturerUsingExcel(file, createdBy, traceId));
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for download manufacturer on page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Manufacturer_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(manufacturerService.download(page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<ManufacturerResponse>>> filterManufacturer(
            @ModelAttribute(name = "manufacturerFilter") @ParameterObject ManufacturerFilter filter,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok()
                .body(manufacturerService.filterBrand(filter, pageable));
    }

}
