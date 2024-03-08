package com.mctoluene.productinformationmanagement.helper;

import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.ProductInventoryType;
import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.*;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProductHelper {

    private ProductHelper() {

    }

    public static Product buildProduct(CreateProductRequestDto requestDto, Brand brand, Manufacturer manufacturer,
            ProductCategory productCategory, MeasuringUnit measuringUnit, WarrantyType warrantyType) {
        Product product = new Product();
        product.setPublicId(UUID.randomUUID());
        product.setProductName(
                WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(requestDto.getProductName().trim())));
        product.setProductDescription(requestDto.getProductDescription().isEmpty() ? ""
                : StringSanitizerUtils.sanitizeInput(requestDto.getProductDescription()));
        product.setBrand(brand);
        product.setManufacturer(manufacturer);
        product.setProductCategory(productCategory);
        product.setMeasurementUnit(measuringUnit);
        product.setProductListing(getProductListingAsString(
                Objects.isNull(requestDto.getProductListings()) ? Set.of(ProductListing.MERCHBUY)
                        : requestDto.getProductListings()));
        product.setProductHighlights(requestDto.getProductHighlights());
        product.setWarrantyAddress(requestDto.getWarrantyAddress());
        product.setWarrantyCover(requestDto.getWarrantyCover());
        product.setWarrantyDuration(requestDto.getWarrantyDuration());
        product.setWarrantyType(warrantyType);
        Optional<Status> optionalStatus = Status
                .getStatus(requestDto.getStatus() != null ? requestDto.getStatus().name() : "");
        if (optionalStatus.isPresent())
            product.setStatus(optionalStatus.get().name());
        else
            product.setStatus(Status.INACTIVE.name());
        product.setCreatedBy(requestDto.getCreatedBy());
        product.setLastModifiedBy(requestDto.getCreatedBy());
        product.setCreatedDate(LocalDateTime.now());
        product.setLastModifiedDate(LocalDateTime.now());
        product.setVersion(BigInteger.ZERO);
        product.setProductNotes(requestDto.getNote());
        product.setVated(Objects.isNull(requestDto.getVated()) ? false : requestDto.getVated());
        product.setMinVat(Objects.isNull(requestDto.getMinVat()) ? BigDecimal.ZERO : requestDto.getMinVat());
        product.setMaxVat(Objects.isNull(requestDto.getMaxVat()) ? BigDecimal.ZERO : requestDto.getMaxVat());
        return product;
    }

    public static CreateProductResponseDto buildProductResponseDto(Product response) {
        return CreateProductResponseDto.builder()
                .publicId(response.getPublicId())
                .productName(response.getProductName().trim())
                .brandPublicId(response.getBrand().getPublicId())
                .brandName(response.getBrand().getBrandName())
                .manufacturerPublicId(response.getManufacturer().getPublicId())

                .manufacturerName(response.getManufacturer().getManufacturerName())
                .productCategoryPublicId(response.getProductCategory().getPublicId())
                .productCategoryName(response.getProductCategory().getProductCategoryName())
                .productDescription(response.getProductDescription())

                .measurementUnit(response.getMeasurementUnit() == null
                        ? null
                        : response.getMeasurementUnit().getName())

                .productListings(getProductListingAsList(response.getProductListing()))
                .productHighlights(response.getProductHighlights())

                .warrantyAddress(response.getWarrantyAddress())
                .warrantyCover(response.getWarrantyCover())
                .warrantyDuration(response.getWarrantyDuration())

                .warrantyTypeName(
                        response.getWarrantyType() == null ? null : response.getWarrantyType().getWarrantyTypeName())

                .createdDate(response.getCreatedDate())
                .createdBy(response.getCreatedBy())

                .lastModifiedBy(response.getLastModifiedBy())
                .lastModifiedDate(response.getLastModifiedDate())
                .status(response.getStatus())
                .version(response.getVersion())
                .note(response.getProductNotes())
                .minVat(response.getMinVat())
                .maxVat(response.getMaxVat())
                .vated(response.getVated())
                .build();
    }

    public static ProductResponseDto buildProductResponse(Product product) {

        BrandResponseDto brandResponseDto = new BrandResponseDto(
                product.getBrand().getPublicId(),
                product.getBrand().getBrandName(),
                product.getBrand().getDescription(),
                product.getBrand().getCreatedDate(),
                product.getBrand().getLastModifiedDate(),
                product.getBrand().getCreatedBy(),
                product.getBrand().getLastModifiedBy(),
                product.getBrand().getStatus().name(),
                product.getBrand().getVersion(),
                product.getBrand().getManufacturer() == null ? null : product.getBrand().getManufacturer().getPublicId()

        );

        ManufacturerResponseDto manufacturerResponseDto = new ManufacturerResponseDto(
                product.getManufacturer().getPublicId(),
                product.getManufacturer().getDescription(), product.getManufacturer().getManufacturerName(),
                product.getManufacturer().getCreatedDate(),
                product.getManufacturer().getCreatedBy(), product.getManufacturer().getLastModifiedBy(),
                product.getManufacturer().getLastModifiedDate(),
                product.getManufacturer().getStatus().name(), product.getManufacturer().getVersion());

        ProductCategoryResponseDto productCategoryResponseDto = new ProductCategoryResponseDto(
                product.getProductCategory().getPublicId(),
                product.getProductCategory().getProductCategoryName(),
                product.getProductCategory().getImageUrl(),
                product.getProductCategory().getDescription(),
                product.getProductCategory().getStatus().name(),
                product.getProductCategory().getVersion(),
                product.getProductCategory().getCreatedDate(),
                product.getProductCategory().getLastModifiedDate(),
                product.getProductCategory().getCreatedBy(),
                product.getProductCategory().getLastModifiedBy()

        );

        WarrantyTypeResponseDto warrantyTypeResponseDto = product.getWarrantyType() == null
                ? null
                : new WarrantyTypeResponseDto(product.getWarrantyType().getPublicId(),
                        product.getWarrantyType().getWarrantyTypeName(), product.getWarrantyType().getDescription(),
                        product.getWarrantyType().getCreatedDate(), product.getWarrantyType().getLastModifiedDate(),
                        product.getWarrantyType().getCreatedBy(), product.getWarrantyType().getLastModifiedBy(),
                        product.getWarrantyType().getStatus().name(), product.getWarrantyType().getVersion());

        return ProductResponseDto.builder()
                .publicId(product.getPublicId())
                .productName(product.getProductName().trim())
                .productDescription(product.getProductDescription())
                .brand(brandResponseDto)
                .manufacturer(manufacturerResponseDto)
                .productCategory(productCategoryResponseDto)

                .measurementUnit(product.getMeasurementUnit() == null
                        ? null
                        : product.getMeasurementUnit().getName())

                .productListing(product.getProductListing())
                .productHighlights(product.getProductHighlights())
                .warrantyAddress(product.getWarrantyAddress())
                .warrantyCover(product.getWarrantyCover())
                .warrantyDuration(product.getWarrantyDuration())

                .warrantyType(warrantyTypeResponseDto)

                .status(product.getStatus())
                .createdBy(product.getCreatedBy())
                .lastModifiedBy(product.getLastModifiedBy())
                .createdDate(product.getCreatedDate())
                .lastModifiedDate(product.getLastModifiedDate())
                .version(product.getVersion())
                .note(product.getProductNotes())
                .minVat(product.getMinVat())
                .maxVat(product.getMaxVat())
                .isVated(Objects.isNull(product.getVated()) ? false : product.getVated())
                .build();
    }

    public static ArchivedProductResponseDto buildArchivedProductResponse(Product product) {
        return ArchivedProductResponseDto.builder()
                .publicId(product.getPublicId())
                .status(product.getStatus())
                .version(product.getVersion())
                .createdBy(product.getCreatedBy())
                .createdDate(product.getCreatedDate())
                .lastModifiedBy(product.getLastModifiedBy())
                .lastModifiedDate(product.getLastModifiedDate())
                .build();
    }

    private static String getProductListingAsString(Set<ProductListing> listings) {
        return listings.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static List<ProductListing> getProductListingAsList(String listingString) {
        return Optional.ofNullable(listingString).map(listing -> Arrays.stream(listing.split(", "))
                .map(ProductListing::valueOf)
                .collect(Collectors.toList()))
                .orElse(Collections.singletonList(ProductListing.MERCHBUY));
    }

    public static FailedProductsResponseDto buildFailedProductResponseDto(FailedProducts response) {
        return FailedProductsResponseDto.builder()
                .publicId(response.getPublicId())
                .brandName(response.getBrandName())
                .manufacturerName(response.getManufacturerName())
                .productCategoryName(response.getProductCategoryName())
                .productCategoryName(response.getProductName().trim())
                .productDescription(response.getProductDescription())
                .measurementUnit(response.getMeasurementUnit())
                .productListing(response.getProductListing())
                .defaultImageUrl(response.getDefaultImageUrl())
                .productHighlights(response.getProductHighlights())
                .warrantyAddress(response.getWarrantyAddress())
                .warrantyCover(response.getWarrantyCover())
                .warrantyDuration(response.getWarrantyDuration())
                .warrantyType(response.getWarrantyType())
                .productCountry(response.getProductCountry())
                .createdDate(response.getCreatedDate())
                .createdBy(response.getCreatedBy())
                .lastModifiedBy(response.getLastModifiedBy())
                .lastModifiedDate(response.getLastModifiedDate())
                .status(response.getStatus())
                .version(response.getVersion())
                .build();
    }

    public static Product buildProduct(String productName, Manufacturer manufacturer, Brand brand,
            ProductCategory productCategory, String createdBy, MeasuringUnit measuringUnit) {
        Product product = new Product();
        product.setPublicId(UUID.randomUUID());
        product.setProductName(WordUtils.capitalizeFully(StringSanitizerUtils.sanitizeInput(productName)));
        product.setManufacturer(manufacturer);
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        product.setStatus(Status.ACTIVE.name());
        product.setCreatedBy(createdBy);
        product.setMeasurementUnit(measuringUnit);
        product.setCreatedDate(LocalDateTime.now());
        product.setLastModifiedDate(LocalDateTime.now());
        product.setVersion(BigInteger.ZERO);

        return product;
    }

    public static ProductCatalogueResponseDto buildProductCatalogueResponse(LiveInventoryResponse liveInventoryResponse,
            VariantVersion variantVersion, InventoryData inventoryData, PriceModelResponseDto priceModelResponse) {
        return ProductCatalogueResponseDto.builder()
                .variantName(variantVersion.getVariantName().trim())
                .variantDescription(variantVersion.getVariantDescription())
                .productPublicId(variantVersion.getProduct().getPublicId())
                .variantPublicId(variantVersion.getProductVariant().getPublicId())
                .sku(inventoryData.sku_code())
                .availableQuantity(inventoryData.available_quantity().intValue())
                .warehouseId(liveInventoryResponse.getWarehouseId())
                .warehouseName(liveInventoryResponse.getWarehouseName())
                .minimumOrderQuantity(priceModelResponse.getMinimumQuantity())
                .maximumOrderQuantity(priceModelResponse.getMaximumQuantity())
                .individualPricing(Objects.nonNull(priceModelResponse.getManualSellingPrice())
                        ? priceModelResponse.getManualSellingPrice().doubleValue()
                        : priceModelResponse.getFinalSellingPrice().doubleValue())
                .volumePricing(priceModelResponse.getVolumePricing())
                .productInventoryType(ProductInventoryType.LIVE_INVENTORY)
                .build();
    }

    public static ProductCatalogueResponseDto buildProductCatalogueResponse(
            SupplierProductDetailsResponseDto supplierProductDetailsResponseDto,
            PriceModelResponseDto priceModelResponse) {
        return ProductCatalogueResponseDto.builder()
                .variantName(supplierProductDetailsResponseDto.getVariantName().trim())
                .productPublicId(supplierProductDetailsResponseDto.getProductId())
                .variantPublicId(supplierProductDetailsResponseDto.getVariantPublicId())
                .sku(supplierProductDetailsResponseDto.getSku())
                .availableQuantity(supplierProductDetailsResponseDto.getQuantity().intValue())
                .minimumOrderQuantity(priceModelResponse.getMinimumQuantity())
                .maximumOrderQuantity(priceModelResponse.getMaximumQuantity())
                .individualPricing(Objects.nonNull(priceModelResponse.getManualSellingPrice())
                        ? priceModelResponse.getManualSellingPrice().doubleValue()
                        : priceModelResponse.getFinalSellingPrice().doubleValue())
                .volumePricing(priceModelResponse.getVolumePricing())
                .lgaName(supplierProductDetailsResponseDto.getLgaName())
                .stateName(supplierProductDetailsResponseDto.getStateName())
                .fulfillmentType(supplierProductDetailsResponseDto.getFulFilmentTypes())
                .supplierId(supplierProductDetailsResponseDto.getSupplierId())
                .supplierName(supplierProductDetailsResponseDto.getSupplierName())
                .productInventoryType(ProductInventoryType.VIRTUAL_INVENTORY)
                .warehouseName(supplierProductDetailsResponseDto.getWarehouseName())
                .warehouseId(supplierProductDetailsResponseDto.getWarehouseId())
                .build();
    }

}