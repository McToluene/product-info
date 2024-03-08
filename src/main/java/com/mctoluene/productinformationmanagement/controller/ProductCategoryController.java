package com.mctoluene.productinformationmanagement.controller;

import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.UpdateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponse;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ProductCategoryFilter;
import com.mctoluene.productinformationmanagement.service.ProductCategoryService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/product-category")
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryController {

    private final TraceService traceService;
    private final ProductCategoryService productCategoryService;

    @PostMapping()
    public ResponseEntity<AppResponse> createProductCategory(
            @RequestBody @Valid CreateProductCategoryRequestDto requestDto) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for create product category {}", requestDto);

        return ResponseEntity.ok()
                .body(productCategoryService.createProductCategory(requestDto));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> updateProductCategory(
            @PathVariable("publicId") UUID publicId,
            @RequestBody @Valid UpdateProductCategoryRequestDto requestDto) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for update product category {}", requestDto);
        return ResponseEntity.ok()
                .body(productCategoryService.updateProductCategory(publicId, requestDto));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AppResponse> getProductCategoryByPublicId(@PathVariable("publicId") UUID publicId) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get product category {}", publicId);

        return ResponseEntity.ok()
                .body(productCategoryService.getProductCategoryByPublicId(publicId));
    }

    @GetMapping("/byCountry/{countryCode}")
    public ResponseEntity<AppResponse> getProductCategoryByCountryCode(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("countryCode") String countryCode) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get product category by country code {}", countryCode);
        return ResponseEntity.ok()
                .body(productCategoryService.getProductCategoryByCountryCode());
    }

    @PostMapping("/get-categories")
    public ResponseEntity<AppResponse> getProductCategoriesByPublicIds(
            @RequestBody List<UUID> publicIds) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        return ResponseEntity.ok().body(productCategoryService.getProductCategoriesByPublicIds(publicIds));
    }

    @GetMapping()
    public ResponseEntity<AppResponse> getAllProductCategories(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam("onlyParentCategory") Boolean onlyParentCategory) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get all product categories page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(productCategoryService.getAllProductCategories(page, size, onlyParentCategory));
    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse> getAllProductCategoriesFiltered(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "") String productCategoryName,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now().minusYears(5)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get all product categories page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(productCategoryService.getAllProductCategoriesFiltered(page, size, productCategoryName, startDate,
                        endDate));
    }

    @GetMapping("/{publicId}/children")
    public ResponseEntity<AppResponse> getAllDirectSubcategoryOfProductCategory(
            @PathVariable("publicId") UUID publicId,
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get all product categories page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(productCategoryService.getAllDirectSubcategoryOfProductCategory(publicId, page, size));
    }

    @GetMapping("/{productCategoryPublicId}/nested")
    public ResponseEntity<AppResponse> getAllNestedSubcategoryOfProductCategory(
            @PathVariable("productCategoryPublicId") UUID productCategoryPublicId) {

        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get all nested product categories {}", productCategoryPublicId);
        return ResponseEntity.ok()
                .body(productCategoryService.getAllNestedSubcategoryOfProductCategory(productCategoryPublicId));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<AppResponse> deleteProductCategoryByPublicId(
            @PathVariable("publicId") UUID publicId) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for delete product category");
        return ResponseEntity.ok()
                .body(productCategoryService.deleteProductCategory(publicId));
    }

    @PutMapping("/{categoryPublicId}/archive")
    public ResponseEntity<AppResponse> archiveProductCategory(
            @PathVariable("categoryPublicId") UUID categoryPublicId) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for archive product category");
        return ResponseEntity.ok()
                .body(productCategoryService.archiveProductCategory(categoryPublicId));
    }

    @PutMapping("/{categoryPublicId}/unarchive")
    public ResponseEntity<AppResponse> unArchiveProductCategory(
            @PathVariable("categoryPublicId") UUID categoryPublicId) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request to un-archive product category");
        return ResponseEntity.ok()
                .body(productCategoryService.unArchiveProductCategory(categoryPublicId));
    }

    @GetMapping("/with-subcategory")
    public ResponseEntity<AppResponse> getAllProductCategoriesWithImmediateSubcategory(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        traceService.propagateSleuthFields(UUID.fromString(RequestHeaderContextHolder.getContext().traceId()));
        log.info("logging request for get all product categories page {} and size {}", page, size);
        return ResponseEntity.ok()
                .body(productCategoryService.getProductCategories(page, size));
    }

    @GetMapping("/filter/all")
    public ResponseEntity<AppResponse<Page<ProductCategoryResponse>>> filterProductCategory(
            @ModelAttribute(name = "productCategoryFilter") @ParameterObject ProductCategoryFilter filter,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok()
                .body(productCategoryService.filterProductCategory(filter, pageable));
    }

}
