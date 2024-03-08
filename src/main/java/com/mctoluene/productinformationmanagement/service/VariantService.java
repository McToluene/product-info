package com.mctoluene.productinformationmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.request.product.ApproveRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.RejectVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditLiveInventoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.UpdateVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.VariantFilterRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantDetailResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantResponse;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantAwaitingApprovalResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ProductVariantFilter;
import com.mctoluene.productinformationmanagement.filter.search.VariantAwaitingApprovalFilter;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VariantService {
    AppResponse findVariantBySku(String sku);

    AppResponse updateVariant(UUID publicId, UpdateVariantRequestDto requestDto);

    AppResponse<Page<ProductVariantDetailResponseDto>> getAllVariants(String searchParam, String countryCode,
            String startDate, String endDate,
            List<String> listOfStatus, Integer page, Integer size,
            Boolean isVated);

    AppResponse getVariantByPublicId(UUID publicId);

    AppResponse searchVariants(String searchValue, Integer page, Integer size);

    AppResponse getVariantsByProductCategoryPublicId(UUID publicId, Integer page, Integer size);

    AppResponse getVariantsBySkuList(List<String> skuList);

    AppResponse getProductVariantsByPublicIds(List<UUID> variantPublicIdList);

    AppResponse searchProductVariantsByPublicIds(String searchParam, List<UUID> variantPublicIdList);

    AppResponse getVariantIdsBySkuList(List<String> skuList);

    AppResponse getVariantsByProductPublicId(UUID productPublicId);

    AppResponse getVariantBySku(String sku);

    AppResponse approveVariantAwaitingApproval(UUID publicId, ApproveRequestDto requestDto);

    AppResponse deleteVariant(UUID publicId);

    AppResponse getVariantsAwaitingApproval(String searchParam, String countryCode, String startDate, String endDate,
            String approvalStatus,
            List<String> listOfStatus, Integer page, Integer size);

    AppResponse getRejectedVariants(String searchParam, String countryCode, String from, String to, Integer page,
            Integer size);

    AppResponse rejectVariantAwaitingApproval(UUID publicId, RejectVariantRequestDto requestDto);

    AppResponse getVariantsWithMissingImages(String searchParam, String from, String to, Integer page, Integer size);

    AppResponse getApprovedAndUnApprovedVariantIdsBySkuList(List<String> variantSkuList);

    AppResponse editVariantAwaitingApproval(UUID variantAwaitingApprovalPublicId,
            EditVariantAwaitingApprovalRequestDto requestDto);

    AppResponse archiveProductVariant(UUID productVariantPublicId);

    AppResponse unarchiveProductVariant(UUID productVariantPublicId);

    AppResponse getAllVariantsByCategoryPublicIds(String searchParam, List<UUID> categoryPublicIds, String startDate,
            String endDate,
            List<String> listOfStatus, Integer page, Integer size) throws JsonProcessingException;

    AppResponse getProductVariantsByPublicIdAndStatusAndFilter(VariantFilterRequestDto requestDto);

    AppResponse getAllVariantsByCategoryPublicIdsMap(Map<UUID, BigDecimal> categoryMarkUps);

    AppResponse getVariantAwaitingByPublicId(UUID publicId);

    AppResponse searchVariantBySkuListAndProductName(List<String> skuCodes, String searchValue, Integer page,
            Integer size);

    AppResponse getProductsFromStockOne(String traceId, String warehouseName, String skuCode, Integer size,
            Integer page);

    AppResponse editThresholdAndLeadTime(EditLiveInventoryRequestDto requestDto);

    AppResponse<Map<String, ProductVariantVatResponseDto>> getVatRatiosByProductVariantPublicIds(
            List<UUID> productVariantPublicIds, List<String> productVariantSkus);

    ByteArrayResource download(String searchParam, String countryCode, String fromDate, String toDate,
            List<String> listOfStatus, Boolean isVated, Integer page, Integer size);

    AppResponse<Map<String, ProductSkuDto>> validateSkus(List<String> skus);

    AppResponse<Page<ProductVariantResponse>> filterProductVariant(ProductVariantFilter productVariantFilter,
            Pageable pageable);

    AppResponse<Page<VariantAwaitingApprovalResponse>> filterVariantAwaitingApproval(
            VariantAwaitingApprovalFilter filter, Pageable pageable);

}
