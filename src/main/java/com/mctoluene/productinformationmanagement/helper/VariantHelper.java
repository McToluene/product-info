package com.mctoluene.productinformationmanagement.helper;

import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.variant.CreateVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditLiveInventoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.*;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantDetailResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.VariantProductResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.VariantResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantsAwaitingApprovalResponseDto;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

public class VariantHelper {
    private VariantHelper() {

    }

    public static VariantsAwaitingApprovalResponseDto buildVariantAwaitingApprovalResponse(
            VariantAwaitingApproval variantAwaitingApproval) {
        return VariantsAwaitingApprovalResponseDto.builder()
                .id(variantAwaitingApproval.getId())
                .approvalStatus(variantAwaitingApproval.getApprovalStatus())
                .variantTypeName(variantAwaitingApproval.getVariantType().getVariantTypeName())
                .publicID(variantAwaitingApproval.getPublicId())
                .variantTypeId(variantAwaitingApproval.getVariantType().getId())
                .variantName(variantAwaitingApproval.getVariantName().trim())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .version(variantAwaitingApproval.getVersion())
                .variantTypeDescription(variantAwaitingApproval.getVariantType().getDescription())
                .variantTypeStatus(variantAwaitingApproval.getVariantType().getStatus())
                .variantDescription(variantAwaitingApproval.getVariantDescription())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .sku(variantAwaitingApproval.getSku())
                .status(variantAwaitingApproval.getStatus())
                .leadTime(variantAwaitingApproval.getLeadTime())
                .threshold(variantAwaitingApproval.getThreshold())
                .createdBy(variantAwaitingApproval.getCreatedBy())
                .createdDate(variantAwaitingApproval.getCreatedDate())
                .lastModifiedBy(variantAwaitingApproval.getLastModifiedBy())
                .lastModifiedDate(variantAwaitingApproval.getLastModifiedDate())
                .completedBy(variantAwaitingApproval.getCompletedBy())
                .countryId(variantAwaitingApproval.getCountryId())
                .completedDate(variantAwaitingApproval.getCompletedDate())
                .vatValue(variantAwaitingApproval.getVatValue())
                .build();
    }

    public static boolean validatePage(int page, int size) {
        return page >= 1 && size > 0;
    }

    public static VariantAwaitingApproval buildVariantAwaitingApproval(CreateVariantAwaitingApprovalRequestDto request,
            CountryDto countryDto, Product product, VariantType variantType) {
        VariantAwaitingApproval variant = new VariantAwaitingApproval();
        variant.setPublicId(UUID.randomUUID());
        variant.setVariantType(variantType);
        variant.setProduct(product);
        variant.setVariantName(
                WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(request.getVariantName().trim())));
        variant.setVariantDescription(request.getVariantDescription());
        variant.setCostPrice(Objects.isNull(request.getCostPrice()) ? BigDecimal.ZERO : request.getCostPrice());
        variant.setStatus(Status.ACTIVE.name());
        variant.setCreatedBy(request.getCreatedBy());
        variant.setLastModifiedBy(request.getCreatedBy());
        variant.setCreatedDate(LocalDateTime.now());
        variant.setLastModifiedDate(LocalDateTime.now());
        variant.setApprovalStatus(ApprovalStatus.PENDING.name());
        variant.setVersion(BigInteger.ZERO);
        variant.setCountryId(countryDto.publicId());
        variant.setIsVated(product.getVated());
        variant.setWeight(Objects.isNull(request.getWeight()) ? 0.0 : request.getWeight());
        return variant;

    }

    public static VariantVersion buildVariantVersion(VariantAwaitingApproval requestDto) {
        VariantVersion variantVersion = new VariantVersion();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setVariantName(requestDto.getVariantName().trim());
        variantVersion.setVariantDescription(requestDto.getVariantDescription());
        variantVersion.setDefaultImageUrl(requestDto.getDefaultImageUrl());
        variantVersion.setProduct(requestDto.getProduct());
        variantVersion.setSku(requestDto.getSku());
        variantVersion.setApprovedDate(LocalDateTime.now());
        variantVersion.setApprovedBy(requestDto.getCompletedBy());
        variantVersion.setVariantType(requestDto.getVariantType());
        variantVersion.setLeadTime(requestDto.getLeadTime());
        variantVersion.setThreshold(requestDto.getThreshold());
        variantVersion.setStatus(Status.ACTIVE.name());
        variantVersion.setCostPrice(requestDto.getCostPrice());
        variantVersion.setVersion(requestDto.getVersion());
        variantVersion.setDefaultImageUrl(requestDto.getDefaultImageUrl());
        variantVersion.setApprovalStatus(ApprovalStatus.APPROVED.name());
        return variantVersion;

    }

    public static ProductVariant buildProductVariant(VariantAwaitingApproval requestDto) {
        ProductVariant productVariant = new ProductVariant();
        productVariant.setVariantName(
                WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(requestDto.getVariantName().trim())));
        productVariant.setPublicId(UUID.randomUUID());
        productVariant.setStatus(Status.ACTIVE.name());
        productVariant.setProduct(requestDto.getProduct());
        productVariant.setApprovedBy(requestDto.getCompletedBy());
        productVariant.setApprovedDate(LocalDateTime.now());
        productVariant.setCreatedBy(requestDto.getCreatedBy());
        productVariant.setLastModifiedBy(requestDto.getCreatedBy());
        productVariant.setCreatedDate(LocalDateTime.now());
        productVariant.setLastModifiedDate(LocalDateTime.now());
        productVariant.setVersion(requestDto.getVersion().add(BigInteger.ONE));
        productVariant.setCountryId(requestDto.getCountryId());
        productVariant.setSku(requestDto.getSku());
        productVariant.setWeight(requestDto.getWeight());
        productVariant.setVariantDescription(requestDto.getVariantDescription());
        productVariant.setVariantType(requestDto.getVariantType());
        productVariant.setIsVated(requestDto.getIsVated());
        productVariant.setVatValue(requestDto.getVatValue());
        return productVariant;
    }

    public static VariantsAwaitingApprovalResponseDto buildVariantAwaitingApproval(
            VariantAwaitingApproval variantAwaitingApproval) {
        return VariantsAwaitingApprovalResponseDto.builder()
                .id(variantAwaitingApproval.getId())
                .publicID(variantAwaitingApproval.getPublicId())
                .variantTypeId(variantAwaitingApproval.getVariantType().getId())
                .variantTypeName(variantAwaitingApproval.getVariantType().getVariantTypeName())
                .variantDescription(variantAwaitingApproval.getVariantType().getDescription())
                .variantTypeStatus(variantAwaitingApproval.getVariantType().getStatus())
                .variantName(variantAwaitingApproval.getVariantName().trim())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .variantDescription(variantAwaitingApproval.getVariantDescription())
                .sku(variantAwaitingApproval.getSku())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .createdBy(variantAwaitingApproval.getCreatedBy())
                .completedBy(variantAwaitingApproval.getCompletedBy())
                .completedDate(variantAwaitingApproval.getCompletedDate())
                .status(variantAwaitingApproval.getStatus())
                .approvalStatus(variantAwaitingApproval.getApprovalStatus())
                .version(variantAwaitingApproval.getVersion())
                .createdDate(variantAwaitingApproval.getCreatedDate())
                .lastModifiedDate(variantAwaitingApproval.getLastModifiedDate())
                .lastModifiedBy(variantAwaitingApproval.getLastModifiedBy())
                .countryId(variantAwaitingApproval.getCountryId())
                .manufacturer(variantAwaitingApproval.getProduct().getManufacturer().getManufacturerName())
                .brandName(variantAwaitingApproval.getProduct().getBrand().getBrandName())
                .productName(variantAwaitingApproval.getProduct().getProductName())
                .weight(variantAwaitingApproval.getWeight())
                .build();
    }

    public static RejectedVariantsResponseDto buildRejectedVariantsResponseDto(
            VariantAwaitingApproval variantAwaitingApproval) {
        return RejectedVariantsResponseDto.builder()
                .id(variantAwaitingApproval.getId())
                .publicID(variantAwaitingApproval.getPublicId())
                .variantTypeId(variantAwaitingApproval.getVariantType().getId())
                .variantTypeName(variantAwaitingApproval.getVariantType().getVariantTypeName())
                .variantDescription(variantAwaitingApproval.getVariantType().getDescription())
                .variantTypeStatus(variantAwaitingApproval.getVariantType().getStatus())
                .variantName(variantAwaitingApproval.getVariantName().trim())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .variantDescription(variantAwaitingApproval.getVariantDescription())
                .sku(variantAwaitingApproval.getSku())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .createdBy(variantAwaitingApproval.getCreatedBy())
                .completedBy(variantAwaitingApproval.getCompletedBy())
                .completedDate(variantAwaitingApproval.getCompletedDate())
                .status(variantAwaitingApproval.getStatus())
                .approvalStatus(variantAwaitingApproval.getApprovalStatus())
                .version(variantAwaitingApproval.getVersion())
                .createdDate(variantAwaitingApproval.getCreatedDate())
                .lastModifiedDate(variantAwaitingApproval.getLastModifiedDate())
                .lastModifiedBy(variantAwaitingApproval.getLastModifiedBy())
                .countryId(variantAwaitingApproval.getCountryId())
                .rejectedReason(variantAwaitingApproval.getRejectedReason())
                .weight(variantAwaitingApproval.getWeight())
                .build();
    }

    public static VariantResponseDto buildVariant(VariantVersion variantVersion) {
        VariantResponseDto responseDto = new VariantResponseDto();
        responseDto.setCostPrice(variantVersion.getCostPrice());
        responseDto.setVariantTypeId(variantVersion.getVariantType().getPublicId());
        responseDto.setVariantName(variantVersion.getVariantName().trim());
        responseDto.setVariantDescription(variantVersion.getVariantDescription());
        responseDto.setCreatedBy(variantVersion.getProductVariant().getCreatedBy());
        responseDto.setSku(variantVersion.getSku());
        responseDto.setPublicId(variantVersion.getProductVariant().getPublicId());
        responseDto.setCreatedDate(variantVersion.getProductVariant().getCreatedDate());
        responseDto.setProductPublicId(variantVersion.getProduct().getPublicId());
        responseDto.setProductName(variantVersion.getProduct().getProductName());
        responseDto.setLastModifiedBy(variantVersion.getProductVariant().getLastModifiedBy());
        responseDto.setLastModifiedDate(variantVersion.getProductVariant().getLastModifiedDate());
        responseDto.setVersion(variantVersion.getProductVariant().getVersion());
        responseDto.setStatus(variantVersion.getProductVariant().getStatus());
        responseDto.setApprovalStatus(variantVersion.getApprovalStatus());
        responseDto.setCountryPublicId(variantVersion.getProductVariant().getCountryId());
        return responseDto;
    }

    public static ProductVariantDetailResponseDto buildVariantWithProductResponse(ProductVariant productVariant,
            List<String> imageCatalog,
            ProductCategoryWithSubcategoryResponse parentCategory) {
        ProductVariantDetailResponseDto responseDto = new ProductVariantDetailResponseDto();

        responseDto.setVariantTypeId(productVariant.getVariantType().getPublicId());
        responseDto.setVariantName(productVariant.getVariantName().trim());
        responseDto.setVariantDescription(productVariant.getVariantDescription());
        responseDto.setCreatedBy(productVariant.getCreatedBy());
        responseDto.setSku(productVariant.getSku());
        responseDto.setPublicId(productVariant.getPublicId());
        responseDto.setCreatedDate(productVariant.getCreatedDate());
        responseDto.setProduct(ProductHelper.buildProductResponse(productVariant.getProduct()));
        responseDto.setLastModifiedBy(productVariant.getLastModifiedBy());
        responseDto.setLastModifiedDate(productVariant.getLastModifiedDate());
        responseDto.setVersion(productVariant.getVersion());
        responseDto.setStatus(productVariant.getStatus());
        responseDto.setCountryPublicId(productVariant.getCountryId());
        responseDto.setImageUrls(imageCatalog);
        responseDto.setWeight(Objects.isNull(productVariant.getWeight()) ? 0.00 : productVariant.getWeight());
        responseDto.setProductPublicId(productVariant.getProduct().getPublicId());
        responseDto.setProductName(productVariant.getProduct().getProductName());
        responseDto.setVariantTypeName(productVariant.getVariantType().getVariantTypeName());
        responseDto.setParentProductCategory(parentCategory);
        responseDto.setVated(Objects.isNull(productVariant.getIsVated()) ? false : productVariant.getIsVated());
        responseDto.setVatValue(productVariant.getVatValue());
        return responseDto;
    }

    public static VariantProductResponseDto buildVariantWithProduct(VariantVersion variantVersion) {
        VariantProductResponseDto variantProductResponseDto = new VariantProductResponseDto();
        variantProductResponseDto.setCostPrice(variantVersion.getCostPrice());
        variantProductResponseDto.setVariantTypeId(variantVersion.getVariantType().getPublicId());
        variantProductResponseDto.setVariantName(variantVersion.getVariantName().trim());
        variantProductResponseDto.setVariantDescription(variantVersion.getVariantDescription());
        variantProductResponseDto.setCreatedBy(variantVersion.getProductVariant().getCreatedBy());
        variantProductResponseDto.setSku(variantVersion.getSku());
        variantProductResponseDto.setPublicId(variantVersion.getProductVariant().getPublicId());
        variantProductResponseDto.setCreatedDate(variantVersion.getProductVariant().getCreatedDate());
        variantProductResponseDto.setLastModifiedBy(variantVersion.getProductVariant().getLastModifiedBy());
        variantProductResponseDto.setLastModifiedDate(variantVersion.getProductVariant().getLastModifiedDate());
        variantProductResponseDto.setVersion(variantVersion.getProductVariant().getVersion());
        variantProductResponseDto.setStatus(variantVersion.getProductVariant().getStatus());
        variantProductResponseDto.setApprovalStatus(variantVersion.getApprovalStatus());
        variantProductResponseDto.setProductPublicId(variantVersion.getProduct().getPublicId());
        variantProductResponseDto.setProductName(variantVersion.getProduct().getProductName());
        variantProductResponseDto.setBrand(variantVersion.getProduct().getBrand());
        variantProductResponseDto.setManufacturer(
                ManufacturerHelper.buildManufacturerResponse(variantVersion.getProduct().getManufacturer()));
        variantProductResponseDto.setProductCategory(ProductCategoryHelper
                .buildProductCategoryResponseDto(variantVersion.getProduct().getProductCategory()));
        variantProductResponseDto.setProductListing(variantVersion.getProduct().getProductListing());
        variantProductResponseDto.setDefaultImageUrl(variantVersion.getDefaultImageUrl());
        variantProductResponseDto.setProductDescription(variantVersion.getProduct().getProductDescription());
        variantProductResponseDto.setProductHighlights(variantVersion.getProduct().getProductHighlights());
        variantProductResponseDto.setWarrantyDuration(variantVersion.getProduct().getWarrantyDuration());
        variantProductResponseDto.setWarrantyCover(variantVersion.getProduct().getWarrantyCover());
        variantProductResponseDto.setWarrantyType(variantVersion.getProduct().getWarrantyType() == null ? null
                : WarrantyTypeHelper.buildWarrantyTypeResponse(variantVersion.getProduct().getWarrantyType()));
        variantProductResponseDto.setWarrantyAddress(variantVersion.getProduct().getWarrantyAddress());
        variantProductResponseDto.setMeasurementUnit(
                MeasuringUnitHelper.buildMeasuringUnitResponse(variantVersion.getProduct().getMeasurementUnit()));
        variantProductResponseDto.setProductStatus(variantVersion.getProduct().getStatus());
        variantProductResponseDto.setNote(variantVersion.getProduct().getProductNotes());
        variantProductResponseDto.setCountryPublicId(variantVersion.getProductVariant().getCountryId());
        variantProductResponseDto.setWeight(variantVersion.getProductVariant().getWeight());
        return variantProductResponseDto;
    }

    public static VariantCompleteResponseDto buildCompleteVariantResponse(VariantVersion variantVersion) {
        return VariantCompleteResponseDto.builder()
                .costPrice(variantVersion.getCostPrice())
                .variantTypeId(variantVersion.getVariantType().getPublicId())
                .variantName(variantVersion.getVariantName().trim())
                .variantDescription(variantVersion.getVariantDescription())
                .createdBy(variantVersion.getProductVariant().getCreatedBy())
                .sku(variantVersion.getSku())
                .publicId(variantVersion.getProductVariant().getPublicId())
                .createdDate(variantVersion.getProductVariant().getCreatedDate())
                .productId(variantVersion.getProductVariant().getPublicId())
                .lastModifiedBy(variantVersion.getProductVariant().getProduct().getLastModifiedBy())
                .lastModifiedDate(variantVersion.getProductVariant().getProduct().getLastModifiedDate())
                .leadTime(variantVersion.getLeadTime())
                .threshold(variantVersion.getThreshold())
                .status(variantVersion.getProductVariant().getVariantName())
                .version(variantVersion.getProductVariant().getVersion())
                .createdBy(variantVersion.getProductVariant().getCreatedBy())
                .productName(variantVersion.getProduct().getProductName())
                .brandName(variantVersion.getProductVariant().getProduct().getBrand().getBrandName())
                .measuringUnitName(variantVersion.getProductVariant().getProduct().getMeasurementUnit().getName())
                .measuringUnitAbbreviation(
                        variantVersion.getProductVariant().getProduct().getMeasurementUnit().getAbbreviation())
                .productCategoryName(variantVersion.getProduct().getProductCategory().getProductCategoryName())
                .productCategoryPublicId(variantVersion.getProduct().getProductCategory().getPublicId())
                .variantTypeName(variantVersion.getVariantType().getVariantTypeName())
                .countryPublicId(variantVersion.getProductVariant().getCountryId())
                .build();

    }

    public static VariantWithMarkupResponseDto buildVariantList(VariantVersion variantVersion) {
        VariantWithMarkupResponseDto responseDto = new VariantWithMarkupResponseDto();
        responseDto.setCostPrice(variantVersion.getCostPrice());
        responseDto.setVariantTypeId(variantVersion.getVariantType().getPublicId());
        responseDto.setVariantName(variantVersion.getVariantName().trim());
        responseDto.setVariantDescription(variantVersion.getVariantDescription());
        responseDto.setCreatedBy(variantVersion.getProductVariant().getCreatedBy());
        responseDto.setSku(variantVersion.getSku());
        responseDto.setPublicId(variantVersion.getProductVariant().getPublicId());
        responseDto.setCreatedDate(variantVersion.getProductVariant().getCreatedDate());
        responseDto.setProductPublicId(variantVersion.getProduct().getPublicId());
        responseDto.setProductName(variantVersion.getProduct().getProductName());
        responseDto.setLastModifiedBy(variantVersion.getProductVariant().getLastModifiedBy());
        responseDto.setLastModifiedDate(variantVersion.getProductVariant().getLastModifiedDate());
        responseDto.setVersion(variantVersion.getProductVariant().getVersion());
        responseDto.setStatus(variantVersion.getProductVariant().getStatus());
        responseDto.setApprovalStatus(variantVersion.getApprovalStatus());
        return responseDto;
    }

    public static VariantMarkupCompleteResponseDto buildCompleteVariantMarkupResponse(VariantVersion variantVersion,
            Map<UUID, BigDecimal> markUpMap) {
        return VariantMarkupCompleteResponseDto.builder()
                .costPrice(variantVersion.getCostPrice())
                .variantTypeId(variantVersion.getVariantType().getPublicId())
                .variantName(variantVersion.getVariantName().trim())
                .variantDescription(variantVersion.getVariantDescription())
                .createdBy(variantVersion.getProductVariant().getCreatedBy())
                .sku(variantVersion.getSku())
                .markup(markUpMap.get(variantVersion.getProduct().getProductCategory().getPublicId()))
                .categoryPublicId(variantVersion.getProduct().getProductCategory().getPublicId())
                .publicId(variantVersion.getProductVariant().getPublicId())
                .createdDate(variantVersion.getProductVariant().getCreatedDate())
                .productId(variantVersion.getProductVariant().getPublicId())
                .lastModifiedBy(variantVersion.getProductVariant().getProduct().getLastModifiedBy())
                .lastModifiedDate(variantVersion.getProductVariant().getProduct().getLastModifiedDate())
                .status(variantVersion.getProductVariant().getStatus())
                .version(variantVersion.getProductVariant().getVersion())
                .createdBy(variantVersion.getProductVariant().getCreatedBy())
                .productName(variantVersion.getProduct().getProductName())
                .brandName(variantVersion.getProductVariant().getProduct().getBrand().getBrandName())
                .productCategoryName(variantVersion.getProduct().getProductCategory().getProductCategoryName())
                .productCategoryPublicId(variantVersion.getProduct().getProductCategory().getPublicId())
                .variantTypeName(variantVersion.getVariantType().getVariantTypeName())
                .build();
    }

    public static VariantProductResponseDto buildVariantWithProduct(VariantAwaitingApproval variant) {
        VariantProductResponseDto variantProductResponseDto = new VariantProductResponseDto();
        variantProductResponseDto.setCostPrice(variant.getCostPrice());
        variantProductResponseDto.setVariantTypeId(variant.getVariantType().getPublicId());
        variantProductResponseDto.setVariantName(variant.getVariantName().trim());
        variantProductResponseDto.setVariantDescription(variant.getVariantDescription());
        variantProductResponseDto.setCreatedBy(variant.getCreatedBy());
        variantProductResponseDto.setSku(variant.getSku());
        variantProductResponseDto.setPublicId(variant.getPublicId());
        variantProductResponseDto.setCreatedDate(variant.getCreatedDate());
        variantProductResponseDto.setLastModifiedBy(variant.getLastModifiedBy());
        variantProductResponseDto.setLastModifiedDate(variant.getLastModifiedDate());
        variantProductResponseDto.setVersion(variant.getVersion());
        variantProductResponseDto.setStatus(variant.getStatus());
        variantProductResponseDto.setApprovalStatus(variant.getApprovalStatus());

        variantProductResponseDto.setProductPublicId(variant.getProduct().getPublicId());
        variantProductResponseDto.setProductName(variant.getProduct().getProductName());
        variantProductResponseDto.setBrand(variant.getProduct().getBrand());
        variantProductResponseDto
                .setManufacturer(ManufacturerHelper.buildManufacturerResponse(variant.getProduct().getManufacturer()));
        variantProductResponseDto.setProductCategory(
                ProductCategoryHelper.buildProductCategoryResponseDto(variant.getProduct().getProductCategory()));
        variantProductResponseDto.setProductListing(variant.getProduct().getProductListing());
        variantProductResponseDto.setDefaultImageUrl(variant.getDefaultImageUrl());
        variantProductResponseDto.setProductDescription(variant.getProduct().getProductDescription());
        variantProductResponseDto.setProductHighlights(variant.getProduct().getProductHighlights());
        variantProductResponseDto.setWarrantyDuration(variant.getProduct().getWarrantyDuration());
        variantProductResponseDto.setWarrantyCover(variant.getProduct().getWarrantyCover());
        variantProductResponseDto.setWarrantyType(variant.getProduct().getWarrantyType() == null ? null
                : WarrantyTypeHelper.buildWarrantyTypeResponse(variant.getProduct().getWarrantyType()));
        variantProductResponseDto.setWarrantyAddress(variant.getProduct().getWarrantyAddress());
        variantProductResponseDto.setMeasurementUnit(
                MeasuringUnitHelper.buildMeasuringUnitResponse(variant.getProduct().getMeasurementUnit()));
        variantProductResponseDto.setProductStatus(variant.getProduct().getStatus());
        variantProductResponseDto.setNote(variant.getProduct().getProductNotes());
        variantProductResponseDto.setCountryPublicId(variant.getCountryId());
        variantProductResponseDto.setWeight(variant.getWeight());
        return variantProductResponseDto;
    }

    public static VariantAwaitingApproval buildVariantAwaitingApproval(VariantVersion variantVersion,
            EditLiveInventoryRequestDto requestDto) {
        VariantAwaitingApproval variant = new VariantAwaitingApproval();
        variant.setPublicId(UUID.randomUUID());
        variant.setSku(variantVersion.getSku());
        variant.setVariantType(variantVersion.getVariantType());
        variant.setProduct(variantVersion.getProduct());
        variant.setVariantName(variantVersion.getVariantName().trim());
        variant.setVariantDescription(variantVersion.getVariantDescription());
        variant.setCostPrice(variantVersion.getCostPrice());
        variant.setStatus(Status.ACTIVE.name());
        variant.setCreatedBy(requestDto.getModifiedBy());
        variant.setLastModifiedBy(requestDto.getModifiedBy());
        variant.setCreatedDate(LocalDateTime.now());
        variant.setLastModifiedDate(LocalDateTime.now());
        variant.setApprovalStatus(ApprovalStatus.PENDING.name());
        variant.setVersion(BigInteger.ZERO);
        variant.setCountryId(variantVersion.getProductVariant().getCountryId());
        return variant;
    }

    public static List<ProductVariantVatResponseDto> buildProductVariantVatResponseDto(
            List<ProductVariant> productVariantVatValueRatioList) {
        var listVariantsVated = new ArrayList<ProductVariantVatResponseDto>();
        productVariantVatValueRatioList.stream().forEach(
                pv -> {
                    listVariantsVated.add(
                            ProductVariantVatResponseDto
                                    .builder()
                                    .getIsVated(Objects.isNull(pv.getIsVated()) ? false : pv.getIsVated())
                                    .getSku(pv.getSku())
                                    .getVatValue(pv.getVatValue())
                                    .getPublicId(pv.getPublicId())
                                    .build());
                });
        return listVariantsVated;
    }

    public static Map<String, ProductVariantVatResponseDto> convertToMap(List<ProductVariant> list) {
        var listDto = VariantHelper.buildProductVariantVatResponseDto(list);

        Map<String, ProductVariantVatResponseDto> mapProductVariants = listDto.stream().collect(TreeMap::new,
                (map, pv) -> {
                    map.put(pv.getGetSku(), pv);
                    map.put(pv.getGetPublicId().toString(), pv);
                },
                TreeMap::putAll);

        return mapProductVariants;
    }
}
