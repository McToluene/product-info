package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.CreateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogVariantAwaitingApprovalDto;
import com.mctoluene.productinformationmanagement.service.ImageCatalogService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/image-catalog")
@RequiredArgsConstructor
@Slf4j
public class ImageCatalogController {

    private final TraceService traceService;
    private final ImageCatalogService imageCatalogService;

    @PostMapping
    public ResponseEntity<AppResponse> createImageCatalog(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid CreateImageCatalogRequestDto requestDto) {

        log.info("logging request for create image catalog {}", requestDto);

        AppResponse imageCatalog = imageCatalogService.createImageCatalog(requestDto);

        log.info("Image catalog created :: {}", imageCatalog.getData());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(imageCatalog.getData())
                .toUri();
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.created(location).body(imageCatalog);
    }

    @PostMapping("/awaiting-variants")
    public ResponseEntity<AppResponse> addImageToAwaitingVariant(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid ImageCatalogVariantAwaitingApprovalDto requestDto) {

        log.info("logging request for adding image catalog to variant awaiting approval {}", requestDto);

        AppResponse imageCatalog = imageCatalogService.createImageCatalogForVariantAwaitingApproval(requestDto);

        log.info("Image catalog added successfully to {}, response:: {}",
                requestDto.getPublicVariantAwaitingApprovalId(),
                imageCatalog.getData());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(imageCatalog.getData())
                .toUri();
        traceService.propagateSleuthFields(traceId);

        return ResponseEntity.created(location).body(imageCatalog);
    }

    @GetMapping("/imageName/{imageName}")
    public ResponseEntity<AppResponse> getImageCatalogByImageName(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "imageName") String imageName) {
        traceService.propagateSleuthFields(traceId);

        log.info("logging request for get ImageCatalog by imageName {}", imageName);

        return ResponseEntity.ok()
                .body(imageCatalogService.getImageCatalogByImageName(imageName));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse> getImageById(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get Image by public id {}", publicId);
        return ResponseEntity.ok()
                .body(imageCatalogService.getImageById(publicId));
    }
}
