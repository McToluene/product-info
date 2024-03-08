
package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.UpdateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.ApproveRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.RejectVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.*;
import com.mctoluene.productinformationmanagement.domain.response.*;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.*;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantAwaitingApprovalResponse;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantsAwaitingApprovalResponseDto;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.exception.*;
import com.mctoluene.productinformationmanagement.filter.search.ProductVariantFilter;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.filter.search.VariantAwaitingApprovalFilter;
import com.mctoluene.productinformationmanagement.helper.*;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.VariantService;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.commons.exceptions.NotFoundException;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VariantServiceImpl implements VariantService {

    private final VariantInternalService variantInternalService;

    private final VariantAwaitingApprovalService variantAwaitingApprovalService;

    private final VariantTypeInternalService variantTypeInternalService;

    private final ProductCategoryInternalServiceImpl productCategoryInternalService;

    private final ProductInternalService productInternalService;

    private final MessageSourceService messageSourceService;

    private final ImageCatalogInternalService imageCatalogInternalService;

    private final ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    private final AlgoliaClientInternalService algoliaClientInternalService;

    private final VariantVersionInternalService variantVersionInternalService;

    private final ProductVariantInternalService productVariantInternalService;

    private final VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    private final ApplicationEventPublisher eventPublisher;

    private final StockOneProductInternalService stockOneProductInternalService;

    private final InventoryClientInternalService inventoryClientInternalService;
    private final LocationClientInternalService locationClientInternalService;

    @Override
    public AppResponse findVariantBySku(String sku) {
        return Optional.ofNullable(variantVersionInternalService.findBySku(sku))
                .map(o -> new AppResponse(HttpStatus.OK.value(),
                        messageSourceService.getMessageByKey("variant.fetched.successfully"),
                        messageSourceService.getMessageByKey("variant.fetched.successfully"),
                        VariantHelper.buildVariant(o), null))
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    @Override
    @Transactional
    public AppResponse updateVariant(UUID publicId, UpdateVariantRequestDto requestDto) {
        var responseEntity = locationClientInternalService.getCountryByPublicId(requestDto.getCountryPublicId());

        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        final var prevVariant = variantInternalService.findProductVariantByPublicId(publicId);

        return prevVariant.map(productVariant -> {

            if (productVariant.getStatus().equalsIgnoreCase(Status.INACTIVE.name())) {
                throw new UnProcessableEntityException(messageSourceService.getMessageByKey("variant.not.active"));
            }

            final var variantType = variantTypeInternalService
                    .findVariantTypeByPublicId(requestDto.getVariantTypePublicId());

            var variantAwaitingApproval = VariantAwaitingApprovalHelper.buildVariantAwaitingApproval(requestDto,
                    variantType, productVariant);
            VariantVersion mostRecentProductVariantVersion = variantVersionInternalService
                    .findMostRecentVariantVersion(variantAwaitingApproval.getProductVariant().getId());
            variantAwaitingApproval.setSku(mostRecentProductVariantVersion.getSku());
            validateAndSaveVatValue(productVariant.getProduct(), variantAwaitingApproval, requestDto);
            variantAwaitingApproval = variantAwaitingApprovalService
                    .saveVariantAwaitingApprovalToDb(variantAwaitingApproval);

            List<UpdateImageCatalogRequestDto> listImageCatalogRequestDto = requestDto.getImageCatalogs();
            List<ImageCatalog> imageCatalogs = new ArrayList<>();
            if (!CollectionUtils.isEmpty(listImageCatalogRequestDto)) {

                for (UpdateImageCatalogRequestDto updateImageCatalogRequestDto : listImageCatalogRequestDto) {
                    ImageCatalog imageCatalog = null;

                    if (!ObjectUtils.isEmpty(updateImageCatalogRequestDto.getImageCatalogPublicId())) {

                        imageCatalog = imageCatalogInternalService.findByPublicIdAndProductVariantId(
                                updateImageCatalogRequestDto.getImageCatalogPublicId(), productVariant.getId());

                        if (!imageCatalog.getImageUrl().equalsIgnoreCase(updateImageCatalogRequestDto.getImageUrl())) {
                            throw new ValidatorException(updateImageCatalogRequestDto.getImageUrl() + " "
                                    + messageSourceService.getMessageByKey("image.catalog.invalid.url"));
                        }
                        continue;
                    } else {
                        validateImageCatalog(updateImageCatalogRequestDto, productVariant.getId());
                        imageCatalog = ImageCatalogHelper.buildImageCatalog(updateImageCatalogRequestDto,
                                productVariant, Status.ACTIVE.name());
                    }

                    imageCatalog.setVariantAwaitingApproval(variantAwaitingApproval);
                    imageCatalogs.add(imageCatalog);
                }
            }

            if (!CollectionUtils.isEmpty(imageCatalogs))
                imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogs);

            VariantsAwaitingApprovalResponseDto variantAwaitingApprovalResponseDto = VariantHelper
                    .buildVariantAwaitingApprovalResponse(variantAwaitingApproval);

            return new AppResponse(HttpStatus.OK.value(),
                    messageSourceService.getMessageByKey("variant.updated.successfully"),
                    messageSourceService.getMessageByKey("variant.updated.successfully"),
                    variantAwaitingApprovalResponseDto, null);
        }).orElseThrow(() -> new ModelNotFoundException(messageSourceService.getMessageByKey("variant.not.found")));
    }

    private void validateAndSaveVatValue(Product product, VariantAwaitingApproval variantAwaitingApproval,
            UpdateVariantRequestDto requestDto) {
        if (product.getVated() &&
                product.getMinVat().compareTo(BigDecimal.ZERO) > 0 &&
                product.getMaxVat().compareTo(BigDecimal.ZERO) > 0) {

            if (requestDto.getVatValue().compareTo(product.getMinVat()) > 0
                    && requestDto.getVatValue().compareTo(product.getMaxVat()) < 0)

                variantAwaitingApproval.setVatValue(requestDto.getVatValue());

        }
        variantAwaitingApproval.setVatValue(BigDecimal.ZERO);

    }

    private ImageCatalog updateImageCatelog(ImageCatalog imageCatalog, UpdateImageCatalogRequestDto requestDto) {
        if (StringUtils.isNotBlank(requestDto.getImageName()))
            imageCatalog.setImageName(requestDto.getImageName().trim());

        if (StringUtils.isNotBlank(requestDto.getImageUrl()))
            imageCatalog.setImageUrl(requestDto.getImageUrl());

        if (StringUtils.isNotBlank(requestDto.getImageDescription()))
            imageCatalog.setImageDescription(requestDto.getImageDescription());

        if (StringUtils.isNotBlank(requestDto.getModifiedBy()))
            imageCatalog.setLastModifiedBy(requestDto.getModifiedBy());

        imageCatalog.setLastModifiedDate(LocalDateTime.now());
        return imageCatalog;
    }

    public void validateImageCatalog(UpdateImageCatalogRequestDto requestDto, UUID productId) {

        if (!imageCatalogInternalService.checkIfNameExist(requestDto.getImageName().trim()))
            throw new ValidatorException(requestDto.getImageName() + " "
                    + messageSourceService.getMessageByKey("image.catalog.name.already.exist"));

        if (imageCatalogInternalService.checkForImageProductDuplicateEntry(productId, requestDto.getImageUrl()))
            throw new ValidatorException(
                    requestDto.getImageName() + " " + messageSourceService.getMessageByKey("image.already.assigned"));
    }

    @Override
    public AppResponse<Page<ProductVariantDetailResponseDto>> getAllVariants(String searchParam, String countryCode,
            String startDate, String endDate,
            List<String> listOfStatus, Integer page, Integer size,
            Boolean isVated) {

        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        var responseEntity = locationClientInternalService.findCountryByCode(countryCode);
        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        if (ObjectUtils.isEmpty(listOfStatus)) {
            listOfStatus = new ArrayList<>();
            listOfStatus.add(Status.ACTIVE.name());
            listOfStatus.add(Status.INACTIVE.name());
        } else {
            listOfStatus.forEach(status -> {
                if (Status.getStatus(status).isEmpty() || status.equals(Status.DELETED.name()))
                    throw new UnProcessableEntityException(
                            messageSourceService.getMessageByKey("invalid.status"));
            });
        }

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);

        log.info("size is {}, page is {}", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<ProductVariantDetailResponseDto> variantResponseDtos = productVariantInternalService
                .findAllProductVariantsPageable(searchParam, fromDate, toDate, ApprovalStatus.APPROVED.name(),
                        listOfStatus, isVated, responseEntity.getBody().getData().publicId(), pageRequest)
                .map(productVariant -> {
                    List<String> imageCatalogs = imageCatalogInternalService
                            .findByProductVariantId(productVariant.getId())
                            .stream()
                            .filter(image -> image.getProductVariant().getId().equals(productVariant.getId()))
                            .map(ImageCatalog::getImageUrl)
                            .toList();
                    ProductCategoryWithSubcategoryResponse parentCategory = productCategoryInternalService
                            .getParentCategoryAndSubCategories(productVariant.getProduct().getProductCategory());

                    return VariantHelper.buildVariantWithProductResponse(productVariant, imageCatalogs, parentCategory);
                });

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse getVariantByPublicId(UUID publicId) {
        ProductVariant productVariant = variantInternalService
                .findByPublicIdAndStatusNot(publicId, Status.DELETED.name())
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("variant.not.found")));

        Optional<VariantVersion> optionalVariantVersion = variantInternalService
                .findVariantByProductVariant(productVariant);
        if (optionalVariantVersion.isEmpty())
            throw new ModelNotFoundException(
                    messageSourceService.getMessageByKey("variant.not.found"));

        VariantProductResponseDto responseDto = VariantHelper.buildVariantWithProduct(optionalVariantVersion.get());
        responseDto.setVated(Objects.isNull(productVariant.getIsVated()) ? false : productVariant.getIsVated());
        responseDto.setVatValue(productVariant.getVatValue());

        List<ImageCatalog> imageCatalogs = imageCatalogInternalService.findByProductVariantId(productVariant.getId());
        responseDto.setImageCatalogs(ImageHelper.buildCatImageResponse(imageCatalogs));

        ProductCategoryWithSubcategoryResponse parentCategory = productCategoryInternalService
                .getParentCategoryAndSubCategories(
                        optionalVariantVersion.get().getProduct().getProductCategory());

        responseDto.setParentCategory(parentCategory);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getVariantsByProductCategoryPublicId(UUID publicId, Integer page, Integer size) {

        if (!VariantHelper.validatePage(page, size))
            throw new PageableException(messageSourceService.getMessageByKey("page.size.error"));

        Pageable pageable = PageRequest.of(page - 1, size);

        ProductCategory productCategory = productCategoryInternalService.findProductCategoryByPublicId(publicId);

        Page<VariantResponseDto> variants = variantVersionInternalService
                .findVariantsByProductCategoryId(productCategory.getId(), pageable).map(VariantHelper::buildVariant);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variants, null);
    }

    @Override
    public AppResponse getVariantsBySkuList(List<String> skuList) {
        List<VariantVersion> variantVersions = variantVersionInternalService.findAllBySkuIn(skuList);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersions.stream().map(VariantHelper::buildCompleteVariantResponse), null);
    }

    @Override
    public AppResponse searchVariants(String searchValue, Integer page, Integer size) {

        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 0 : size;
        Page<VariantVersion> variants = variantInternalService.searchVariants(searchValue,
                (PageRequest.of(page, size)));
        List<VariantCompleteResponseDto> variantResponseDtos = new ArrayList<>();
        for (VariantVersion variant : variants) {
            VariantCompleteResponseDto completeResponseDto = VariantHelper.buildCompleteVariantResponse(variant);
            variantResponseDtos.add(completeResponseDto);
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse getProductVariantsByPublicIds(List<UUID> variantPublicIdList) {
        List<ProductVariant> productVariant = variantInternalService
                .findProductVariantsByPublicIds(variantPublicIdList);
        List<VariantCompleteResponseDto> variantResponseDtos = new ArrayList<>();

        for (ProductVariant variant : productVariant) {
            VariantVersion productDetails = getProductDetails(variant);
            if (productDetails != null) {
                VariantCompleteResponseDto completeResponseDto = VariantHelper
                        .buildCompleteVariantResponse(productDetails);
                variantResponseDtos.add(completeResponseDto);
            }
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse searchProductVariantsByPublicIds(String searchParam, List<UUID> variantPublicIdList) {
        if (searchParam == null)
            searchParam = "";
        if (searchParam.isBlank())
            searchParam = "";

        List<ProductVariant> productVariant = variantInternalService
                .findProductVariantsByPublicIds(variantPublicIdList);
        List<VariantCompleteResponseDto> variantResponseDtos = new ArrayList<>();

        List<VariantVersion> variantVersions = variantVersionInternalService.searchVariantVersionByProductVariantsIn(
                searchParam, productVariant.stream().map(BaseEntity::getId).toList());

        for (VariantVersion version : variantVersions) {
            VariantCompleteResponseDto completeResponseDto = VariantHelper.buildCompleteVariantResponse(version);
            variantResponseDtos.add(completeResponseDto);
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse getVariantIdsBySkuList(List<String> skuList) {
        List<VariantVersion> variants = variantVersionInternalService.findAllBySkuIn(skuList);

        List<String> validVariantsIds = variants.stream()
                .map(variant -> variant.getProductVariant().getPublicId().toString()).toList();

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                validVariantsIds, null);
    }

    @Override
    public AppResponse getVariantsByProductPublicId(UUID productPublicId) {

        Product product = productInternalService.findByPublicId(productPublicId);

        if (Objects.isNull(product))
            throw new ModelNotFoundException(
                    messageSourceService.getMessageByKey("product.not.found"));

        List<VariantVersion> variantVersions = variantInternalService
                .findAllByStatusAndProductVariantIn(product.getId());

        List<VariantResponseDto> variantResponseDtos = variantVersions.stream().map(variantVersion -> {
            VariantResponseDto responseDto = VariantHelper.buildVariant(variantVersion);
            List<ImageCatalog> imageCatalogs = imageCatalogInternalService
                    .findByProductVariantId(variantVersion.getProductVariant().getId());
            responseDto.setImageCatalogs(ImageHelper.buildCatImageResponse(imageCatalogs));
            return responseDto;
        }).toList();

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse getVariantBySku(String sku) {
        final var variantVersion = variantVersionInternalService.findBySku(sku);
        VariantCompleteResponseDto completeResponseDto = VariantHelper.buildCompleteVariantResponse(variantVersion);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                completeResponseDto, null);

    }

    @Override
    public AppResponse approveVariantAwaitingApproval(UUID publicId, ApproveRequestDto requestDto) {
        log.info("about to approve variant awaiting approval:: {}", publicId);
        VariantAwaitingApproval variantAwaitingApproval = variantAwaitingApprovalInternalService
                .findByPublicId(publicId);
        if (variantAwaitingApproval.getApprovalStatus().equals(ApprovalStatus.APPROVED.name()))
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.already.approved"));

        // TODO momentany fix issue
        var measurementUnit = MeasuringUnit.builder()
                .abbreviation("KG")
                .build();
        if (null == variantAwaitingApproval.getProduct().getMeasurementUnit().getAbbreviation())
            variantAwaitingApproval.getProduct().setMeasurementUnit(measurementUnit);
        // end TODO

        ProductRequestDto productRequestDto = StockOneHelper.buildProductRequestDto("Test_WH_Loft_Africa",
                BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, variantAwaitingApproval.getVariantName(),
                "FG", variantAwaitingApproval.getSku(),
                variantAwaitingApproval.getDefaultImageUrl(),
                variantAwaitingApproval.getProduct().getMeasurementUnit().getAbbreviation(),
                variantAwaitingApproval.getProduct().getProductCategory().getProductCategoryName(),
                variantAwaitingApproval.getProduct().getProductName(), variantAwaitingApproval.getCostPrice());

        log.info("productVariant from VAA{}", variantAwaitingApproval.getProductVariant());

        if (variantAwaitingApproval.getProductVariant() != null) {

            List<ImageCatalog> imagesForVariantAwaitingApproval = imageCatalogInternalService
                    .findByProductVariantId(variantAwaitingApproval.getProductVariant().getId());

            if (imagesForVariantAwaitingApproval.isEmpty())
                throw new ValidatorException(messageSourceService.getMessageByKey("variant.no.image"));

            updateExistingVariant(variantAwaitingApproval);

        } else {

            List<ImageCatalog> imagesForVariantAwaitingApproval = imageCatalogInternalService
                    .findByVariantAwaitingApprovalId(variantAwaitingApproval.getId());

            if (imagesForVariantAwaitingApproval.isEmpty())
                throw new ValidatorException(messageSourceService.getMessageByKey("variant.no.image"));

            updateNewVariant(requestDto, variantAwaitingApproval);

        }
        variantAwaitingApproval.getProductVariant().setIsVated(variantAwaitingApproval.getIsVated());
        eventPublisher.publishEvent(
                new ProductVariantApprovedEvent(variantAwaitingApproval.getProductVariant(), productRequestDto));

        VariantsAwaitingApprovalResponseDto variantsAwaitingApprovalResponseDto = VariantHelper
                .buildVariantAwaitingApproval(variantAwaitingApproval);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.successfully.approved"),
                messageSourceService.getMessageByKey("variant.successfully.approved"),
                variantsAwaitingApprovalResponseDto, null);
    }

    private void updateNewVariant(ApproveRequestDto requestDto, VariantAwaitingApproval variantAwaitingApproval) {
        ProductVariant productVariant = VariantHelper.buildProductVariant(variantAwaitingApproval);
        VariantVersion variantVersion = VariantHelper.buildVariantVersion(variantAwaitingApproval);
        variantVersion.setApprovedBy(requestDto.getApprovedBy());
        UUID productId = UUID.randomUUID();
        productVariant.setProduct(variantAwaitingApproval.getProduct());
        productVariant.setId(productId);
        productVariant.setOriginalPublicId(productId);
        productVariant.setIsVated(variantAwaitingApproval.getIsVated());

        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.APPROVED.name());
        variantAwaitingApproval.setCompletedDate(LocalDateTime.now());
        variantAwaitingApproval.setProductVariant(productVariant);
        variantAwaitingApproval.setCompletedBy(requestDto.getApprovedBy());
        variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(variantAwaitingApproval);

        List<ProductVariant> approvedVariants;
        variantVersion.getProduct().setPublicId(variantAwaitingApproval.getProduct().getPublicId());
        variantVersion.setProductVariant(productVariant);
        variantVersionInternalService.saveVariantVersionToDb(variantVersion);

        imageCatalogInternalService.setProductVariantImageCatalogForVariantAwaitingApproval(
                variantAwaitingApproval.getId(), productVariant.getId());
    }

    private void updateExistingVariant(VariantAwaitingApproval variantAwaitingApproval) {
        VariantVersion mostRecentProductVariantVersion = variantVersionInternalService
                .findMostRecentVariantVersion(variantAwaitingApproval.getProductVariant().getId());
        log.info("latest variant version {}", mostRecentProductVariantVersion);
        VariantVersion newVariantVersion = VariantHelper.buildVariantVersion(variantAwaitingApproval);
        newVariantVersion.setVersion(mostRecentProductVariantVersion.getVersion().add(BigInteger.ONE));
        newVariantVersion.setProductVariant(variantAwaitingApproval.getProductVariant());
        newVariantVersion.setStatus(Status.ACTIVE.name());
        variantAwaitingApprovalInternalService.setApprovalStatusByProductVariantId(
                variantAwaitingApproval.getProductVariant().getId(), ApprovalStatus.APPROVED.name());
        variantVersionInternalService.setVersionToStatusByProductVariantId(
                variantAwaitingApproval.getProductVariant().getId(), Status.INACTIVE.name());
        variantVersionInternalService.saveVariantVersionToDb(newVariantVersion);

        ProductVariant productVariant = productVariantInternalService
                .findByPublicId(mostRecentProductVariantVersion.getProductVariant().getPublicId());
        productVariant.setVariantName(
                WordUtils.capitalizeFully(variantAwaitingApproval.getProductVariant().getVariantName().trim()));
        productVariant.setStatus(Status.ACTIVE.name());
        productVariant.setIsVated(variantAwaitingApproval.getIsVated());
        productVariant.setVatValue(variantAwaitingApproval.getVatValue());
        productVariantInternalService.saveProductVariantToDb(productVariant);
    }

    @Override
    public AppResponse deleteVariant(UUID publicId) {

        ProductVariant productVariant = variantInternalService.findProductVariantByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("variant.not.found")));

        if (productVariant.getStatus().equals(Status.DELETED.name()))
            throw new ModelNotFoundException(
                    messageSourceService.getMessageByKey("variant.not.found"));

        if (!Objects.isNull(variantVersionInternalService.findByProductVariantId(productVariant.getId())))
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("variant.in.use"));

        String objectId = productVariant.getOriginalPublicId().toString();
        var algoliaResponse = algoliaClientInternalService.deleteProductInAlgolia(objectId);

        if (algoliaResponse.getStatusCodeValue() != HttpStatus.OK.value())
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("variant.not.deleted"));

        productVariant.setStatus(Status.DELETED.name());

        var deletedVariant = variantInternalService.deleteProductVariant(productVariant);

        log.info("variant has been deleted {}", deletedVariant.getStatus());

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.deleted.successfully"),
                messageSourceService.getMessageByKey("variant.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse<Page<VariantsAwaitingApprovalResponseDto>> getVariantsAwaitingApproval(String searchParam,
            String countryCode, String startDate,
            String endDate,
            String approvalStatus, List<String> listOfStatus, Integer page, Integer size) {

        var responseEntity = locationClientInternalService.findCountryByCode(countryCode);
        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        if (ObjectUtils.isEmpty(listOfStatus)) {
            listOfStatus = new ArrayList<>();
            listOfStatus.add(Status.ACTIVE.name());
            listOfStatus.add(Status.INACTIVE.name());
        } else {
            listOfStatus.forEach(status -> {
                if (Status.getStatus(status).isEmpty() || status.equals(Status.DELETED.name()))
                    throw new UnProcessableEntityException(
                            messageSourceService.getMessageByKey("invalid.status"));
            });
        }

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);

        log.info("size is {}, page is {}", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<VariantsAwaitingApprovalResponseDto> variantsAwaitingApproval = variantAwaitingApprovalInternalService
                .searchVariantsAwaitingApproval(
                        searchParam, responseEntity.getBody().getData().publicId(), fromDate, toDate, approvalStatus,
                        listOfStatus, pageRequest)
                .map(VariantHelper::buildVariantAwaitingApproval);

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                variantsAwaitingApproval, null);
    }

    @Override
    public AppResponse<Page<RejectedVariantsResponseDto>> getRejectedVariants(String searchParam,
            String countryCode, String startDate, String endDate,
            Integer page, Integer size) {
        var responseEntity = locationClientInternalService.findCountryByCode(countryCode);
        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.dates"));

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);

        List<String> statusList = new ArrayList<>();
        statusList.add("ACTIVE");
        statusList.add("INACTIVE");

        Page<RejectedVariantsResponseDto> rejectedVariants = variantAwaitingApprovalInternalService
                .searchVariantsAwaitingApproval(
                        searchParam, responseEntity.getBody().getData().publicId(), fromDate, toDate, "REJECTED",
                        statusList,
                        pageRequest)
                .map(VariantHelper::buildRejectedVariantsResponseDto);

        return new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("rejected.variant.fetched.successfully"),
                messageSourceService.getMessageByKey("rejected.variant.fetched.successfully"),
                rejectedVariants, null);
    }

    @Override
    public AppResponse getVariantsWithMissingImages(String searchParam, String from, String to, Integer page,
            Integer size) {
        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;

        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime fromDate;
        LocalDateTime toDate;

        try {
            toDate = to == null || to.isEmpty() ? LocalDateTime.now() : LocalDate.parse(to).atTime(LocalTime.MAX);
            fromDate = from == null || from.isEmpty() ? LocalDateTime.now().minusYears(50)
                    : LocalDate.parse(from).atStartOfDay();
        } catch (Exception ex) {
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.dates"));
        }

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.dates"));

        Page<VariantsAwaitingApprovalResponseDto> variants = variantAwaitingApprovalInternalService
                .getVariantsWithMissingImages(searchParam, fromDate, toDate, pageable)
                .map(VariantHelper::buildVariantAwaitingApproval);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                variants, null);
    }

    @Override
    public AppResponse rejectVariantAwaitingApproval(UUID publicId, RejectVariantRequestDto requestDto) {
        VariantAwaitingApproval variantAwaitingApproval = variantAwaitingApprovalInternalService
                .findByVariantAwaitingApprovalByPublicId(publicId);
        if (variantAwaitingApproval.getApprovalStatus().equals(ApprovalStatus.APPROVED.name()))
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.already.approved"));
        if (variantAwaitingApproval.getApprovalStatus().equals(ApprovalStatus.REJECTED.name()))
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.already.rejected"));
        variantAwaitingApproval.setRejectedReason(requestDto.getRejectionReason());
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.REJECTED.name());
        variantAwaitingApproval.setCompletedDate(LocalDateTime.now());
        variantAwaitingApproval.setCompletedBy(requestDto.getRejectedBy());
        variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(variantAwaitingApproval);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                null, null);
    }

    @Override
    public AppResponse editVariantAwaitingApproval(UUID variantAwaitingApprovalPublicId,
            EditVariantAwaitingApprovalRequestDto requestDto) {

        var variantAwaitingApproval = variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(
                variantAwaitingApprovalPublicId);

        var updatedVariantAwaitingApproval = updateVariantAwaitingApproval(variantAwaitingApproval, requestDto);

        var variantAwaitingApprovalResponse = VariantHelper
                .buildVariantAwaitingApprovalResponse(updatedVariantAwaitingApproval);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                variantAwaitingApprovalResponse, null);
    }

    private VariantAwaitingApproval updateVariantAwaitingApproval(VariantAwaitingApproval variantAwaitingApproval,
            EditVariantAwaitingApprovalRequestDto requestDto) {

        if (!Objects.isNull(requestDto.getVariantTypeId()))
            variantAwaitingApproval.setVariantType(
                    variantTypeInternalService.findVariantTypeById(requestDto.getVariantTypeId()));

        if (StringUtils.isNotBlank(requestDto.getVariantName()))
            variantAwaitingApproval.setVariantName(requestDto.getVariantName());

        if (StringUtils.isNotBlank(requestDto.getVariantDescription()))
            variantAwaitingApproval.setVariantDescription(requestDto.getVariantDescription());

        if (StringUtils.isNotBlank(requestDto.getDefaultImageUrl()))
            variantAwaitingApproval.setDefaultImageUrl(requestDto.getDefaultImageUrl());

        if (!Objects.isNull(requestDto.getCostPrice()))
            variantAwaitingApproval.setCostPrice(requestDto.getCostPrice());

        if (StringUtils.isNotBlank(requestDto.getLastModifiedBy()))
            variantAwaitingApproval.setLastModifiedBy(requestDto.getLastModifiedBy());

        variantAwaitingApproval.setLastModifiedDate(LocalDateTime.now());

        variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(variantAwaitingApproval);

        return variantAwaitingApproval;
    }

    private VariantVersion getProductDetails(ProductVariant variant) {
        return variantVersionInternalService.findByProductVariantIdAndStatus(variant.getId(), Status.ACTIVE.name());
    }

    @Override
    public AppResponse getApprovedAndUnApprovedVariantIdsBySkuList(List<String> variantSkuList) {
        List<VariantVersion> variantVersions = variantVersionInternalService.findAllBySkuIn(variantSkuList);
        List<String> validVariantsIds = null;
        List<String> validVariantsSku = new ArrayList<>();
        Map<String, String> validSkuMap = new HashMap<>();

        if (!variantVersions.isEmpty()) {
            validVariantsIds = variantVersions.stream()
                    .map(variant -> variant.getProductVariant().getPublicId().toString()).toList();
            validVariantsSku = variantVersions.stream().map(variant -> variant.getSku().toString()).toList();
            variantVersions.stream().forEach(variant -> validSkuMap
                    .put(variant.getProductVariant().getPublicId().toString(), variant.getSku().toString()));
        }
        List<String> nonExistingSku = variantSkuList;
        nonExistingSku.removeAll(validVariantsSku);

        ApprovedUnApprovedVariantIdsDto approvedUnApprovedVariantIdsDto = new ApprovedUnApprovedVariantIdsDto();
        approvedUnApprovedVariantIdsDto.setValidVariantsIds(validVariantsIds);
        approvedUnApprovedVariantIdsDto.setValidSkuMap(validSkuMap);
        approvedUnApprovedVariantIdsDto.setNonExistingSku(nonExistingSku);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                approvedUnApprovedVariantIdsDto, null);
    }

    @Override
    public AppResponse archiveProductVariant(UUID productVariantPublicId) {
        var productVariant = productVariantInternalService.findByPublicId(productVariantPublicId);
        if (productVariant.getStatus().equals(Status.INACTIVE.name())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("product.variant.already.archived"));
        }
        productVariant.setStatus(Status.INACTIVE.name());
        productVariantInternalService.saveProductVariantToDb(productVariant);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                null, null);
    }

    @Override
    public AppResponse unarchiveProductVariant(UUID productVariantPublicId) {
        var productVariant = productVariantInternalService.findByPublicId(productVariantPublicId);
        if (productVariant.getStatus().equals(Status.ACTIVE.name())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("product.variant.already.unarchived"));
        }
        productVariant.setStatus(Status.ACTIVE.name());
        productVariantInternalService.saveProductVariantToDb(productVariant);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                null, null);
    }

    @Override
    public AppResponse getAllVariantsByCategoryPublicIds(String searchParam, List<UUID> categoryPublicIds,
            String startDate, String endDate,
            List<String> listOfStatus, Integer page, Integer size) throws JsonProcessingException {

        List<ProductCategory> productCategories = productCategoryInternalService
                .findProductCategoryByPublicIds(categoryPublicIds);

        LocalDateTime toDate = endDate == null || endDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(endDate).atTime(LocalTime.MAX);
        LocalDateTime fromDate = startDate == null || startDate.isEmpty() ? LocalDateTime.now().minusYears(40)
                : LocalDate.parse(startDate).atStartOfDay();

        if (fromDate.isAfter(toDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        if (ObjectUtils.isEmpty(listOfStatus)) {
            listOfStatus = new ArrayList<>();
            listOfStatus.add(Status.ACTIVE.name());
            listOfStatus.add(Status.INACTIVE.name());
        } else {
            listOfStatus.forEach(status -> {
                if (Status.getStatus(status).isEmpty() || status.equals(Status.DELETED.name()))
                    throw new UnProcessableEntityException(
                            messageSourceService.getMessageByKey("invalid.status"));
            });
        }

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);

        log.info("size is {}, page is {}", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<VariantWithMarkupResponseDto> productVariants = variantInternalService
                .findAllVariantsByCategoryPublicIds(searchParam, productCategories,
                        fromDate, toDate, ApprovalStatus.APPROVED.name(), listOfStatus, pageRequest)
                .map(VariantHelper::buildVariantList);

        List<String> listOfSku = productVariants.stream().map(e -> e.getSku()).toList();

        ObjectMapper objectMapper = new ObjectMapper();

        ResponseEntity<AppResponse> priceModelResponse = shoppingExperienceClientInternalService
                .getPriceModelBySkuList(listOfSku);

        AppResponse appResponse = priceModelResponse.getBody();

        Map<String, BigDecimal> priceModelMap = objectMapper
                .convertValue(appResponse.getData(), new TypeReference<List<PriceModelResponseDto>>() {
                })
                .stream()
                .collect(Collectors.toMap(PriceModelResponseDto::getProductSku, PriceModelResponseDto::getMarkup));

        var finalResponse = productVariants.map(variantResponseDto -> {
            variantResponseDto.setMarkup(priceModelMap.get(variantResponseDto.getSku()));
            return variantResponseDto;
        });

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                finalResponse, null);
    }

    @Override
    public AppResponse getProductVariantsByPublicIdAndStatusAndFilter(VariantFilterRequestDto requestDto) {
        List<String> status = new ArrayList<>();
        if (ObjectUtils.isEmpty(requestDto.getStatus())) {
            status.add(Status.ACTIVE.name());
            status.add(Status.INACTIVE.name());
        } else if (Status.getStatus(requestDto.getStatus()).isEmpty() || status.equals(Status.DELETED.name()))
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("invalid.status"));
        else
            status.add(Status.getStatus(requestDto.getStatus()).get().name());

        List<VariantVersion> variantVersions = variantInternalService
                .findProductVariantsByPublicIdsAndStatusAndFilter(requestDto.getVariantPublicIds(),
                        status,
                        requestDto.getSearchValue(),
                        Objects.requireNonNullElse(requestDto.getCategoryPublicIds(), new ArrayList<>()));

        Set<VariantCompleteResponseDto> variantResponseDtos = new HashSet<>();

        for (VariantVersion variantVersion : variantVersions) {
            if (!Objects.isNull(variantVersion)) {
                VariantCompleteResponseDto completeResponseDto = VariantHelper
                        .buildCompleteVariantResponse(variantVersion);
                variantResponseDtos.add(completeResponseDto);
            }
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantResponseDtos, null);
    }

    @Override
    public AppResponse getAllVariantsByCategoryPublicIdsMap(Map<UUID, BigDecimal> categoryMarkUps) {

        Set<UUID> categoryPublicIds = categoryMarkUps.keySet();
        List<UUID> productCategories = new ArrayList<>();
        Map<UUID, BigDecimal> categoriesMarkups = new HashMap<>();

        for (UUID categoryPublicId : categoryPublicIds) {
            productCategories = productCategoryInternalService.getAllChildrenOfProductCategory(categoryPublicId)
                    .stream()
                    .map(category -> category.getPublicId())
                    .collect(Collectors.toList());

            productCategories.add(categoryPublicId);
            productCategories.stream().map(
                    productCategory -> categoriesMarkups.put(productCategory, categoryMarkUps.get(categoryPublicId)))
                    .collect(Collectors.toList());
        }

        List<VariantMarkupCompleteResponseDto> variantVersionMarkupsList = variantInternalService
                .findByCategoryPublicIds(categoriesMarkups.keySet())
                .stream()
                .map(version -> VariantHelper.buildCompleteVariantMarkupResponse(version, categoriesMarkups))
                .collect(Collectors.toList());

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionMarkupsList, null);
    }

    @Override
    public AppResponse getVariantAwaitingByPublicId(UUID publicId) {
        VariantAwaitingApproval variantAwaitingApproval = variantAwaitingApprovalInternalService
                .findByPublicId(publicId);

        VariantProductResponseDto responseDto = VariantHelper.buildVariantWithProduct(variantAwaitingApproval);

        List<ImageCatalog> imageCatalogs = imageCatalogInternalService
                .findByVariantAwaitingApprovalId(variantAwaitingApproval.getId());
        responseDto.setImageCatalogs(ImageHelper.buildCatImageResponse(imageCatalogs));

        ProductCategoryWithSubcategoryResponse parentCategory = productCategoryInternalService
                .getParentCategoryAndSubCategories(variantAwaitingApproval.getProduct().getProductCategory());
        responseDto.setParentCategory(parentCategory);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse searchVariantBySkuListAndProductName(List<String> skuCodes, String searchValue, Integer page,
            Integer size) {

        if (!VariantHelper.validatePage(page, size))
            throw new PageableException(messageSourceService.getMessageByKey("page.size.error"));

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<VariantVersion> variantVersionPage = variantVersionInternalService
                .searchVariantBySkuListAndProductName(skuCodes, searchValue, pageable);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                new PageImpl<>(variantVersionPage.stream().map(VariantHelper::buildCompleteVariantResponse)
                        .collect(Collectors.toList()), pageable, variantVersionPage.getTotalElements()),
                null);

    }

    @Override
    public AppResponse getProductsFromStockOne(String traceId, String warehouseName, String skuCode, Integer size,
            Integer page) {
        AppResponse stockOneAppResponse = null;
        if (!VariantHelper.validatePage(page, size))
            throw new PageableException(messageSourceService.getMessageByKey("page.size.error"));

        validateWarehouse(traceId, warehouseName);
        try {
            stockOneAppResponse = stockOneProductInternalService.getProducts(traceId, warehouseName, skuCode, size,
                    page);
            log.info("app response from stockOne {}", stockOneAppResponse);
        } catch (Exception ex) {
            log.error("error stockOne service -> {}", ex.getMessage());
            throw new StockOneException(messageSourceService.getMessageByKey("stock.one.failed"));
        }
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("stockOne.products.fetched.successfully"),
                messageSourceService.getMessageByKey("stockOne.products.fetched.successfully"),
                stockOneAppResponse.getData(), null);
    }

    @Transactional
    @Override
    public AppResponse editThresholdAndLeadTime(EditLiveInventoryRequestDto requestDto) {

        VariantVersion variantVersion = variantVersionInternalService.findVariantBySku(requestDto.getSku());

        VariantAwaitingApproval variantAwaitingApproval = VariantHelper.buildVariantAwaitingApproval(variantVersion,
                requestDto);

        variantAwaitingApproval.setLeadTime(requestDto.getLeadTime());

        variantAwaitingApproval.setThreshold(requestDto.getThreshold());

        var variantAwaitingApprovalExist = variantAwaitingApprovalInternalService
                .findBySkuAndApprovalStatus(requestDto.getSku(), ApprovalStatus.PENDING.name());

        if (Objects.nonNull(variantAwaitingApprovalExist)) {
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.awaited.approval"));
        }

        variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(variantAwaitingApproval);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.require.approval"),
                messageSourceService.getMessageByKey("variant.require.approval"),
                VariantHelper.buildVariantAwaitingApprovalResponse(variantAwaitingApproval),
                null);
    }

    @Override
    public AppResponse<Map<String, ProductVariantVatResponseDto>> getVatRatiosByProductVariantPublicIds(
            List<UUID> productVariantPublicIds,
            List<String> productVariantSkus) {
        productVariantPublicIds = productVariantPublicIds == null ? List.of() : productVariantPublicIds;
        productVariantSkus = productVariantSkus == null ? List.of() : productVariantSkus;
        Map<String, ProductVariantVatResponseDto> productVariantVatValueRatioMap = variantInternalService
                .getProductVariantVatValueRatio(productVariantPublicIds, productVariantSkus);

        return new AppResponse<Map<String, ProductVariantVatResponseDto>>(HttpStatus.OK.value(),
                "Variant VAT values returned successfully",
                "Variant VAT values returned successfully",
                productVariantVatValueRatioMap, null);
    }

    @Override
    public ByteArrayResource download(String searchParam, String countryCode, String fromDate, String toDate,
            List<String> listOfStatus, Boolean isVated, Integer page, Integer size) {
        LocalDateTime startDate = fromDate == null || fromDate.isEmpty() ? LocalDateTime.now().minusYears(100)
                : LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime endDate = toDate == null || toDate.isEmpty() ? LocalDateTime.now()
                : LocalDate.parse(toDate).atTime(LocalTime.MAX);

        var responseEntity = locationClientInternalService.findCountryByCode(countryCode);
        if (Objects.requireNonNull(responseEntity.getBody()).getData() == null
                || responseEntity.getStatusCodeValue() != 200)
            throw new ValidatorException(messageSourceService.getMessageByKey("invalid.country.error"));

        if (startDate.isAfter(endDate))
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("invalid.dates"));

        if (ObjectUtils.isEmpty(listOfStatus)) {
            listOfStatus = new ArrayList<>();
            listOfStatus.add(Status.ACTIVE.name());
            listOfStatus.add(Status.INACTIVE.name());
        } else {
            listOfStatus.forEach(status -> {
                if (Status.getStatus(status).isEmpty() || status.equals(Status.DELETED.name()))
                    throw new UnProcessableEntityException(
                            messageSourceService.getMessageByKey("invalid.status"));
            });
        }

        PageRequest pageRequest = PageRequest.of(page <= 0 ? 0 : page - 1, size < 1 ? 10 : size);

        log.info("size is {}, page is {}", pageRequest.getPageSize(), pageRequest.getPageNumber());

        Page<ProductVariant> variants = productVariantInternalService
                .findAllProductVariantsPageable(searchParam, startDate, endDate, ApprovalStatus.APPROVED.name(),
                        listOfStatus, isVated, responseEntity.getBody().getData().publicId(), pageRequest);

        var content = variants.getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Product Variants");

            // Create the header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Sku");
            headerRow.createCell(3).setCellValue("Brand");
            headerRow.createCell(4).setCellValue("Manufacturer");
            headerRow.createCell(5).setCellValue("Category");
            headerRow.createCell(6).setCellValue("Description");
            headerRow.createCell(7).setCellValue("Created Date");
            headerRow.createCell(8).setCellValue("Created By");
            headerRow.createCell(9).setCellValue("Last Modified By");
            headerRow.createCell(10).setCellValue("Vated");
            headerRow.createCell(11).setCellValue("Vat Value");
            headerRow.createCell(12).setCellValue("Weight");

            int rowNum = 1;
            for (ProductVariant data : content) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getPublicId().toString());
                row.createCell(1).setCellValue(data.getVariantName());
                row.createCell(2).setCellValue(data.getSku());
                row.createCell(3).setCellValue(data.getProduct().getBrand().getBrandName());
                row.createCell(4).setCellValue(data.getProduct().getManufacturer().getManufacturerName());
                row.createCell(5).setCellValue(data.getProduct().getProductCategory().getProductCategoryName());
                row.createCell(6).setCellValue(data.getVariantDescription());
                row.createCell(7).setCellValue(data.getCreatedDate());
                row.createCell(8).setCellValue(data.getCreatedBy());
                row.createCell(9).setCellValue(data.getLastModifiedBy());
                row.createCell(10)
                        .setCellValue(Objects.isNull(data.getIsVated()) ? "false" : data.getIsVated().toString());
                row.createCell(11)
                        .setCellValue(Objects.isNull(data.getVatValue()) ? "0.00" : data.getVatValue().toString());
                row.createCell(11).setCellValue(Objects.isNull(data.getWeight()) ? 0 : data.getWeight());
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
    public AppResponse<Map<String, ProductSkuDto>> validateSkus(List<String> skus) {
        List<ProductVariant> foundVariantPublicIds = variantInternalService.findVariantPublicIdBySkus(skus);
        if (foundVariantPublicIds.isEmpty()) {
            throw new NotFoundException(messageSourceService.getMessageByKey("variant.skus.does.not.exist"));
        }
        Map<String, ProductSkuDto> validatedSkuResponse = skus.stream()
                .collect(Collectors.toMap(Function.identity(), sku -> {
                    ProductVariant productVariant = foundVariantPublicIds.stream()
                            .filter(variant -> variant.getSku().equals(sku))
                            .findFirst().orElse(null);

                    UUID publicId = null;
                    boolean exists = false;

                    if (Objects.nonNull(productVariant)) {
                        publicId = productVariant.getPublicId();
                        exists = true;
                    }

                    return new ProductSkuDto(publicId, sku, exists);
                }));
        return new AppResponse<>(HttpStatus.OK.value(), "", "", validatedSkuResponse, null);
    }

    private void validateWarehouse(String traceId, String warehouseName) {
        try {
            inventoryClientInternalService.getWarehouseByName(traceId, warehouseName);
        } catch (Exception ex) {
            log.error("error inventory service -> {}", ex.getMessage());
            throw new NotFoundException(messageSourceService.getMessageByKey("warehouse.not.found"));
        }
    }

    @Override
    public AppResponse<Page<ProductVariantResponse>> filterProductVariant(ProductVariantFilter productVariantFilter,
            Pageable pageable) {
        QueryBuilder<ProductVariant, ProductVariantResponse> queryBuilder = QueryBuilder.build(ProductVariant.class,
                ProductVariantResponse.class);
        productVariantFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<ProductVariantResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }

    @Override
    public AppResponse<Page<VariantAwaitingApprovalResponse>> filterVariantAwaitingApproval(
            VariantAwaitingApprovalFilter filter, Pageable pageable) {
        QueryBuilder<VariantAwaitingApproval, VariantAwaitingApprovalResponse> queryBuilder = QueryBuilder
                .build(VariantAwaitingApproval.class, VariantAwaitingApprovalResponse.class);
        filter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<VariantAwaitingApprovalResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }
}
