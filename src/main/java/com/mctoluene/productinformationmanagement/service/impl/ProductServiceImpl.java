package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.configuration.CountryProperties;
import com.mctoluene.productinformationmanagement.configuration.PimVatProperties;
import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.*;
import com.mctoluene.productinformationmanagement.domain.request.location.IdListDto;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.UpdateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.CreateVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.*;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponse;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.PageableException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.search.ProductFilter;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.helper.*;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductService;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.productinformationmanagement.util.PoiExcelParser;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;
import com.mctoluene.commons.exceptions.NotFoundException;
import com.mctoluene.commons.response.AppResponse;

import liquibase.repackaged.com.opencsv.CSVReader;
import liquibase.repackaged.com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductInternalService productInternalService;
    private final VariantInternalService variantInternalService;

    private final ImageCatalogInternalService imageCatalogInternalService;

    private final MessageSourceService messageSourceService;

    private final BrandInternalService brandInternalService;

    private final MeasuringUnitInternalService measuringUnitInternalService;

    private final ManufacturerInternalService manufacturerInternalService;

    private final ProductCategoryInternalService productCategoryInternalService;

    private final VariantTypeInternalService variantTypeInternalService;

    private final VariantVersionInternalService variantVersionInternalService;

    private final LocationClientInternalService locationClientInternalService;

    private final WarrantyTypeInternalService warrantyTypeInternalService;

    private final FailedProductsInternalService failedProductsInternalService;

    private final VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    private final AzureBusMessageQueueService azureBusMessageQueueService;

    private final ObjectMapper mapper;

    private final InventoryClientInternalService inventoryClientInternalService;

    private final ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    private final ProductVariantInternalService productVariantInternalService;

    private final MessageSource messageSource;

    private final PimVatProperties pimVatProperties;

    private static final String DEFAULT_VAT_VALUE = "default.vat.value";
    private static final String DEFAULT_MAX_VAT = "default.max.vat";
    private static final String DEFAULT_MIN_VAT = "default.min.vat";

    @Override
    public AppResponse createNewProduct(CreateProductRequestDto requestDto, Boolean createWithoutVariants,
            String countryCode) {

        Brand brand = brandInternalService.findByPublicId(requestDto.getBrandPublicId());

        if (!Objects.equals(brand.getManufacturer().getPublicId(), requestDto.getManufacturerPublicId())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("product.manufacturer.brand.mismatch"));
        }

        Manufacturer manufacturer = brand.getManufacturer();

        if (productInternalService.productNameIsNotUniqueToBrandAndManufacturer(
                requestDto.getProductName().toUpperCase().trim(),
                brand, manufacturer)) {
            throw new ValidatorException(messageSourceService.getMessageByKey("product.name.is.not.unique"));
        }

        WarrantyType warrantyType = null;
        if (requestDto.getWarrantyTypePublicId() != null && !requestDto.getWarrantyTypePublicId().isEmpty()) {
            warrantyType = warrantyTypeInternalService
                    .findByPublicId(UUID.fromString(requestDto.getWarrantyTypePublicId()));
        }

        ProductCategory productCategory = productCategoryInternalService
                .findProductCategoryByPublicId(requestDto.getCategoryPublicId());
        MeasuringUnit measuringUnit = measuringUnitInternalService
                .findByPublicId(requestDto.getMeasurementUnitPublicId());

        // set min vat and max vat
        Map<String, Double> configData = getConfigData(countryCode);
        validateVatRequest(requestDto, configData);

        Product product = ProductHelper.buildProduct(requestDto, brand, manufacturer, productCategory, measuringUnit,
                warrantyType);

        if (requestDto.getVariants() != null) {
            List<UUID> countryIds = requestDto.getVariants().stream()
                    .map(CreateVariantAwaitingApprovalRequestDto::getCountryPublicId).distinct().toList();
            List<CountryDto> countries = fetchVariantCountries(countryIds);

            List<VariantAwaitingApproval> variants = buildVariantAwaitingApproval(requestDto.getVariants(), countries,
                    product);

            productInternalService.saveProductToDb(product);
            variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(variants);
        }
        // allow product creation from queue for TypeMessage.PRODUCTS_ONLY
        if (createWithoutVariants)
            productInternalService.saveProductToDb(product);

        CreateProductResponseDto responseDto = ProductHelper.buildProductResponseDto(product);
        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.created.successfully"),
                messageSourceService.getMessageByKey("product.created.successfully"),
                responseDto, null);
    }

    private static void validateVatRequest(CreateProductRequestDto requestDto, Map<String, Double> configData) {

        if (Objects.nonNull(requestDto.getMinVat()) && Objects.nonNull(requestDto.getMaxVat()) &&
                requestDto.getMinVat().compareTo(requestDto.getMaxVat()) > 0) {
            throw new ValidatorException("Min VAT cannot be greater than Max VAT");
        }

        // if minVat is less than zero, set to default min vat for country
        boolean minVatIsLessThanZero = requestDto.getMinVat().compareTo(BigDecimal.ZERO) < 0;
        requestDto.setMinVat(minVatIsLessThanZero
                ? BigDecimal.valueOf(configData.get(DEFAULT_MIN_VAT))
                : requestDto.getMinVat());

        // if maxVat is less than or equal to zero, use default min vat for country
        boolean maxVatIsLessThanZero = requestDto.getMaxVat().compareTo(BigDecimal.ZERO) <= 0;
        requestDto.setMaxVat(maxVatIsLessThanZero
                ? BigDecimal.valueOf(configData.get(DEFAULT_MAX_VAT))
                : requestDto.getMaxVat());

    }

    private Map<String, Double> getConfigData(String countryCode) {
        CountryProperties countryProperties = pimVatProperties.getCountries().get(countryCode);
        log.info("countries properties {} ", countryProperties);
        return Map.of(DEFAULT_MAX_VAT, Double.parseDouble(countryProperties.getDefaultVatMaxValue()),
                DEFAULT_MIN_VAT, Double.parseDouble(countryProperties.getDefaultVatMinValue()),
                DEFAULT_VAT_VALUE, Double.parseDouble(countryProperties.getDefaultVatValue()));
    }

    private List<CountryDto> fetchVariantCountries(List<UUID> countryIds) {
        IdListDto ids = new IdListDto();
        ids.setIds(countryIds);
        var responseEntity = locationClientInternalService.findCountryByPublicIds(ids);
        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));
        return responseEntity.getBody().getData();
    }

    @Override
    public AppResponse getProductsByProductCategoryId(UUID productCategoryId, Integer page, Integer size) {

        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResponseDto> productResponseDtos = productInternalService
                .findByCategory(pageable, productCategoryId).map(ProductHelper::buildProductResponse);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                productResponseDtos, null);
    }

    @Override
    public AppResponse getProductsByProductCategoryIds(List<UUID> productCategoryPublicIds, Integer page,
            Integer size) {
        final var productCategoryIds = productCategoryInternalService
                .findProductCategoryByPublicIds(productCategoryPublicIds)
                .stream().map(BaseEntity::getId).collect(Collectors.toList());

        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;

        Pageable pageable = PageRequest.of(page, size);

        log.info("Product categroies {}", productCategoryIds);

        Page<Product> productsByCategory = productInternalService.getProductsByCategoryIds(productCategoryIds,
                pageable);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                productsByCategory, null);
    }

    @Override
    public AppResponse getAllProducts(Integer page, Integer size, String searchParam, String fromDate, String toDate,
            List<UUID> categoryPublicIds, List<UUID> brandPublicIds,
            List<UUID> manufacturerPublicIds, List<UUID> warrantyTypePublicIds, List<UUID> measuringUnitPublicIds) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;
        if (categoryPublicIds == null)
            categoryPublicIds = List.of();
        if (brandPublicIds == null)
            brandPublicIds = List.of();
        if (manufacturerPublicIds == null)
            manufacturerPublicIds = List.of();
        if (warrantyTypePublicIds == null)
            warrantyTypePublicIds = List.of();
        if (measuringUnitPublicIds == null)
            measuringUnitPublicIds = List.of();

        LocalDateTime startDate = fromDate == null || fromDate.isEmpty() ? LocalDateTime.now().minusYears(100)
                : LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime endDate = toDate == null || toDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(toDate).atTime(LocalTime.MAX);

        final var productIdVariantCount = variantInternalService.getAllProductVariants().stream()
                .filter(p -> p.getProduct() != null)
                .collect(Collectors.groupingBy(p -> p.getProduct().getId(), Collectors.counting()));

        Page<Product> listOfProducts = productInternalService.searchProducts(searchParam, startDate, endDate,
                PageRequest.of(page, size),
                categoryPublicIds, brandPublicIds, manufacturerPublicIds, warrantyTypePublicIds,
                measuringUnitPublicIds);
        final Page<ProductResponseDto> productResponseData = listOfProducts.map(p -> {
            var productResponse = ProductHelper.buildProductResponse(p);
            productResponse.setVariantCount(productIdVariantCount.getOrDefault(p.getId(), 0L));

            ProductCategoryWithSubcategoryResponse parentCategory = productCategoryInternalService
                    .getParentCategoryAndSubCategories(p.getProductCategory());

            productResponse.setParentCategory(parentCategory);

            return productResponse;
        });
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"),
                productResponseData, null);
    }

    @Override
    public AppResponse updateProductByPublicId(UUID publicId, UpdateProductRequestDto updateProductRequestDto) {
        Product product = productInternalService.findByPublicId(publicId);
        product = updateProduct(product, updateProductRequestDto);
        CreateProductResponseDto responseDto = ProductHelper.buildProductResponseDto(product);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.updated.successfully"),
                messageSourceService.getMessageByKey("product.updated.successfully"),
                responseDto, null);
    }

    @Override
    @Transactional
    public List<ImageUploadTemplateRequest> saveUploadProductVariants(
            List<ImageUploadTemplateRequest> imageTemplateRequests, String createdBy, UUID countryId) {

        log.info("about to save image product upload template request {} ", imageTemplateRequests);

        List<ImageCatalog> imageCatalogs = new ArrayList<>();
        List<FailedProducts> failedProducts = new ArrayList<>();
        List<VariantAwaitingApproval> variantAwaitingApprovalList = new ArrayList<>();
        // List<ProductVariant> productVariantList = new ArrayList<>();
        List<ImageUploadTemplateRequest> missingImagesList = new ArrayList<>();
        ProductVariantDto productVariantDto;
        log.info("image upload template request size {}", imageTemplateRequests.size());

        for (ImageUploadTemplateRequest imageTemplateRequest : imageTemplateRequests) {

            if (checkIsBlank(imageTemplateRequest.getImageUrl1())
                    && checkIsBlank(imageTemplateRequest.getImageUrl2())) {
                log.info("image urls are empty for product {}", imageTemplateRequests);
                missingImagesList.add(imageTemplateRequest);
                continue;
            }

            productVariantDto = TemplateHelper.buildProductVariantDtoUsingImageTemplateRequest(imageTemplateRequest,
                    createdBy, mapper);

            productVariantDto.setCountryId(countryId);
            productVariantDto.setMeasurementUnit(imageTemplateRequest.getMeasurementUnit());
            productVariantDto.setVated(imageTemplateRequest.isVated());

            var responseEntity = locationClientInternalService.getCountryByPublicId(countryId);

            if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                    || responseEntity.getStatusCodeValue() != 200)
                throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

            String countryCode = responseEntity.getBody().getData().threeLetterCode();
            Map<String, Double> configData = getConfigData(countryCode);

            productVariantDto.setVatValue(Objects.nonNull(imageTemplateRequest.getVatValue())
                    ? imageTemplateRequest.getVatValue()
                    : BigDecimal.valueOf(configData.get(DEFAULT_VAT_VALUE)));

            VariantAwaitingApproval variantAwaitingApproval = validateTemplate(productVariantDto, failedProducts,
                    variantAwaitingApprovalList, imageTemplateRequest, configData);

            if (Objects.isNull(variantAwaitingApproval)) {
                log.info("variantAwaitingApproval is null - getting next imageUploadRequest object ..");
                continue;
            }

            validateImages(createdBy, imageCatalogs, imageTemplateRequest, variantAwaitingApproval);

        }

        if (!failedProducts.isEmpty())
            failedProductsInternalService.saveAllFailedProducts(failedProducts);

        variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(variantAwaitingApprovalList);
        imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogs);

        return missingImagesList;
    }

    @Override
    public List<ImageUploadTemplateRequest> saveUploadProductVariants(
            List<ImageUploadTemplateRequest> imageTemplateRequests,
            String createdBy, UUID countryId, Product product) {
        List<ImageCatalog> imageCatalogs = new ArrayList<>();
        List<FailedProducts> failedProducts = new ArrayList<>();
        List<VariantAwaitingApproval> variantAwaitingApprovalList = new ArrayList<>();
        List<ImageUploadTemplateRequest> missingImagesList = new ArrayList<>();
        ProductVariantDto productVariantDto;
        log.info("image upload template request size {}", imageTemplateRequests.size());

        for (ImageUploadTemplateRequest imageTemplateRequest : imageTemplateRequests) {
            if (checkIsBlank(imageTemplateRequest.getImageUrl1())
                    && checkIsBlank(imageTemplateRequest.getImageUrl2())) {
                log.info("image urls are empty for product {}", imageTemplateRequests);
                missingImagesList.add(imageTemplateRequest);
                continue;
            }

            productVariantDto = TemplateHelper.buildProductVariantDtoUsingImageTemplateRequest(imageTemplateRequest,
                    createdBy, mapper);

            productVariantDto.setCountryId(countryId);
            VariantAwaitingApproval variantAwaitingApproval = validateTemplate(productVariantDto, failedProducts,
                    variantAwaitingApprovalList, product);

            if (Objects.isNull(variantAwaitingApproval))
                continue;

            validateImages(createdBy, imageCatalogs, imageTemplateRequest, variantAwaitingApproval);
        }

        if (!failedProducts.isEmpty())
            failedProductsInternalService.saveAllFailedProducts(failedProducts);

        try {
            List<VariantAwaitingApproval> savedVariantsAwaitingApproval = variantAwaitingApprovalInternalService
                    .saveVariantAwaitingApprovalToDb(variantAwaitingApprovalList);

            if (!CollectionUtils.isEmpty(savedVariantsAwaitingApproval))
                imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogs);
        } catch (Exception e) {
            log.info("Error occurred while processing request ", e);
        }

        return missingImagesList;
    }

    private void validateImages(String createdBy, List<ImageCatalog> imageCatalogs,
            ImageUploadTemplateRequest imageTemplateRequest,
            VariantAwaitingApproval variantAwaitingApproval) {

        if (!checkIsBlank(imageTemplateRequest.getImageUrl1())) {
            log.info("image url 1 is {}", imageTemplateRequest.getImageUrl1());
            ImageCatalog imageCatalog1 = ImageCatalogHelper.buildImageCatalog(imageTemplateRequest.getImageUrl1(),
                    createdBy, variantAwaitingApproval);
            imageCatalogs.add(imageCatalog1);
        }
        if (!checkIsBlank(imageTemplateRequest.getImageUrl2())) {
            log.info("image url 2 is {}", imageTemplateRequest.getImageUrl2());
            ImageCatalog imageCatalog2 = ImageCatalogHelper.buildImageCatalog(imageTemplateRequest.getImageUrl2(),
                    createdBy, variantAwaitingApproval);
            imageCatalogs.add(imageCatalog2);
        }
    }

    private VariantAwaitingApproval validateTemplate(ProductVariantDto productVariantDto,
            List<FailedProducts> failedProducts,
            List<VariantAwaitingApproval> variantAwaitingApprovalList, Product product) {
        log.info("logging inside validateTemplate for product: {}", productVariantDto);

        VariantType variantType;

        if (checkIsBlank(productVariantDto.getManufacturerName()) || checkIsBlank(productVariantDto.getBrandName())
                || checkIsBlank(productVariantDto.getProductName())
                || checkIsBlank(productVariantDto.getProductCategoryName())
                || checkIsBlank(productVariantDto.getVariantName())
                || checkIsBlank(productVariantDto.getVariantTypeName())) {
            log.error("data is blank while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("missing.important.data"));
            return null;
        }

        variantType = variantTypeInternalService.findVariantTypeByNameIgnoreCase(productVariantDto.getVariantTypeName())
                .orElseGet(() -> {
                    log.error("Variant type name : {} is not present", productVariantDto.getVariantTypeName());
                    return null;
                });

        if (ObjectUtils.anyNull(variantType)) {
            log.error("Required data is not present while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("invalid.variant.type"));
            return null;
        } else {
            return validateAndCreateVariantAwaitingApproval(productVariantDto, failedProducts,
                    variantAwaitingApprovalList, product, variantType);
        }

    }

    private VariantAwaitingApproval validateAndCreateVariantAwaitingApproval(ProductVariantDto productVariantDto,
            List<FailedProducts> failedProducts, List<VariantAwaitingApproval> variantAwaitingApprovalList,
            Product product, VariantType variantType) {
        List<VariantAwaitingApproval> variantAwaitingApprovals = variantAwaitingApprovalInternalService
                .findByVariantNameAndApprovalStatusNotAndProduct(productVariantDto.getVariantName(),
                        ApprovalStatus.REJECTED, product);

        Optional<ProductVariant> productVariantOptional = productVariantInternalService
                .findProductVariantByNameAndProduct(productVariantDto.getVariantName(), product);

        if (!variantAwaitingApprovals.isEmpty() || productVariantOptional.isPresent()) {
            log.error("variant already exist with variant name: {} and product name: {} ",
                    productVariantDto.getVariantName(),
                    productVariantDto.getProductName());
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("variant.name.already.exists"));
            return null;
        }

        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApprovalHelper.buildVariantAwaitingApproval(
                variantType,
                StringSanitizerUtils.sanitizeInput(productVariantDto.getVariantName()).trim(),
                productVariantDto.getCostPrice(), productVariantDto.getCreatedBy(),
                productVariantDto.getProductVariantDetails(), productVariantDto.getCountryId(),
                productVariantDto.getWeight());
        variantAwaitingApproval.setProduct(product);
        variantAwaitingApproval.setIsVated(product.getVated());

        var responseEntity = locationClientInternalService.getCountryByPublicId(variantAwaitingApproval.getCountryId());

        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        Map<String, Double> configData = getConfigData(responseEntity.getBody().getData().threeLetterCode());

        setVatValueForBulkVariantUpload(variantAwaitingApproval, failedProducts, product, productVariantDto,
                configData);

        assignSku(variantAwaitingApproval, product.getBrand().getBrandName(), product.getProductName().trim());

        variantAwaitingApprovalList.add(variantAwaitingApproval);
        return variantAwaitingApproval;
    }

    private void setVatValueForBulkVariantUpload(VariantAwaitingApproval variantAwaitingApproval,
            List<FailedProducts> failedProducts,
            Product product, ProductVariantDto productVariantDto, Map<String, Double> configData) {
        if (!variantAwaitingApproval.getProduct().getVated() || Objects.isNull(productVariantDto.getVatValue())) {
            variantAwaitingApproval.setVatValue(BigDecimal.ZERO);
            return;
        }

        // if product is vated, validate variant vat value is within product vat range
        boolean vatValueIsGreaterThanMinVat = productVariantDto.getVatValue().compareTo(product.getMinVat()) > 0;
        boolean vatValueIsLessThanMaxVat = productVariantDto.getVatValue().compareTo(product.getMaxVat()) < 0;

        boolean vatValueIsEqualToMinVat = productVariantDto.getVatValue().compareTo(product.getMinVat()) == 0;
        boolean vatValueIsEqualToMaxVat = productVariantDto.getVatValue().compareTo(product.getMaxVat()) == 0;
        boolean vatValueIsEqualToZero = productVariantDto.getVatValue().compareTo(BigDecimal.ZERO) == 0;

        if (vatValueIsGreaterThanMinVat && vatValueIsLessThanMaxVat) {
            variantAwaitingApproval.setVatValue(productVariantDto.getVatValue());

        } else if (vatValueIsEqualToZero || vatValueIsEqualToMinVat || vatValueIsEqualToMaxVat) {
            variantAwaitingApproval.setVatValue(BigDecimal.valueOf(configData.get(DEFAULT_VAT_VALUE)));
        } else {
            log.info("vat value for " + variantAwaitingApproval.getProductVariant().getVariantName()
                    + " is not within min and max vat value");
            addFailedProducts(productVariantDto, failedProducts, "vat value is not within min and max vat value");
        }

    }

    private ProductVariant parseToProductVariant(VariantAwaitingApproval variantAwaitingApproval) {

        return ProductVariant.builder()
                .status(Status.ACTIVE.name())
                .variantName(variantAwaitingApproval.getVariantName())
                .originalPublicId(variantAwaitingApproval.getPublicId())
                .product(variantAwaitingApproval.getProduct())
                .approvedDate(LocalDateTime.now())
                .approvedBy(variantAwaitingApproval.getCreatedBy())
                .countryId(variantAwaitingApproval.getCountryId())
                .originalPublicId(variantAwaitingApproval.getPublicId())
                .variantName(variantAwaitingApproval.getVariantName())
                .variantDescription(variantAwaitingApproval.getVariantDescription())
                .status(variantAwaitingApproval.getStatus())
                .sku(variantAwaitingApproval.getSku())
                .build();
    }

    @Override
    @Transactional
    public void savePriceTemplateRequest(List<PriceTemplateRequest> priceTemplateRequests, String createdBy) {
        log.info("price upload template request size {}", priceTemplateRequests.size());
        List<FailedProducts> failedProducts = new ArrayList<>();
        List<VariantAwaitingApproval> variantAwaitingApprovalList = new ArrayList<>();
        ProductVariantDto productVariantDto;

        for (PriceTemplateRequest priceTemplateRequest : priceTemplateRequests) {

            productVariantDto = TemplateHelper.buildProductVariantDtoUsingPriceTemplateRequest(priceTemplateRequest,
                    createdBy, mapper);
            validateTemplate(productVariantDto, failedProducts, variantAwaitingApprovalList);
        }

        if (!failedProducts.isEmpty())
            failedProductsInternalService.saveAllFailedProducts(failedProducts);
        variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(variantAwaitingApprovalList);
    }

    @Override
    @Transactional
    public void saveStockUpdateTemplateRequest(List<StockUpdateTemplateRequest> stockUpdateTemplateRequests,
            String createdBy) {
        log.info("stock upload template request size {}", stockUpdateTemplateRequests.size());
        List<FailedProducts> failedProducts = new ArrayList<>();
        List<VariantAwaitingApproval> variantAwaitingApprovalList = new ArrayList<>();
        ProductVariantDto productVariantDto;

        for (StockUpdateTemplateRequest stockUpdateTemplateRequest : stockUpdateTemplateRequests) {

            productVariantDto = TemplateHelper.buildProductVariantDtoUsingStockUpdateTemplateRequest(
                    stockUpdateTemplateRequest, createdBy, mapper);
            validateTemplate(productVariantDto, failedProducts, variantAwaitingApprovalList);
        }

        if (!failedProducts.isEmpty())
            failedProductsInternalService.saveAllFailedProducts(failedProducts);
        variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(variantAwaitingApprovalList);
    }

    @Override
    @Transactional
    public void saveCategoryUploadTemplateRequest(List<CategoryUploadTemplateRequest> categoryUploadTemplateRequests,
            String createdBy) {

        List<FailedProducts> failedProducts = new ArrayList<>();
        List<VariantAwaitingApproval> variantAwaitingApprovalList = new ArrayList<>();
        ProductVariantDto productVariantDto;

        for (CategoryUploadTemplateRequest categoryUploadTemplateRequest : categoryUploadTemplateRequests) {

            productVariantDto = TemplateHelper.buildProductVariantDtoUsingCategoryUploadTemplateRequest(
                    categoryUploadTemplateRequest, createdBy, mapper);
            validateTemplate(productVariantDto, failedProducts, variantAwaitingApprovalList);
        }

        if (!failedProducts.isEmpty())
            failedProductsInternalService.saveAllFailedProducts(failedProducts);
        variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(variantAwaitingApprovalList);

    }

    private VariantAwaitingApproval validateTemplate(ProductVariantDto productVariantDto,
            List<FailedProducts> failedProducts, List<VariantAwaitingApproval> variantAwaitingApprovalList) {

        log.info("logging inside validateTemplate for product: {}", productVariantDto);
        Product product;
        Manufacturer manufacturer;
        Brand brand;
        ProductCategory productCategory;
        VariantType variantType;

        if (checkIsBlank(productVariantDto.getManufacturerName()) || checkIsBlank(productVariantDto.getBrandName())
                || checkIsBlank(productVariantDto.getProductName())
                || checkIsBlank(productVariantDto.getProductCategoryName())
                || checkIsBlank(productVariantDto.getVariantName())
                || checkIsBlank(productVariantDto.getVariantTypeName())) {
            log.error("data is blank while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("missing.important.data"));
            return null;
        }

        manufacturer = manufacturerInternalService.findByManufacturerName(productVariantDto.getManufacturerName())
                .orElseGet(() -> {
                    log.error("Manufacture name : {} is not present", productVariantDto.getManufacturerName());
                    return null;
                });
        brand = brandInternalService.findByBrandName(productVariantDto.getBrandName()).orElseGet(() -> {
            log.error("Brand name : {} is not present", productVariantDto.getBrandName());
            return null;
        });
        productCategory = productCategoryInternalService
                .findProductCategoryByNameIgnoreCase(productVariantDto.getProductCategoryName()).orElseGet(() -> {
                    log.error("Product category name : {} is not present", productVariantDto.getProductCategoryName());
                    return null;
                });
        variantType = variantTypeInternalService.findVariantTypeByNameIgnoreCase(productVariantDto.getVariantTypeName())
                .orElseGet(() -> {
                    log.error("Variant type name : {} is not present", productVariantDto.getVariantTypeName());
                    return null;
                });

        if (ObjectUtils.anyNull(manufacturer, brand, productCategory, variantType)) {
            log.error("Required data is not present while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("missing.important.data.v2"));
            return null;
        } else {
            Optional<Product> productOptional = productInternalService
                    .getProductByNameAndBrandAndManufacturer(
                            StringSanitizerUtils.sanitizeInput(productVariantDto.getProductName()).trim(),
                            brand, manufacturer);
            if (productOptional.isEmpty()) {
                MeasuringUnit measuringUnit = measuringUnitInternalService
                        .findByName(productVariantDto.getMeasurementUnit());

                product = ProductHelper.buildProduct(productVariantDto.getProductName().trim(), manufacturer, brand,
                        productCategory, productVariantDto.getCreatedBy(), measuringUnit);

                if (Objects.nonNull(brand) && Objects.nonNull(manufacturer)
                        && !Objects.equals(brand.getManufacturer().getPublicId(), manufacturer.getPublicId())) {
                    log.error("mismatch of product {} ",
                            messageSourceService.getMessageByKey("product.manufacturer.brand.mismatch"));
                    return null;
                }
                product.setVated(productVariantDto.getVated());

                product = productInternalService.saveProductToDb(product);
            } else {
                product = productOptional.get();
            }
            return validateAndCreateVariantAwaitingApproval(productVariantDto, failedProducts,
                    variantAwaitingApprovalList, product, variantType);
        }
    }

    private VariantAwaitingApproval validateTemplate(ProductVariantDto productVariantDto,
            List<FailedProducts> failedProducts,
            List<VariantAwaitingApproval> variantAwaitingApprovalList,
            ImageUploadTemplateRequest request, Map<String, Double> configData) {

        log.info("logging inside validateTemplate for product: {}", productVariantDto);
        Product product;
        Manufacturer manufacturer;
        Brand brand;
        ProductCategory productCategory;
        VariantType variantType;

        if (checkIsBlank(productVariantDto.getManufacturerName()) || checkIsBlank(productVariantDto.getBrandName())
                || checkIsBlank(productVariantDto.getProductName())
                || checkIsBlank(productVariantDto.getProductCategoryName())
                || checkIsBlank(productVariantDto.getVariantName())
                || checkIsBlank(productVariantDto.getVariantTypeName())) {
            log.error("data is blank while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("missing.important.data"));
            return null;
        }

        manufacturer = manufacturerInternalService.findByManufacturerName(productVariantDto.getManufacturerName())
                .orElseGet(() -> {
                    log.error("Manufacturer name : {} is not present", productVariantDto.getManufacturerName());
                    return null;
                });
        brand = brandInternalService.findByBrandName(productVariantDto.getBrandName()).orElseGet(() -> {
            log.error("Brand name : {} is not present", productVariantDto.getBrandName());
            return null;
        });
        productCategory = productCategoryInternalService
                .findProductCategoryByNameIgnoreCase(productVariantDto.getProductCategoryName()).orElseGet(() -> {
                    log.error("Product category name : {} is not present", productVariantDto.getProductCategoryName());
                    return null;
                });
        variantType = variantTypeInternalService.findVariantTypeByNameIgnoreCase(productVariantDto.getVariantTypeName())
                .orElseGet(() -> {
                    log.error("Variant type name : {} is not present", productVariantDto.getVariantTypeName());
                    return null;
                });

        if (ObjectUtils.anyNull(manufacturer, brand, productCategory, variantType)) {
            log.error("Required data is not present while uploading product: {}", productVariantDto);
            addFailedProducts(productVariantDto, failedProducts,
                    messageSourceService.getMessageByKey("missing.important.data.v2"));
            return null;
        } else {
            Optional<Product> productOptional = productInternalService
                    .getProductByNameAndBrandAndManufacturer(
                            StringSanitizerUtils.sanitizeInput(productVariantDto.getProductName()).trim(),
                            brand, manufacturer);
            if (productOptional.isEmpty()) {
                MeasuringUnit measuringUnit = measuringUnitInternalService
                        .findByName(productVariantDto.getMeasurementUnit());

                product = ProductHelper.buildProduct(productVariantDto.getProductName().trim(), manufacturer, brand,
                        productCategory, productVariantDto.getCreatedBy(), measuringUnit);

                validateAndSetVatMinAndMax(request, product, configData);

                if (Objects.nonNull(brand) && Objects.nonNull(manufacturer)
                        && !Objects.equals(brand.getManufacturer().getPublicId(), manufacturer.getPublicId())) {
                    log.error("mismatch of product {} ",
                            messageSourceService.getMessageByKey("product.manufacturer.brand.mismatch"));
                    return null;
                }
                product.setVated(productVariantDto.getVated());

                product = productInternalService.saveProductToDb(product);
            } else {
                product = productOptional.get();
            }
            return validateAndCreateVariantAwaitingApproval(productVariantDto, failedProducts,
                    variantAwaitingApprovalList, product, variantType);
        }
    }

    private static void validateAndSetVatMinAndMax(ImageUploadTemplateRequest requestDto, Product product,
            Map<String, Double> configData) {
        // if min vat is greater than max vat, set both to country default min and max
        if (requestDto.getMinVat().compareTo(requestDto.getMaxVat()) > 0) {
            log.info("Min VAT cannot be greater than Max VAT, resetting both to 0");
            requestDto.setMinVat(BigDecimal.valueOf(configData.get(DEFAULT_MIN_VAT)));
            requestDto.setMaxVat(BigDecimal.valueOf(configData.get(DEFAULT_MAX_VAT)));
        }

        if (requestDto.isVated()) {
            boolean minVatIsNullOrLessThanZero = Objects.isNull(requestDto.getMinVat()) ||
                    requestDto.getMinVat().compareTo(BigDecimal.ZERO) < 0;
            requestDto.setMinVat(minVatIsNullOrLessThanZero ? BigDecimal.valueOf(configData.get(DEFAULT_MIN_VAT))
                    : requestDto.getMinVat());

            boolean maxVatIsNullOrLessThanMinVatOrEqualToMinVat = Objects.isNull(requestDto.getMaxVat()) ||
                    requestDto.getMaxVat().compareTo(requestDto.getMinVat()) <= 0;
            requestDto.setMaxVat(
                    maxVatIsNullOrLessThanMinVatOrEqualToMinVat ? BigDecimal.valueOf(configData.get(DEFAULT_MAX_VAT))
                            : requestDto.getMaxVat());
        }

        product.setMinVat(requestDto.getMinVat());
        product.setMaxVat(requestDto.getMaxVat());
    }

    private void addFailedProducts(ProductVariantDto productVariantDto, List<FailedProducts> failedProductsList,
            String reason) {
        FailedProducts failedProducts = FailedProductsHelper.buildFailedProductsUsingImageUploadTemplateRequest(
                productVariantDto.getManufacturerName(), productVariantDto.getBrandName(),
                productVariantDto.getProductCategoryName(), productVariantDto.getProductName().trim(),
                productVariantDto.getCreatedBy(), reason);
        failedProducts.setProductDetails(productVariantDto.getProductVariantDetails());
        failedProductsList.add(failedProducts);
    }

    private boolean checkIsBlank(String name) {
        return StringUtils.isBlank(name);
    }

    private Product updateProduct(Product product, UpdateProductRequestDto requestDto) {
        if (StringUtils.isNotBlank(requestDto.getProductName()))
            product.setProductName(WordUtils.capitalizeFully(requestDto.getProductName().toUpperCase().trim()));

        if (StringUtils.isNotBlank(requestDto.getProductDescription()))
            product.setProductDescription(requestDto.getProductDescription());

        if (StringUtils.isNotBlank(requestDto.getProductListing()))
            product.setProductListing(requestDto.getProductListing());

        if (StringUtils.isNotBlank(requestDto.getProductHighlights()))
            product.setProductHighlights(requestDto.getProductHighlights());

        if (!Objects.isNull(requestDto.getMeasurementUnitPublicId()))
            product.setMeasurementUnit(
                    measuringUnitInternalService.findByPublicId(requestDto.getMeasurementUnitPublicId()));

        if (StringUtils.isNotBlank(requestDto.getWarrantyAddress()))
            product.setWarrantyAddress(requestDto.getWarrantyAddress());

        if (!Objects.isNull(requestDto.getWarrantyTypePublicId()))
            product.setWarrantyType(warrantyTypeInternalService.findByPublicId(requestDto.getWarrantyTypePublicId()));

        if (StringUtils.isNotBlank(requestDto.getWarrantyDuration()))
            product.setWarrantyDuration(requestDto.getWarrantyDuration());

        if (StringUtils.isNotBlank(requestDto.getWarrantyCover()))
            product.setWarrantyCover(requestDto.getWarrantyCover());

        if (!Objects.isNull(requestDto.getBrandPublicId()))
            product.setBrand(brandInternalService.findByPublicId(requestDto.getBrandPublicId()));

        if (!Objects.isNull(requestDto.getManufacturerPublicId()))
            product.setManufacturer(
                    manufacturerInternalService.findByPublicId(requestDto.getManufacturerPublicId()).orElseThrow(
                            () -> new NoResultException(
                                    messageSourceService.getMessageByKey("manufacturer.not.found"))));

        if (!Objects.isNull(requestDto.getCategoryPublicId()))
            product.setProductCategory(
                    productCategoryInternalService.findProductCategoryByPublicId(requestDto.getCategoryPublicId()));

        if (StringUtils.isNotBlank(requestDto.getModifiedBy())) {
            product.setLastModifiedBy(requestDto.getModifiedBy());
        }

        product.setMinVat(requestDto.getMinVat() == null || requestDto.getMinVat().compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : requestDto.getMinVat());
        product.setMaxVat(requestDto.getMaxVat() == null || requestDto.getMaxVat().compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : requestDto.getMaxVat());

        return productInternalService.saveProductToDb(product);
    }

    private List<VariantAwaitingApproval> buildVariantAwaitingApproval(
            List<CreateVariantAwaitingApprovalRequestDto> requestDto, List<CountryDto> countries, Product product) {
        List<VariantAwaitingApproval> variantList = new ArrayList<>();

        for (CreateVariantAwaitingApprovalRequestDto request : requestDto) {

            Optional<CountryDto> countryDto = countries.stream()
                    .filter(country -> country.publicId().equals(request.getCountryPublicId())).findFirst();

            if (countryDto.isEmpty())
                throw new ModelNotFoundException(messageSourceService.getMessageByKey("country.not.found",
                        request.getCountryPublicId().toString()));

            VariantType variantType = variantTypeInternalService.findVariantTypeByPublicId(request.getVariantTypeId());

            VariantAwaitingApproval variantAwaitingApproval = VariantHelper.buildVariantAwaitingApproval(request,
                    countryDto.get(), product, variantType);

            validateAndSaveVatValue(product, variantAwaitingApproval, request, countryDto.get().threeLetterCode());

            assignSku(variantAwaitingApproval, product.getBrand().getBrandName(), product.getProductName());

            variantList.add(variantAwaitingApproval);
        }
        return variantList;
    }

    private void validateAndSaveVatValue(Product product, VariantAwaitingApproval variantAwaitingApproval,
            CreateVariantAwaitingApprovalRequestDto requestDto, String countryCode) {

        // if product is not vated or variant vat value is not provided, set vat value
        // to zero
        if (!product.getVated() || Objects.isNull(requestDto.getVatValue())) {
            variantAwaitingApproval.setVatValue(BigDecimal.ZERO);
            return;
        }

        // if product is vated, validate variant vat value is within product vat range
        boolean vatValueIsGreaterThanMinVat = requestDto.getVatValue().compareTo(product.getMinVat()) > 0;
        boolean vatValueIsLessThanMaxVat = requestDto.getVatValue().compareTo(product.getMaxVat()) < 0;

        boolean vatValueIsEqualToMinVat = requestDto.getVatValue().compareTo(product.getMinVat()) == 0;
        boolean vatValueIsEqualToMaxVat = requestDto.getVatValue().compareTo(product.getMaxVat()) == 0;
        boolean vatValueIsEqualToZero = requestDto.getVatValue().compareTo(BigDecimal.ZERO) == 0;

        if (vatValueIsGreaterThanMinVat && vatValueIsLessThanMaxVat) {
            variantAwaitingApproval.setVatValue(requestDto.getVatValue());

        } else if (vatValueIsEqualToZero || vatValueIsEqualToMinVat || vatValueIsEqualToMaxVat) {
            Map<String, Double> configData = getConfigData(countryCode);
            variantAwaitingApproval.setVatValue(BigDecimal.valueOf(configData.get(DEFAULT_VAT_VALUE)));
        } else {
            throw new ValidatorException("vat value for " + variantAwaitingApproval.getProductVariant().getVariantName()
                    + " is not within min and max vat value");
        }

    }

    private void assignSku(VariantAwaitingApproval variant, String brandName, String productName) {
        String sku = null;
        Optional<VariantAwaitingApproval> foundVariant;
        do {
            sku = generateSku(brandName, productName);

            // check variant awaiting approval table for duplicate because it contains all
            // SKU, regardless of approval status
            foundVariant = variantAwaitingApprovalInternalService.findVariantBySku(sku);
        } while (foundVariant.isPresent());

        variant.setSku(sku);
    }

    private String generateSku(String brandName, String productName) {

        String serialNumber = generateSerialNumber();

        String brand = StringUtils.left(brandName.replaceAll("\\s", ""), 3);
        String product = StringUtils.left(productName.replaceAll("\\s", ""), 3);
        return brand.toUpperCase() + "-" + product.toUpperCase() + "-" + serialNumber;
    }

    private String generateSerialNumber() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return String.format("%05d", num);
    }

    @Override
    public AppResponse deleteProduct(UUID publicId) {
        Product product = productInternalService.findByPublicId(publicId);
        if (product.getStatus().equals(Status.DELETED.name())) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("product.not.found"));
        }
        product.setStatus(Status.DELETED.name());

        Product deletedProduct = productInternalService.deleteProduct(product);
        log.info("Product has been deleted {}", deletedProduct.getStatus());

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.deleted.successfully"),
                messageSourceService.getMessageByKey("product.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse updateProductArchiveStatus(UUID publicId, String status) {

        var product = productInternalService.findByPublicId(publicId);
        AppResponse appResponse = null;

        if (status.equalsIgnoreCase(Status.INACTIVE.name())) {

            if (product.getStatus().equals(Status.INACTIVE.name()))
                throw new ValidatorException(messageSourceService.getMessageByKey("product.already.archived"));

            var archivedProduct = productInternalService.updateProductArchiveStatus(publicId, status);
            variantInternalService.updateProductVariantsArchiveStatus(archivedProduct, status);

            appResponse = new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("product.archived.successfully"),
                    messageSourceService.getMessageByKey("product.archived.successfully"),
                    null, null);

        } else if (status.equalsIgnoreCase(Status.ACTIVE.name())) {

            if (product.getStatus().equals(Status.ACTIVE.name()))
                throw new ValidatorException(messageSourceService.getMessageByKey("product.already.unarchived"));

            productInternalService.updateProductArchiveStatus(publicId, status);

            appResponse = new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("product.unarchived.successfully"),
                    messageSourceService.getMessageByKey("product.unarchived.successfully"),
                    null, null);
        }
        return appResponse;
    }

    @Override
    public AppResponse getProductsByProductCategory(UUID categoryId) {

        List<Product> producList = productInternalService.findByCategory(categoryId);
        List<UUID> productPublicIds = producList.stream().map(BaseEntity::getPublicId).collect(Collectors.toList());
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                productPublicIds, null);
    }

    @Override
    public AppResponse getApprovedProductsByPublicIdList(List<UUID> prodPublicIdList) {
        List<UUID> approvedProductList = new ArrayList<>();

        prodPublicIdList.forEach(productPublicId -> {
            if (checkApprovedProduct(productPublicId)) {
                approvedProductList.add(productPublicId);
            }
        });

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                approvedProductList, null);
    }

    @Override
    public AppResponse getApprovedProductsPublicIdListUsingSku(List<String> skuList) {
        List<UUID> productIdList = variantVersionInternalService.findAllBySkuIn(skuList)
                .stream().map(variant -> variant.getProductVariant().getProduct().getId()).toList();
        List<UUID> productPublicIdList = productInternalService.findAllById(productIdList)
                .stream().map(BaseEntity::getPublicId).toList();

        if (productPublicIdList.isEmpty()) {
            return new AppResponse(HttpStatus.NOT_FOUND.value(),
                    messageSourceService.getMessageByKey("product.not.found"),
                    messageSourceService.getMessageByKey("product.not.found"),
                    null, null);
        }

        return getApprovedProductsByPublicIdList(productPublicIdList);
    }

    @Override
    public AppResponse getProductsByBrand(UUID brandPublicId) {
        Brand brand = brandInternalService.findByPublicId(brandPublicId);

        List<Product> productsByBrand = productInternalService.getProductsByBrandId(brand.getId());

        List<ProductResponseDto> productResponseDtos = getProductResponse(productsByBrand);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.retrieved.successfully"),
                messageSourceService.getMessageByKey("product.retrieved.successfully"),
                productResponseDtos, null);
    }

    @Override
    public AppResponse getProductByPublicId(UUID publicId) {
        Product product = productInternalService.findByPublicIdAndStatus(publicId);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.retrieved.successfully"),
                messageSourceService.getMessageByKey("product.retrieved.successfully"),
                ProductHelper.buildProductResponse(product), null);
    }

    private AppResponse csvFileUpload(MultipartFile file, String createdBy, UUID traceId) throws IOException {
        if (file.getOriginalFilename().equals("price-upload-template.csv")) {
            BulkProductUploadRequest dataFromCsvForPriceUploadTemplate = getDataFromCsvForPriceUploadTemplate(
                    file.getInputStream());
            return sendToQueue(dataFromCsvForPriceUploadTemplate, traceId, createdBy);
        } else if (file.getOriginalFilename().equals("stock-upload-template.csv")) {
            BulkProductUploadRequest dataFromCsvForStockUploadTemplate = getDataFromCsvForStockUploadTemplate(
                    file.getInputStream());
            return sendToQueue(dataFromCsvForStockUploadTemplate, traceId, createdBy);
        } else if (file.getOriginalFilename().equals("image-upload-template.csv")) {

            log.info("image-upload{}", file);
            BulkProductUploadRequest dataFromCsvForImageUploadTemplate = getDataFromCsvForImageUploadTemplate(
                    file.getInputStream());
            return sendToQueue(dataFromCsvForImageUploadTemplate, traceId, createdBy);

        }
        throw new FileNotFoundException(messageSourceService.getMessageByKey("please.add.valid.file"));
    }

    @Override
    public AppResponse uploadProductUsingExcel(MultipartFile file, String createdBy, UUID traceId) throws IOException {
        if (file.getOriginalFilename().endsWith(".csv")) {
            log.info("in csv files");
            return csvFileUpload(file, createdBy, traceId);
        } else {
            log.info("into excel file {}", file);
            BulkProductUploadRequest dataFromExcel = getDataFromExcel(file.getInputStream());
            return sendToQueue(dataFromExcel, traceId, createdBy);
        }

    }

    @Override
    public AppResponse createProductCatalogue(UUID traceId, UUID warehouseId, UUID stateId, UUID cityId, UUID lgaId,
            String searchValue, Integer page, Integer size) {
        List<String> skuList = new ArrayList<>();
        List<VariantVersion> variantVersions = new ArrayList<>();
        List<ProductCatalogueResponseDto> productCatalogueResponseDtos = new ArrayList<>();
        Map<String, PriceModelResponseDto> priceModelMap = new HashMap<>();

        if (validatePage(page, size)) {
            throw new PageableException(messageSourceService.getMessageByKey("page.size.error"));
        }
        if (Objects.isNull(warehouseId) && Objects.isNull(stateId)) {
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.warehouseId.or.stateId"));
        }

        LiveInventoryResponse liveInventoryResponse = getLiveInventoryResponse(traceId, warehouseId, stateId);
        if (Objects.nonNull(liveInventoryResponse) && !liveInventoryResponse.getLiveInventoryProducts().isEmpty()) {
            variantVersions = variantVersionInternalService.findVariantBySkuList(
                    liveInventoryResponse.getLiveInventoryProducts().stream().map(InventoryData::sku_code).toList());
        }
        List<SupplierProductDetailsResponseDto> supplierProductDetailsResponseDtos = getVirtualInventoryResponse(
                stateId, cityId, lgaId);

        if (StringUtils.isNotBlank(searchValue)) {
            variantVersions = variantVersions.stream()
                    .filter(variantVersion -> variantVersion.getVariantName().contains(searchValue)).toList();
            supplierProductDetailsResponseDtos = supplierProductDetailsResponseDtos.stream()
                    .filter(supplierProductDetailsResponseDto -> supplierProductDetailsResponseDto.getVariantName()
                            .contains(searchValue))
                    .toList();
        }
        skuList.addAll(variantVersions.stream().map(VariantVersion::getSku).toList());
        skuList.addAll(
                supplierProductDetailsResponseDtos.stream().map(SupplierProductDetailsResponseDto::getSku).toList());
        List<PriceModelResponseDto> priceModelResponseDtos = getPriceModelResponse(skuList);

        for (PriceModelResponseDto priceModel : priceModelResponseDtos) {
            priceModelMap.putIfAbsent(priceModel.getProductSku(), priceModel);
        }
        variantVersions.forEach(variantVersion -> {
            Optional<InventoryData> inventoryDataOptional = liveInventoryResponse.getLiveInventoryProducts()
                    .stream().filter(data -> data.sku_code().equals(variantVersion.getSku()))
                    .findFirst();
            if (inventoryDataOptional.isPresent()) {
                PriceModelResponseDto priceModelResponse = priceModelMap.get(inventoryDataOptional.get().sku_code());
                if (Objects.nonNull(priceModelResponse)) {
                    ProductCatalogueResponseDto productCatalogueResponseDto = ProductHelper
                            .buildProductCatalogueResponse(liveInventoryResponse, variantVersion,
                                    inventoryDataOptional.get(), priceModelResponse);
                    productCatalogueResponseDtos.add(productCatalogueResponseDto);
                }
            }
        });
        supplierProductDetailsResponseDtos.forEach(supplierProductDetailsResponseDto -> {
            PriceModelResponseDto priceModelResponse = priceModelMap.get(supplierProductDetailsResponseDto.getSku());
            if (Objects.nonNull(priceModelResponse)) {
                ProductCatalogueResponseDto productCatalogueResponseDto = ProductHelper
                        .buildProductCatalogueResponse(supplierProductDetailsResponseDto, priceModelResponse);
                productCatalogueResponseDtos.add(productCatalogueResponseDto);
            }
        });

        List<UUID> productVariantPublicIds = productCatalogueResponseDtos.stream()
                .map(ProductCatalogueResponseDto::getVariantPublicId).toList();
        List<ProductVariant> productVariantsByPublicIds = variantInternalService
                .findProductVariantsByPublicIds(productVariantPublicIds);
        List<ImageCatalog> imageCatalogs = imageCatalogInternalService
                .findByProductVariants(productVariantsByPublicIds);
        Map<UUID, List<ImageCatalog>> imageMap = imageCatalogs.stream()
                .collect(Collectors.groupingBy(imgCatalog -> imgCatalog.getProductVariant().getPublicId()));
        List<ProductCatalogueResponseDto> productCatalogueResDto = productCatalogueResponseDtos.stream()
                .map(productCatalogueResponse -> {
                    productCatalogueResponse
                            .setImageUrls(getImageUrls(imageMap, productCatalogueResponse.getVariantPublicId()));
                    return productCatalogueResponse;
                }).toList();

        int offset = (page - 1) * size;
        int limit = Math.min(offset + size, productCatalogueResDto.size());
        List<ProductCatalogueResponseDto> paginatedProductInfo = productCatalogueResDto.subList(offset, limit);
        Page<ProductCatalogueResponseDto> productCataloguePage = new PageImpl<>(paginatedProductInfo,
                PageRequest.of(page - 1, size), productCatalogueResDto.size());
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"), productCataloguePage,
                null);
    }

    private static boolean validatePage(int page, int size) {
        return page < 1 || size <= 0;
    }

    private List<String> getImageUrls(Map<UUID, List<ImageCatalog>> imageMap, UUID variantPublicId) {
        List<ImageCatalog> imageCatalogs = imageMap.get(variantPublicId);
        if (!CollectionUtils.isEmpty(imageCatalogs)) {
            return imageCatalogs.stream().map(ImageCatalog::getImageUrl).toList();
        }
        return new ArrayList<>();
    }

    private LiveInventoryResponse getLiveInventoryResponse(UUID traceId, UUID warehouseId, UUID stateId) {
        LiveInventoryResponse liveInventoryData = null;
        try {
            AppResponse liveInventoryResponse = inventoryClientInternalService
                    .getStockOneLiveInventoryProducts(String.valueOf(traceId), stateId, warehouseId);
            log.info("live inventory response {}", liveInventoryResponse.getData());
            liveInventoryData = mapper.convertValue(liveInventoryResponse.getData(), LiveInventoryResponse.class);
        } catch (Exception ex) {
            log.error("error while fetching live inventory -> {}", ex.getMessage());
        }
        return liveInventoryData;
    }

    public List<SupplierProductDetailsResponseDto> getVirtualInventoryResponse(UUID stateId, UUID cityId, UUID lgaId) {
        List<SupplierProductDetailsResponseDto> supplierProductDetailsResponseDtos = new ArrayList<>();
        try {
            AppResponse virtualInventoryResponse = inventoryClientInternalService.filterVirtualStorageProducts(stateId,
                    cityId, lgaId);
            log.info("virtual inventory response {}", virtualInventoryResponse.getData());
            supplierProductDetailsResponseDtos = mapper.convertValue(virtualInventoryResponse.getData(),
                    new TypeReference<List<SupplierProductDetailsResponseDto>>() {
                    });

        } catch (Exception ex) {
            log.error("error while fetching virtual inventory -> {}", ex.getMessage());
        }
        return supplierProductDetailsResponseDtos;
    }

    private List<PriceModelResponseDto> getPriceModelResponse(List<String> skuList) {
        List<PriceModelResponseDto> priceModelResponseList = new ArrayList<>();
        try {
            AppResponse priceModelResponse = shoppingExperienceClientInternalService.getPriceModelBySkuList(skuList)
                    .getBody();
            log.info("priceModel response {}", priceModelResponse.getData());
            priceModelResponseList = mapper.convertValue(priceModelResponse.getData(),
                    new TypeReference<List<PriceModelResponseDto>>() {
                    });
        } catch (Exception ex) {
            log.error("error while fetching price model -> {}", ex.getMessage());
            throw new NotFoundException(messageSourceService.getMessageByKey("price.model.not.found"));
        }
        return priceModelResponseList;
    }

    private BulkProductUploadRequest getDataFromCsvForImageUploadTemplate(InputStream imageUploadTemplate)
            throws IOException {
        BulkProductUploadRequest bulkProductUploadRequest = new BulkProductUploadRequest();
        List<ImageUploadTemplateRequest> imageUploadTemplateRequests = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(imageUploadTemplate));
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build();
            String[] record = null;

            while ((record = csvReader.readNext()) != null) {
                ImageUploadTemplateRequest imageUploadTemplateRequest = new ImageUploadTemplateRequest();
                log.info("record {}", Arrays.toString(record));
                imageUploadTemplateRequest.setBusinessName(UtilsHelper.validateAndTrim(record[0]));
                imageUploadTemplateRequest.setManufacturerName(UtilsHelper.validateAndTrim(record[1]));
                imageUploadTemplateRequest.setBrand(UtilsHelper.validateAndTrim(record[2]));
                imageUploadTemplateRequest.setProductCategory(UtilsHelper.validateAndTrim(record[3]));
                imageUploadTemplateRequest.setProductName(UtilsHelper.validateAndTrim(record[5]));
                imageUploadTemplateRequest.setVariantType(UtilsHelper.validateAndTrim(record[6]));
                imageUploadTemplateRequest.setVariantName(UtilsHelper.validateAndTrim(record[7]));
                imageUploadTemplateRequest.setWeight(ParseDouble(UtilsHelper.validateAndTrim(record[8])));
                imageUploadTemplateRequest.setCostPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[9])));
                imageUploadTemplateRequest.setListingPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[10])));
                imageUploadTemplateRequest.setImageUrl1(UtilsHelper.validateAndTrim(record[11]));
                imageUploadTemplateRequest.setImageUrl2(UtilsHelper.validateAndTrim(record[12]));
                log.info("processing imageUploadTemplateRequest {}", imageUploadTemplateRequest);
                imageUploadTemplateRequests.add(imageUploadTemplateRequest);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FileNotFoundException(messageSourceService.getMessageByKey("please.add.data.in.file"));
        }

        bulkProductUploadRequest.setImageUploadTemplateRequests(imageUploadTemplateRequests);
        return bulkProductUploadRequest;
    }

    private BulkProductUploadRequest getDataFromCsvForPriceUploadTemplate(InputStream is) throws IOException {
        BulkProductUploadRequest bulkProductUploadRequest = new BulkProductUploadRequest();
        List<PriceTemplateRequest> priceTemplateRequests = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is));
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build();
            String[] record = null;

            while ((record = csvReader.readNext()) != null) {
                PriceTemplateRequest priceTemplateRequest = new PriceTemplateRequest();
                log.info("record {}", Arrays.toString(record));
                priceTemplateRequest.setManufacturerName(UtilsHelper.validateAndTrim(record[0]));
                priceTemplateRequest.setBrand(UtilsHelper.validateAndTrim(record[1]));
                priceTemplateRequest.setProductCategory(UtilsHelper.validateAndTrim(record[2]));
                priceTemplateRequest.setSubCategory(UtilsHelper.validateAndTrim(record[3]));
                priceTemplateRequest.setProductName(UtilsHelper.validateAndTrim(record[4]));
                priceTemplateRequest.setVariantType(UtilsHelper.validateAndTrim(record[5]));
                priceTemplateRequest.setVariantName(UtilsHelper.validateAndTrim(record[6]));
                priceTemplateRequest.setWeight(ParseDouble(UtilsHelper.validateAndTrim(record[7])));
                priceTemplateRequest.setCostPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[8])));
                priceTemplateRequest.setListingPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[9])));
                priceTemplateRequest.setMaterial(UtilsHelper.validateAndTrim(record[10]));
                priceTemplateRequest.setColor(UtilsHelper.validateAndTrim(record[11]));
                priceTemplateRequest.setDimension(UtilsHelper.validateAndTrim(record[12]));
                priceTemplateRequest.setMoq1(ParseLong(UtilsHelper.validateAndTrim(record[13])));
                priceTemplateRequest.setMoq1Price(ParseBigDec(UtilsHelper.validateAndTrim(record[14])));
                priceTemplateRequest.setMoq2(ParseLong(UtilsHelper.validateAndTrim(record[15])));
                priceTemplateRequest.setMoq2Price(ParseBigDec(UtilsHelper.validateAndTrim(record[16])));
                log.info("processing priceTemplateRequest {}", priceTemplateRequest);
                priceTemplateRequests.add(priceTemplateRequest);
            }

        } catch (Exception ex) {
            throw new FileNotFoundException(messageSourceService.getMessageByKey("please.add.data.in.file"));
        }
        bulkProductUploadRequest.setPriceTemplateRequests(priceTemplateRequests);
        return bulkProductUploadRequest;
    }

    private BulkProductUploadRequest getDataFromCsvForStockUploadTemplate(InputStream is) throws IOException {
        BulkProductUploadRequest bulkProductUploadRequest = new BulkProductUploadRequest();
        List<StockUpdateTemplateRequest> stockUpdateTemplateRequests = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(is));
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build();
            String[] record = null;

            while ((record = csvReader.readNext()) != null) {
                StockUpdateTemplateRequest stockUpdateTemplateRequest = new StockUpdateTemplateRequest();
                log.info("record {}", Arrays.toString(record));
                stockUpdateTemplateRequest.setBusinessName(UtilsHelper.validateAndTrim(record[0]));
                stockUpdateTemplateRequest.setManufacturerName(UtilsHelper.validateAndTrim(record[1]));
                stockUpdateTemplateRequest.setBrand(UtilsHelper.validateAndTrim(record[2]));
                stockUpdateTemplateRequest.setProductCategory(UtilsHelper.validateAndTrim(record[3]));
                stockUpdateTemplateRequest.setSubCategory(UtilsHelper.validateAndTrim(record[4]));
                stockUpdateTemplateRequest.setProductName(UtilsHelper.validateAndTrim(record[5]));
                stockUpdateTemplateRequest.setVariantType(UtilsHelper.validateAndTrim(record[6]));
                stockUpdateTemplateRequest.setVariantName(UtilsHelper.validateAndTrim(record[7]));
                stockUpdateTemplateRequest.setWeight(ParseDouble(UtilsHelper.validateAndTrim(record[8])));
                stockUpdateTemplateRequest.setCostPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[9])));
                stockUpdateTemplateRequest.setListingPrice(ParseBigDec(UtilsHelper.validateAndTrim(record[10])));
                stockUpdateTemplateRequest.setQuantityToUpload(ParseBigInt(UtilsHelper.validateAndTrim(record[11])));
                log.info("processing stockUpdateTemplateRequest {}", stockUpdateTemplateRequest);
                stockUpdateTemplateRequests.add(stockUpdateTemplateRequest);
            }

        } catch (Exception ex) {
            throw new FileNotFoundException(messageSourceService.getMessageByKey("please.add.data.in.file"));
        }
        bulkProductUploadRequest.setStockUpdateTemplateRequests(stockUpdateTemplateRequests);
        return bulkProductUploadRequest;
    }

    private AppResponse sendToQueue(BulkProductUploadRequest bulkProductUploadRequest, UUID traceId, String createdBy)
            throws JsonProcessingException {

        BulkProductUploadRequest queueMessageRequest = BulkProductUploadRequest.builder().build();

        queueMessageRequest.setImageUploadTemplateRequests(bulkProductUploadRequest.getImageUploadTemplateRequests());
        queueMessageRequest.setPriceTemplateRequests(bulkProductUploadRequest.getPriceTemplateRequests());
        queueMessageRequest.setStockUpdateTemplateRequests(bulkProductUploadRequest.getStockUpdateTemplateRequests());
        queueMessageRequest
                .setCategoryUploadTemplateRequests(bulkProductUploadRequest.getCategoryUploadTemplateRequests());
        queueMessageRequest.setTraceId(traceId);
        queueMessageRequest.setCreatedBy(createdBy);
        log.info("in a queueMessageRequest{}", queueMessageRequest);

        MessageContentEvent contentEvent = new MessageContentEvent<>(TypeMessage.PRODUCT, queueMessageRequest);
        azureBusMessageQueueService.sendMessage(contentEvent);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"),
                null, null);
    }

    private BulkProductUploadRequest getDataFromExcel(InputStream filePart) throws IOException {
        BulkProductUploadRequest bulkProductUploadRequest = new BulkProductUploadRequest();
        List<ImageUploadTemplateRequest> imageUploadTemplateRequests = new ArrayList<>();
        List<StockUpdateTemplateRequest> stockUpdateTemplateRequests = new ArrayList<>();
        List<PriceTemplateRequest> priceTemplateRequests = new ArrayList<>();
        Map<String, List<CategoryUploadTemplateRequest>> categoryUploadTemplateRequests = new HashMap();
        final var categoryUploadSheets = List.of("agric", "baby and kids", "power solutions", "phones & tablets",
                "other categories",
                "office", "household supplies", "home & kitchen", "groceries", "electronics", "drinks",
                "computers & accessories", "cleaning", "beauty & personal care", "automotive");

        try {
            // Create workbook object from byte input stream
            Workbook workbook = WorkbookFactory.create(filePart);
            log.info("in the excel{}", filePart);
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                sheet = workbook.getSheetAt(i);
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName.trim().equalsIgnoreCase("image upload template")) {
                    imageUploadTemplateRequests = PoiExcelParser.parseFromExcel(sheet,
                            ImageUploadTemplateRequest.class);

                } else if (sheetName.trim().equalsIgnoreCase("Stock update")) {
                    stockUpdateTemplateRequests = PoiExcelParser.parseFromExcel(sheet,
                            StockUpdateTemplateRequest.class);

                } else if (sheetName.trim().equalsIgnoreCase("Price template")) {
                    priceTemplateRequests = PoiExcelParser.parseFromExcel(sheet, PriceTemplateRequest.class);

                } else {
                    if (categoryUploadSheets.contains(sheetName.trim().toLowerCase())) {
                        List<CategoryUploadTemplateRequest> categoryData = PoiExcelParser.parseFromExcel(sheet,
                                CategoryUploadTemplateRequest.class);
                        categoryUploadTemplateRequests.put(sheetName, categoryData);
                    }
                }

            }
            bulkProductUploadRequest.setImageUploadTemplateRequests(imageUploadTemplateRequests);
            bulkProductUploadRequest.setStockUpdateTemplateRequests(stockUpdateTemplateRequests);
            bulkProductUploadRequest.setPriceTemplateRequests(priceTemplateRequests);
            bulkProductUploadRequest.setCategoryUploadTemplateRequests(categoryUploadTemplateRequests);
            return bulkProductUploadRequest;

        } catch (Exception ee) {
            ee.printStackTrace();
            throw new FileNotFoundException(messageSourceService.getMessageByKey("please.add.valid.file"));
        }
    }

    private List<ProductResponseDto> getProductResponse(List<Product> products) {
        List<ProductResponseDto> responseDtoList = new ArrayList<>();
        for (Product product : products) {
            ProductResponseDto responseDto = ProductHelper.buildProductResponse(product);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    public boolean checkApprovedProduct(UUID publicId) {
        final var product = productInternalService.findByPublicId(publicId);

        return product != null;
    }

    private String readCellData(Cell cell) {
        String data = null;
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType().equals(CellType.STRING))
            return data = cell.getStringCellValue();
        else
            return data = String.valueOf(cell.getNumericCellValue());
    }

    private Double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    private Long ParseLong(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Long.parseLong(strNumber);
            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    private BigInteger ParseBigInt(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return new BigInteger(strNumber);
            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    private BigDecimal ParseBigDec(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return new BigDecimal(strNumber);
            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    @Override
    public ByteArrayResource download(Integer page, Integer size, String searchParam, String fromDate, String toDate,
            List<UUID> categoryPublicIds, List<UUID> brandPublicIds, List<UUID> manufacturerPublicIds,
            List<UUID> warrantyTypePublicIds, List<UUID> measuringUnitPublicIds) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;
        if (categoryPublicIds == null)
            categoryPublicIds = List.of();
        if (brandPublicIds == null)
            brandPublicIds = List.of();
        if (manufacturerPublicIds == null)
            manufacturerPublicIds = List.of();
        if (warrantyTypePublicIds == null)
            warrantyTypePublicIds = List.of();
        if (measuringUnitPublicIds == null)
            measuringUnitPublicIds = List.of();

        LocalDateTime startDate = fromDate == null || fromDate.isEmpty() ? LocalDateTime.now().minusYears(100)
                : LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime endDate = toDate == null || toDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(toDate).atTime(LocalTime.MAX);

        Page<Product> listOfProducts = productInternalService.searchProducts(searchParam, startDate, endDate,
                PageRequest.of(page, size),
                categoryPublicIds, brandPublicIds, manufacturerPublicIds, warrantyTypePublicIds,
                measuringUnitPublicIds);

        var content = listOfProducts.getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Brand");
            headerRow.createCell(3).setCellValue("Manufacturer");
            headerRow.createCell(4).setCellValue("Category");
            headerRow.createCell(5).setCellValue("Warranty");
            headerRow.createCell(6).setCellValue("Measuring unit");
            headerRow.createCell(7).setCellValue("Description");
            headerRow.createCell(8).setCellValue("Created Date");
            headerRow.createCell(9).setCellValue("Created By");
            headerRow.createCell(10).setCellValue("Last Modified By");
            headerRow.createCell(11).setCellValue("Vated");
            headerRow.createCell(12).setCellValue("Min Vat");
            headerRow.createCell(13).setCellValue("Max Vat");

            int rowNum = 1;
            for (Product data : content) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getId().toString());
                row.createCell(1).setCellValue(data.getProductName());
                row.createCell(2).setCellValue(data.getBrand().getId().toString());
                row.createCell(3).setCellValue(data.getManufacturer().getId().toString());
                row.createCell(4).setCellValue(data.getProductCategory().getId().toString());
                row.createCell(5).setCellValue(
                        !Objects.isNull(data.getWarrantyType()) ? data.getWarrantyType().getId().toString() : "");
                row.createCell(6).setCellValue(data.getMeasurementUnit().getId().toString());
                row.createCell(7).setCellValue(data.getProductDescription());
                row.createCell(8).setCellValue(data.getCreatedDate());
                row.createCell(9).setCellValue(data.getCreatedBy());
                row.createCell(10).setCellValue(data.getLastModifiedBy());
                row.createCell(11).setCellValue(Objects.isNull(data.getVated()) ? "false" : data.getVated().toString());
                row.createCell(12)
                        .setCellValue(Objects.isNull(data.getMinVat()) ? "0.00" : data.getMinVat().toString());
                row.createCell(13)
                        .setCellValue(Objects.isNull(data.getMaxVat()) ? "0.00" : data.getMaxVat().toString());
            }
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            log.error("Error occurred {} ", e);
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("bulk.download.of.product.failed"));
        }
    }

    @Override
    public AppResponse<Page<ProductResponse>> filterProduct(ProductFilter productFilter, Pageable pageable) {
        QueryBuilder<Product, ProductResponse> queryBuilder = QueryBuilder.build(Product.class, ProductResponse.class);
        productFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<ProductResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }

}