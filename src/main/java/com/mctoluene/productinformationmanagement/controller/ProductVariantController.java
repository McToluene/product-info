package com.mctoluene.productinformationmanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.request.product.ApproveRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.RejectVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.*;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponse;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantAwaitingApprovalResponse;
import com.mctoluene.productinformationmanagement.filter.search.ProductVariantFilter;
import com.mctoluene.productinformationmanagement.filter.search.VariantAwaitingApprovalFilter;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantService;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/variant")
@RequiredArgsConstructor
@Slf4j
public class ProductVariantController {

    private final TraceService traceService;

    private final VariantService variantService;

    @PutMapping("/{publicId}")
    public ResponseEntity<AppResponse> updateVariant(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("publicId") UUID publicId,
            @RequestBody @Valid UpdateVariantRequestDto requestDto) {
        log.info("logging request for update variant {}", requestDto);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.updateVariant(publicId, requestDto));
    }

    @GetMapping
    public ResponseEntity<AppResponse> getAllVariants(@RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) List<String> status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "isVated", required = false) Boolean isVated) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variants page {} and size of {}", page, size);
        return ResponseEntity.ok()
                .body(variantService.getAllVariants(searchParam, countryCode, fromDate, toDate, status, page, size,
                        isVated));
    }

    @GetMapping("/by-sku/{sku}")
    public ResponseEntity<AppResponse> getVariantBySku(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "sku") String sku) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant by sku {}", sku);
        return ResponseEntity.ok().body(variantService.findVariantBySku(sku));
    }

    @GetMapping("/internal/{sku}")
    public ResponseEntity<AppResponse> getVariantCompleteResponseBySku(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "sku") String sku) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant by sku with value {}", sku);
        return ResponseEntity.ok().body(variantService.getVariantBySku(sku));
    }

    @GetMapping("/{variantPublicId}")
    public ResponseEntity<AppResponse> getVariantByPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "variantPublicId") UUID variantPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant by public id {}", variantPublicId);
        return ResponseEntity.ok()
                .body(variantService.getVariantByPublicId(variantPublicId));
    }

    @PostMapping("/get-by-publicIds")
    public ResponseEntity<AppResponse> getVariantsByPublicIds(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<UUID> variantPublicIdList) {
        log.info("Logging request to get variants by ids {}", variantPublicIdList);
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getProductVariantsByPublicIds(variantPublicIdList));
    }

    @PostMapping("/search-by-publicIds/")
    public ResponseEntity<AppResponse> searchVariantsByPublicIds(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(name = "searchParam", required = false) String searchParam,
            @RequestBody List<UUID> variantPublicIdList) {
        log.info("Logging request to get variants by ids {} and search param {}", variantPublicIdList, searchParam);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantService.searchProductVariantsByPublicIds(searchParam, variantPublicIdList));
    }

    @GetMapping("/by-product-category/{publicId}")
    public ResponseEntity<AppResponse> getVariantsByProductCategoryPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("publicId") UUID publicId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        traceService.propagateSleuthFields(traceId);

        return ResponseEntity.ok()
                .body(variantService.getVariantsByProductCategoryPublicId(publicId, page, size));
    }

    @PostMapping("/get-by-sku-list")
    public ResponseEntity<AppResponse> getVariantsBySkuList(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<String> variantSkuList) {

        log.info("Logging request to get variants by SKUs {}", variantSkuList);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getVariantsBySkuList(variantSkuList));
    }

    @PostMapping("/search-variants")
    public ResponseEntity<AppResponse> searchVariants(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam String searchValue,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Logging request to get variants on page {} and size {}", page, size);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.searchVariants(searchValue, page, size));
    }

    @PostMapping("/get-by-sku-list/ids")
    public ResponseEntity<AppResponse> getVariantIdsBySkuList(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<String> variantSkuList) {
        log.info("Logging request to get variants by SKUs {}", variantSkuList);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getVariantIdsBySkuList(variantSkuList));
    }

    @GetMapping("/product/{productPublicId}")
    public ResponseEntity<AppResponse> getVariantsByProductPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "productPublicId") UUID productPublicId) {
        log.info("Logging request to get variant by product publicID {}", productPublicId);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getVariantsByProductPublicId(productPublicId));
    }

    @GetMapping("/awaiting-approval")
    public ResponseEntity<AppResponse> getVariantsAwaitingApproval(@RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false, defaultValue = "PENDING") String approvalStatus,
            @RequestParam(required = false) List<String> listOfStatus,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Logging request to get variants awaiting approval on page {} and size {}", page, size);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getVariantsAwaitingApproval(searchParam, countryCode, from, to,
                approvalStatus, listOfStatus, page, size));
    }

    @GetMapping("/rejected")
    public ResponseEntity<AppResponse> getRejectedVariants(@RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("Logging request to get rejected variants on page {} and size {}", page, size);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantService.getRejectedVariants(searchParam, countryCode, from, to, page, size));
    }

    @GetMapping("/missing-images")
    public ResponseEntity<AppResponse> getVariantsWithMissingImages(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Logging request to get variantswith missing images on page {} and size {}", page, size);

        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getVariantsWithMissingImages(searchParam, from, to, page, size));
    }

    @PutMapping("/{variantPublicId}/approve")
    public ResponseEntity<AppResponse> approveProductVariant(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "variantPublicId") UUID variantPublicId,
            @RequestBody @Valid ApproveRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for approve ProductVariant {}", variantPublicId);
        return ResponseEntity.ok()
                .body(variantService.approveVariantAwaitingApproval(variantPublicId, requestDto));
    }

    @PutMapping("/{variantPublicId}/reject")
    public ResponseEntity<AppResponse> rejectVariant(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "variantPublicId") UUID variantPublicId,
            @RequestBody @Valid RejectVariantRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for getting rejected ProductVariant with id {}", variantPublicId);
        return ResponseEntity.ok()
                .body(variantService.rejectVariantAwaitingApproval(variantPublicId, requestDto));
    }

    @PostMapping("/get-by-sku-list/approved/unapproved/ids")
    public ResponseEntity<AppResponse> getApprovedAndUnApprovedVariantIdsBySkuList(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<String> variantSkuList) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getApprovedAndUnApprovedVariantIdsBySkuList(variantSkuList));
    }

    @PutMapping("/awaiting-approval/{variantAwaitingApprovalPublicId}")
    public ResponseEntity<AppResponse> updateVariantAwaitingApproval(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("variantAwaitingApprovalPublicId") UUID variantAwaitingApprovalPublicId,
            @RequestBody @Valid EditVariantAwaitingApprovalRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for update variants awaiting approval {} {}",
                variantAwaitingApprovalPublicId, requestDto);
        return ResponseEntity.ok().body(variantService.editVariantAwaitingApproval(
                variantAwaitingApprovalPublicId, requestDto));

    }

    @PutMapping("/archive/{productVariantPublicId}")
    public ResponseEntity<AppResponse> archiveProductVariant(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("productVariantPublicId") UUID productVariantPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for a archive product variant {}", productVariantPublicId);
        return ResponseEntity.ok()
                .body(variantService.archiveProductVariant(productVariantPublicId));
    }

    @PutMapping("/unarchive/{productVariantPublicId}")
    public ResponseEntity<AppResponse> unArchiveProductVariant(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable("productVariantPublicId") UUID productVariantPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for a un-archive product variant {}", productVariantPublicId);
        return ResponseEntity.ok()
                .body(variantService.unarchiveProductVariant(productVariantPublicId));
    }

    @PostMapping("/byCategoryPublicIds")
    public ResponseEntity<AppResponse> getAllVariantsByCategoryPublicIds(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<UUID> categoryPublicIds,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) List<String> status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size)
            throws JsonProcessingException {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variants  {} {}", page, size);
        return ResponseEntity.ok().body(variantService.getAllVariantsByCategoryPublicIds(searchParam, categoryPublicIds,
                fromDate, toDate, status, page, size));
    }

    @PostMapping("/get-by-publicIds/filter")
    public ResponseEntity<AppResponse> getVariantsByPublicIdsAndFilter(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid VariantFilterRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.getProductVariantsByPublicIdAndStatusAndFilter(requestDto));
    }

    @PostMapping("/byCategoriesMap")
    public ResponseEntity<AppResponse> getAllVariantsByCategoryPublicIdMap(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody Map<UUID, BigDecimal> markUpMap) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variants by categoryPublicIdMap {}", markUpMap);
        return ResponseEntity.ok().body(variantService.getAllVariantsByCategoryPublicIdsMap(markUpMap));
    }

    @GetMapping("/variant-awaiting/{variantPublicId}")
    public ResponseEntity<AppResponse> getVariantByVariantAwaitingPublicId(@RequestHeader("x-trace-id") UUID traceId,
            @PathVariable(name = "variantPublicId") UUID variantPublicId) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variant by public id {}", variantPublicId);
        return ResponseEntity.ok()
                .body(variantService.getVariantAwaitingByPublicId(variantPublicId));
    }

    @PostMapping("/search-by-sku-product-name")
    public ResponseEntity<AppResponse> searchVariantsBySkuAndProductName(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody List<String> skuCodes,
            @RequestParam(required = false, defaultValue = "") String searchValue,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantService.searchVariantBySkuListAndProductName(skuCodes, searchValue, page, size));
    }

    @GetMapping("/stockOneGetProducts")
    public ResponseEntity<AppResponse> getProducts(@RequestHeader("x-trace-id") UUID traceId,
            @RequestParam("warehouseName") String warehouseName,
            @RequestParam(value = "skuCode", required = false) String skuCode,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok()
                .body(variantService.getProductsFromStockOne(traceId.toString(), warehouseName, skuCode, size, page));
    }

    @PutMapping("/live-inventory")
    public ResponseEntity<AppResponse> editLiveInventory(@RequestHeader("x-trace-id") UUID traceId,
            @RequestBody @Valid EditLiveInventoryRequestDto requestDto) {
        traceService.propagateSleuthFields(traceId);
        return ResponseEntity.ok().body(variantService.editThresholdAndLeadTime(requestDto));
    }

    @PostMapping("/vat-values-ratio")
    public ResponseEntity<AppResponse> getVatValuesRatioByProductVariantsPublicId(
            @RequestHeader("x-trace-id") UUID traceId,
            @RequestBody GetProductVariantVatRequest request) {
        traceService.propagateSleuthFields(traceId);
        AppResponse<Map<String, ProductVariantVatResponseDto>> response = variantService
                .getVatRatiosByProductVariantPublicIds(request.getProductVariantPublicIds(),
                        request.getProductVariantSkus());

        if (response.getData().isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadVariants(@RequestHeader("x-trace-id") UUID traceId,
            @RequestHeader("x-country-code") String countryCode,
            @RequestParam(defaultValue = "", required = false) String searchParam,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) List<String> status,
            @RequestParam(value = "isVated", required = false) Boolean isVated,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        traceService.propagateSleuthFields(traceId);
        log.info("logging request for get variants page {} and size of {}", page, size);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Product_Variant_Export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(variantService.download(searchParam, countryCode, fromDate, toDate, status, isVated, page, size));
    }

    @GetMapping("/validate-skus")
    public ResponseEntity<AppResponse<Map<String, ProductSkuDto>>> validateSkus(@RequestParam("skus") List<String> skus,
            @RequestHeader("x-trace-id") UUID traceId) {

        traceService.propagateSleuthFields(traceId);
        AppResponse<Map<String, ProductSkuDto>> response = variantService.validateSkus(skus);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/filter")
    public ResponseEntity<AppResponse<Page<ProductVariantResponse>>> filterProductVariant(
            @ModelAttribute(name = "productVariantFilter") @ParameterObject ProductVariantFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(variantService.filterProductVariant(filter, pageable));
    }

    @GetMapping("/awaiting-approval/filter")
    public ResponseEntity<AppResponse<Page<VariantAwaitingApprovalResponse>>> filterVariantAwaitingVariant(
            @ModelAttribute(name = "productVariantFilter") @ParameterObject VariantAwaitingApprovalFilter filter,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok()
                .body(variantService.filterVariantAwaitingApproval(filter, pageable));
    }
}
