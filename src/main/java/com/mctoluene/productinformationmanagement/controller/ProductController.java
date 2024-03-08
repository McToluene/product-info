package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.UpdateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ProductFilter;
import com.mctoluene.productinformationmanagement.service.FailedProductService;
import com.mctoluene.productinformationmanagement.service.ProductService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final TraceService traceService;

    private final ProductService productService;

    private final FailedProductService failedProductService;

    @PostMapping
    public ResponseEntity<AppResponse> createProduct(@RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @RequestBody @Valid CreateProductRequestDto requestDto) {
        log.info("logging request for create product {}", requestDto);

        AppResponse product = productService.createNewProduct(requestDto, Boolean.FALSE, countryCode);

        log.info("Product created successfully {}", product.getData());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getData())
                .toUri();
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.created(location).body(product);
    }

    @GetMapping("/{productPublicId}")
    public ResponseEntity<AppResponse> getProductByPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "productPublicId") UUID productPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get product {}", productPublicId);
        return ResponseEntity.ok().body(productService.getProductByPublicId(productPublicId));
    }

    @GetMapping
    public ResponseEntity<AppResponse> getAllProducts(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) List<UUID> categoryPublicIds,
            @RequestParam(required = false) List<UUID> brandPublicIds,
            @RequestParam(required = false) List<UUID> manufacturerPublicIds,
            @RequestParam(required = false) List<UUID> warrantyTypePublicIds,
            @RequestParam(required = false) List<UUID> measuringUnitPublicIds) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get products  {} {}", page, size);
        return ResponseEntity.ok().body(productService.getAllProducts(page, size, searchParam, fromDate, toDate,
                categoryPublicIds, brandPublicIds, manufacturerPublicIds, warrantyTypePublicIds,
                measuringUnitPublicIds));
    }

    @PostMapping("/get-for-categories")
    public ResponseEntity<AppResponse> getProductsByProductCategoryIds(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<UUID> categoriesPublisList,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "1") Integer size) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(productService.getProductsByProductCategoryIds(categoriesPublisList, page, size));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse> deleteProduct(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "publicId") UUID publicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for delete product {}", publicId);
        return ResponseEntity.ok()
                .body(productService.deleteProduct(publicId));
    }

    @PutMapping("/{productPublicId}/archive")
    public ResponseEntity<AppResponse> archiveProduct(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "productPublicId") UUID productPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for archive product {}", productPublicId);
        return ResponseEntity.ok()
                .body(productService.updateProductArchiveStatus(productPublicId, Status.INACTIVE.name()));
    }

    @PutMapping("/{productPublicId}/unarchive")
    public ResponseEntity<AppResponse> unarchiveProduct(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "productPublicId") UUID productPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for unarchive product {}", productPublicId);
        return ResponseEntity.ok()
                .body(productService.updateProductArchiveStatus(productPublicId, Status.ACTIVE.name()));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> updateProductByPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("publicId") UUID publicId,
            @RequestBody UpdateProductRequestDto updateProductRequestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for update product by publicId {}", updateProductRequestDto);
        return ResponseEntity.ok()
                .body(productService.updateProductByPublicId(publicId, updateProductRequestDto));
    }

    @GetMapping("/category/id-list/{categoryId}")
    public ResponseEntity<AppResponse> getListOfProductIdsByProductCategory(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("categoryId") UUID categoryId) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(productService.getProductsByProductCategory(categoryId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<AppResponse> getProductsByProductCategoryId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("categoryId") UUID categoryId,
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(productService.getProductsByProductCategoryId(categoryId, page, size));
    }

    @PostMapping("/approved-list")
    public ResponseEntity<AppResponse> checkValidProductsList(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<UUID> productPublicIdsList) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(productService.getApprovedProductsByPublicIdList(productPublicIdsList));
    }

    @PostMapping("/sku/approved-list")
    public ResponseEntity<AppResponse> getApprovedProductsBySkuList(@RequestHeader("x-trace-id") UUID traceId,

            @RequestBody List<String> skuList) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(productService.getApprovedProductsPublicIdListUsingSku(skuList));
    }

    @GetMapping("/brand/{brandPublicId}")
    public ResponseEntity<AppResponse> getProductsByBrandPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("brandPublicId") UUID brandPublicId) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(productService.getProductsByBrand(brandPublicId));
    }

    @PostMapping("/link-using-excel")
    public ResponseEntity<AppResponse> bulkUploadProductAndVariantFile(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam("createdBy") String createdBy,
            @RequestBody MultipartFile file) throws IOException {

        traceService.propagateSleuthFields(traceId);
        log.info("logging request to upload product  {} using excel", file);
        return ResponseEntity.ok(productService.uploadProductUsingExcel(file, createdBy, traceId));

    }

    @GetMapping("/product-catalogue")
    public ResponseEntity<AppResponse> createProductCatalogue(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(value = "warehouseId", required = false) UUID warehouseId,
            @RequestParam(value = "stateId", required = false) UUID stateId,
            @RequestParam(value = "cityId", required = false) UUID cityId,
            @RequestParam(value = "lgaId", required = false) UUID lgaId,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok(productService.createProductCatalogue(traceId, warehouseId, stateId, cityId, lgaId,
                searchValue, page, size));
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) List<UUID> categoryPublicIds,
            @RequestParam(required = false) List<UUID> brandPublicIds,
            @RequestParam(required = false) List<UUID> manufacturerPublicIds,
            @RequestParam(required = false) List<UUID> warrantyTypePublicIds,
            @RequestParam(required = false) List<UUID> measuringUnitPublicIds) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get products  {} {}", page, size);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Product_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(productService.download(page, size, searchParam, fromDate, toDate,
                        categoryPublicIds, brandPublicIds, manufacturerPublicIds, warrantyTypePublicIds,
                        measuringUnitPublicIds));
    }

    @GetMapping("/failedUpload")
    public ResponseEntity<AppResponse> getFailedProducts(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false, name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false, name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get products  {} {}", page, size);
        return ResponseEntity.ok().body(failedProductService.getFailedProducts(searchParam, from, to, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<ProductResponse>>> filterProduct(
            @ModelAttribute(name = "productFilter") @ParameterObject ProductFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(productService.filterProduct(filter, pageable));
    }
}
