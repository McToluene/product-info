package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.UpdateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.ApproveRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.RejectVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditLiveInventoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.UpdateVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.VariantFilterRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.PriceModelResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.VariantCompleteResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductSkuDetail;
import com.mctoluene.productinformationmanagement.exception.*;
import com.mctoluene.productinformationmanagement.helper.VariantHelper;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.VariantService;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private VariantService variantService;

    @Mock
    private ProductCategoryInternalServiceImpl productCategoryInternalService;

    @Mock
    private ProductInternalService productInternalService;

    @InjectMocks
    private VariantServiceImpl variantServiceImpl;

    @Mock
    private VariantInternalService variantInternalService;

    @Mock
    private VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    @Mock
    private VariantVersionInternalService variantVersionInternalService;

    @Mock
    private ImageCatalogInternalService imageCatalogInternalService;

    @Mock
    private ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    @Mock
    private ProductVariantInternalService productVariantInternalService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private StockOneProductInternalService stockOneProductInternalService;

    @Mock
    private InventoryClientInternalService inventoryClientInternalService;

    @Mock
    private VariantTypeInternalService variantTypeInternalService;

    @Mock
    private VariantAwaitingApprovalService variantAwaitingApprovalService;

    @Mock
    private AlgoliaClientInternalService algoliaClientInternalService;

    @Mock
    private LocationClientInternalService locationClientInternalService;

    @Test
    void getVariantByPublicId() {
        ProductVariant productVariant = buildProductVariant();
        VariantVersion variantVersion = buildVariantVersion();

        UUID publicId = UUID.randomUUID();
        given(variantInternalService.findByPublicIdAndStatusNot(publicId, Status.DELETED.name()))
                .willReturn(Optional.of(productVariant));
        given(variantInternalService.findVariantByProductVariant(productVariant))
                .willReturn(Optional.of(variantVersion));
        var createdResponse = variantServiceImpl.getVariantByPublicId(publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void rejectVariantAwaitingApproval() throws Exception {

        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus("PENDING")
                .build();

        RejectVariantRequestDto requestDto = new RejectVariantRequestDto();
        requestDto.setRejectedBy("admin@mail.com");
        requestDto.setRejectionReason("Incorrect data");

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                null, null);
        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(publicId))
                .thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any())).thenReturn(any());

        var rejectedResponse = variantServiceImpl.rejectVariantAwaitingApproval(publicId, requestDto);

        assertThat(rejectedResponse.getData()).isNull();
        assertThat(rejectedResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.successfully.rejected"));
        assertThat(rejectedResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void approveVariantAwaitingApproval() {

        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName("Test_WH_Loft_Africa");
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        ProductSkuDetail productSkuDetail = ProductSkuDetail.builder()
                .breath(BigDecimal.ZERO)
                .height(BigDecimal.ZERO)
                .weight(BigDecimal.ZERO)
                .length(BigDecimal.ZERO)
                .skuName(variantAwaitingApproval.getVariantName())
                .skuType("FG")
                .sku(variantAwaitingApproval.getSku())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .measuringUnit(variantAwaitingApproval.getProductVariant().getProduct().getMeasurementUnit().getName())
                .skuCategoryName(variantAwaitingApproval.getProduct().getProductCategory().getProductCategoryName())
                .skuDescription(variantAwaitingApproval.getProduct().getProductName())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .build();

        productSkuDetails.add(productSkuDetail);
        productRequestDto.setSkuDetails(productSkuDetails);

        VariantVersion variantVersion = buildVariantVersion();

        List<ImageCatalog> imageCatalogList = getImageCatalogList(variantAwaitingApproval.getProductVariant(),
                variantAwaitingApproval);

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);
        when(variantVersionInternalService.saveVariantVersionToDb(any()))
                .thenReturn(VariantVersion.builder().version(BigInteger.ZERO).build());
        when(imageCatalogInternalService.findByProductVariantId(any(UUID.class))).thenReturn(imageCatalogList);
        when(stockOneProductInternalService.createProduct(productRequestDto)).thenReturn(ResponseEntity.ok().build());
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any(UUID.class)))
                .thenReturn(getImageCatalogList(variantAwaitingApproval.getProductVariant(), variantAwaitingApproval));
        when(variantVersionInternalService.findMostRecentVariantVersion(any(UUID.class))).thenReturn(variantVersion);
        when(productVariantInternalService.findByPublicId(any(UUID.class))).thenReturn(buildProductVariant());
        doNothing().when(eventPublisher).publishEvent(any(ProductVariantApprovedEvent.class));
        var approvedResponse = variantServiceImpl.approveVariantAwaitingApproval(publicId, requestDto);

        assertThat(approvedResponse.getData()).isNotNull();
        assertThat(approvedResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.successfully.approved"));
        assertThat(approvedResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void approveVariantAwaitingApprovalAlreadyApprovedTest() {

        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();

        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName("Test_WH_Loft_Africa");
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.approveVariantAwaitingApproval(publicId, requestDto));

    }

    @Test
    void testGetVariantsWithMissingImages() {
        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        Pageable pageable = PageRequest.of(0, 5);

        String name = "";

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page pagedProduct = new PageImpl(productList);

        LocalDateTime fromDate = LocalDate.parse("2023-08-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-08-05").atTime(LocalTime.MAX);

        when(variantAwaitingApprovalInternalService.getVariantsWithMissingImages(name, fromDate, toDate, pageable))
                .thenReturn(pagedProduct);

        AppResponse response = variantServiceImpl.getVariantsWithMissingImages(name, "2023-08-02", "2023-08-05", 1, 5);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("variants.retrieved.success"));
    }

    @Test
    void testArchiveProductVariant() {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                null, null);

        UUID productVariantPublicId = UUID.randomUUID();
        UUID productVariantId = UUID.randomUUID();
        var productVariant = ProductVariant.builder()
                .approvedBy("test")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();
        productVariant.setPublicId(productVariantPublicId);
        productVariant.setId(productVariantId);

        var updatedProductVariant = ProductVariant.builder()
                .approvedBy("test")
                .approvedDate(LocalDateTime.now())
                .status(Status.INACTIVE.name())
                .build();
        updatedProductVariant.setPublicId(productVariantPublicId);
        updatedProductVariant.setId(productVariantId);

        var variantVersion = VariantVersion.builder()
                .status(Status.ACTIVE.name())
                .approvedBy("test")
                .productVariant(productVariant)
                .build();
        var updatedVariantVersion = VariantVersion.builder()
                .status(Status.INACTIVE.name())
                .approvedBy("test")
                .productVariant(productVariant)
                .build();

        when(productVariantInternalService.findByPublicId(productVariantPublicId)).thenReturn(productVariant);
        when(productVariantInternalService.saveProductVariantToDb(updatedProductVariant))
                .thenReturn(updatedProductVariant);
        when(variantVersionInternalService.findByProductVariantId(updatedProductVariant.getId()))
                .thenReturn(variantVersion);
        when(variantVersionInternalService.saveVariantVersionToDb(updatedVariantVersion))
                .thenReturn(updatedVariantVersion);

        var foundResponse = variantServiceImpl.archiveProductVariant(productVariantPublicId);
        assertThat(foundResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("product.variant.archived.successfully"));
        assertThat(foundResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testUnArchiveProductVariant() {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                null, null);

        UUID productVariantPublicId = UUID.randomUUID();
        UUID productVariantId = UUID.randomUUID();
        var productVariant = ProductVariant.builder()
                .approvedBy("test")
                .approvedDate(LocalDateTime.now())
                .status(Status.INACTIVE.name())
                .build();
        productVariant.setPublicId(productVariantPublicId);
        productVariant.setId(productVariantId);

        var updatedProductVariant = ProductVariant.builder()
                .approvedBy("test")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();
        updatedProductVariant.setPublicId(productVariantPublicId);
        updatedProductVariant.setId(productVariantId);

        var variantVersion = VariantVersion.builder()
                .status(Status.INACTIVE.name())
                .approvedBy("test")
                .productVariant(productVariant)
                .build();
        var updatedVariantVersion = VariantVersion.builder()
                .status(Status.ACTIVE.name())
                .approvedBy("test")
                .productVariant(productVariant)
                .build();

        when(productVariantInternalService.findByPublicId(productVariantPublicId)).thenReturn(productVariant);
        when(productVariantInternalService.saveProductVariantToDb(updatedProductVariant))
                .thenReturn(updatedProductVariant);
        when(variantVersionInternalService.findByProductVariantId(updatedProductVariant.getId()))
                .thenReturn(variantVersion);
        when(variantVersionInternalService.saveVariantVersionToDb(updatedVariantVersion))
                .thenReturn(updatedVariantVersion);

        var foundResponse = variantServiceImpl.unarchiveProductVariant(productVariantPublicId);
        assertThat(foundResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"));
        assertThat(foundResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getProductVariantsByPublicIdAndFilter() {
        List<UUID> publicIdList = List.of(UUID.randomUUID());
        Product product = Product.builder()
                .status("ACTIVE")
                .brand(new Brand())
                .productCategory(new ProductCategory())
                .measurementUnit(new MeasuringUnit())
                .build();

        VariantFilterRequestDto variantFilterRequestDto = VariantFilterRequestDto.builder()
                .variantPublicIds(publicIdList)
                .status("ACTIVE")
                .categoryPublicIds(List.of(UUID.randomUUID()))
                .build();

        List<ProductVariant> productVariants = new ArrayList<>();

        ProductVariant productVariant = new ProductVariant();
        productVariant.setApprovedBy("Kunal");
        productVariant.setApprovedDate(LocalDateTime.now());
        productVariant.setStatus(Status.ACTIVE.name());
        productVariant.setProduct(product);
        productVariant.setCreatedBy("abc");

        productVariants.add(productVariant);

        VariantVersion variantVersion = new VariantVersion();
        variantVersion.setProduct(product);
        variantVersion.setVariantType(new VariantType());
        variantVersion.setProductVariant(productVariant);
        variantVersion.setVariantName("var1");

        VariantCompleteResponseDto variantCompleteResponseDto = VariantCompleteResponseDto.builder()
                .costPrice(variantVersion.getCostPrice())
                .version(BigInteger.ONE)
                .costPrice(BigDecimal.ONE)
                .variantDescription("V1")
                .variantTypeId(UUID.randomUUID())
                .brandName("B1")
                .productCategoryName("C1")
                .build();

        when(variantInternalService.findProductVariantsByPublicIdsAndStatusAndFilter(publicIdList,
                List.of(variantFilterRequestDto.getStatus()), variantFilterRequestDto.getSearchValue(),
                variantFilterRequestDto.getCategoryPublicIds())).thenReturn(List.of(variantVersion));
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                List.of(variantCompleteResponseDto), null);

        var foundResponse = variantServiceImpl.getProductVariantsByPublicIdAndStatusAndFilter(variantFilterRequestDto);
        assertThat(foundResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.fetched.successfully"));
        assertThat(foundResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    private WarrantyType getWarrantyType() {
        return WarrantyType.builder()
                .warrantyTypeName("w")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void getAllVariantsByCategoryPublicIds() throws JsonProcessingException {

        var categoryPublicId = UUID.randomUUID();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Hilarious")
                .depth(5)
                .description("no description needed")
                .imageUrl("abc@example.com")
                .status(Status.ACTIVE)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(categoryPublicId);

        Product product = Product.builder()
                .productName("ABD")
                .status(Status.ACTIVE.name())
                .productCategory(productCategory)
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());

        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("Dilip")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .product(product)
                .build();
        productVariant.setId(UUID.randomUUID());
        productVariant.setPublicId(UUID.randomUUID());

        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setVariantName("ABD");
        variantVersion.setSku("SKU12345");
        variantVersion.setApprovalStatus(ApprovalStatus.APPROVED.name());
        variantVersion.setStatus(Status.ACTIVE.name());
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setApprovedBy("Dilip");
        variantVersion.setApprovedDate(LocalDateTime.now());
        variantVersion.setProduct(product);
        variantVersion.setVariantType(VariantType.builder().build());
        variantVersion.setProductVariant(productVariant);

        List<VariantVersion> variantVersions = List.of(variantVersion);

        Page variantVersionPage = new PageImpl(variantVersions);

        LocalDateTime fromDate = LocalDate.parse("2023-04-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-04-25").atTime(LocalTime.MAX);

        List<String> listOfStatus = new ArrayList();
        listOfStatus.add("ACTIVE");

        List<PriceModelResponseDto> priceModelResponseDto = Arrays.asList(PriceModelResponseDto
                .builder()
                .configurationPublicId(UUID.randomUUID())
                .publicId(UUID.randomUUID())
                .productSku("SKU12345")
                .markup(BigDecimal.TEN)
                .build());

        AppResponse priceModelResponse = new AppResponse(HttpStatus.OK.value(),
                "", "", priceModelResponseDto, null);

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionPage, null);

        when(productCategoryInternalService.findProductCategoryByPublicIds(List.of(categoryPublicId)))
                .thenReturn(List.of(productCategory));
        when(shoppingExperienceClientInternalService.getPriceModelBySkuList(List.of(variantVersion.getSku())))
                .thenReturn(ResponseEntity.ok().body(priceModelResponse));
        when(variantInternalService.findAllVariantsByCategoryPublicIds("ABD", List.of(productCategory), fromDate,
                toDate,
                ApprovalStatus.APPROVED.name(), listOfStatus, PageRequest.of(0, 10))).thenReturn(variantVersionPage);
        when(variantService.getAllVariantsByCategoryPublicIds("ABD", List.of(categoryPublicId), "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10)).thenReturn(appResponse);

        AppResponse response = variantServiceImpl.getAllVariantsByCategoryPublicIds("ABD", List.of(categoryPublicId),
                "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getAllVariantsByCategoryPublicId() {

        var categoryPublicId = UUID.randomUUID();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Hilarious")
                .depth(5)
                .description("no description needed")
                .imageUrl("abc@example.com")
                .status(Status.ACTIVE)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(categoryPublicId);

        Product product = Product.builder()
                .productName("ABD")
                .status(Status.ACTIVE.name())
                .productCategory(productCategory)
                .build();
        product.setId(UUID.randomUUID());
        product.setBrand(new Brand());
        product.setPublicId(UUID.randomUUID());

        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("Dilip")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .product(product)
                .build();
        productVariant.setId(UUID.randomUUID());
        productVariant.setPublicId(UUID.randomUUID());

        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setVariantName("ABD");
        variantVersion.setSku("SKU12345");
        variantVersion.setApprovalStatus(ApprovalStatus.APPROVED.name());
        variantVersion.setStatus(Status.ACTIVE.name());
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setApprovedBy("Dilip");
        variantVersion.setApprovedDate(LocalDateTime.now());
        variantVersion.setProduct(product);
        variantVersion.setVariantType(VariantType.builder().build());
        variantVersion.setProductVariant(productVariant);

        List<VariantVersion> variantVersions = List.of(variantVersion);

        Map<UUID, BigDecimal> markUpMap = new HashMap<>();
        markUpMap.put(categoryPublicId, BigDecimal.valueOf(20));

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                List.of(VariantHelper.buildCompleteVariantMarkupResponse(variantVersion, markUpMap)), null);

        when(productCategoryInternalService.getAllChildrenOfProductCategory(categoryPublicId))
                .thenReturn(List.of(productCategory));
        when(variantInternalService.findByCategoryPublicIds(Set.of(categoryPublicId))).thenReturn(variantVersions);
        when(variantService.getAllVariantsByCategoryPublicIdsMap(markUpMap)).thenReturn(appResponse);

        AppResponse response = variantServiceImpl.getAllVariantsByCategoryPublicIdsMap(markUpMap);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getVariantVersionBySkuListAndProductName() {

        Integer page = 1;
        Integer size = 2;

        Product product = Product.builder()
                .status("ACTIVE")
                .productName("test")
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setStatus("ACTIVE");
        variantVersion.setSku("ABD");
        variantVersion.setProduct(product);
        List<VariantVersion> variantVersions = List.of(variantVersion);
        Page variantVersionPage = new PageImpl(variantVersions);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.version.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.version.fetched.successfully"),
                variantVersionPage, null);

        when(variantVersionInternalService.searchVariantBySkuListAndProductName(List.of("ABD"), "test",
                PageRequest.of(page, size))).thenReturn(variantVersionPage);
        when(variantService.searchVariantBySkuListAndProductName(List.of("ABD"), "test",
                page, size)).thenReturn(response);

        AppResponse appResponse = variantService.searchVariantBySkuListAndProductName(List.of("ABD"), "test",
                page, size);

        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.version.fetched.successfully"));
    }

    @Test
    void testGetStockOneProducts() throws Exception {
        String traceId = UUID.randomUUID().toString();

        AppResponse inventoryResponse = new AppResponse(HttpStatus.OK.value(),
                "Warehouse Fetched Successfully",
                "Warehouse Fetched Successfully",
                null, null);

        AppResponse stockOneAppResponse = new AppResponse(HttpStatus.OK.value(),
                "success",
                "success",
                null, null);

        when(inventoryClientInternalService.getWarehouseByName(traceId, "test"))
                .thenReturn(inventoryResponse);
        when(stockOneProductInternalService.getProducts(traceId, "test", "", 1, 1))
                .thenReturn(stockOneAppResponse);

        AppResponse response = variantServiceImpl.getProductsFromStockOne(traceId, "test", "", 1, 1);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("stockOne.products.fetched.successfully"));
    }

    @Test
    void editThresholdAndLeadTime() {

        var categoryPublicId = UUID.randomUUID();
        VariantVersion variantVersion = buildVariantVersion();

        EditLiveInventoryRequestDto requestDto = EditLiveInventoryRequestDto.builder()
                .leadTime(7)
                .threshold(5000)
                .modifiedBy("Dilip")
                .sku("SKU12345")
                .build();

        VariantAwaitingApproval variantAwaitingApproval = VariantHelper.buildVariantAwaitingApproval(variantVersion,
                requestDto);
        variantAwaitingApproval.setId(UUID.randomUUID());
        variantAwaitingApproval.setLeadTime(requestDto.getLeadTime());
        variantAwaitingApproval.setThreshold(requestDto.getThreshold());

        when(variantVersionInternalService.findVariantBySku(requestDto.getSku())).thenReturn(variantVersion);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);

        AppResponse response = variantServiceImpl.editThresholdAndLeadTime(requestDto);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("variant.require.approval"));
        assertThat(response.getError()).isNull();
    }

    @Test
    void findVariantBySkuTest() {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setCreatedBy("creator@mctoluene.am");
        productVariant.setCreatedDate(LocalDateTime.now());

        final String sku = "testsku";
        VariantVersion variant = VariantVersion.builder().variantName("anme")
                .variantType(new VariantType()).variantDescription("desc").costPrice(BigDecimal.ONE)
                .product(product).sku(sku).status("ACTIVE").productVariant(productVariant).build();

        when(variantVersionInternalService.findBySku(sku)).thenReturn(variant);

        var response = variantServiceImpl.findVariantBySku(sku);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void findVariantBySkuDoesNotExistTest() {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setCreatedBy("creator@mctoluene.am");
        productVariant.setCreatedDate(LocalDateTime.now());

        final String sku = "testsku";

        when(variantVersionInternalService.findBySku(sku)).thenReturn(null);

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantServiceImpl.findVariantBySku(sku));
    }

    /*
     * @Test
     * void updateVariantTest() {
     * UUID publicId = UUID.randomUUID();
     * UpdateVariantRequestDto requestDto = buildUpdateVariantRequestDto(publicId);
     * ProductVariant productVariant = buildProductVariant(publicId, Status.ACTIVE);
     * VariantType variantType = buildVariantType();
     * VariantAwaitingApproval variantAwaitingApproval =
     * VariantAwaitingApproval.builder()
     * .variantName("variant")
     * .variantType(variantType)
     * .approvalStatus("ACTIVE")
     * .countryId(publicId)
     * .build();
     * 
     * Product product = new Product();
     * product.setId(UUID.randomUUID());
     * 
     * final String sku = "testsku";
     * VariantVersion variantVersion = VariantVersion.builder().variantName("anme")
     * .variantType(new
     * VariantType()).variantDescription("desc").costPrice(BigDecimal.ONE)
     * .product(product).sku(sku).status("ACTIVE").productVariant(productVariant).
     * build();
     * ImageCatalog imageCatalog = ImageCatalog.builder()
     * .status(Status.ACTIVE.name())
     * .imageDescription("image-002")
     * .imageUrl("http:mctoluene-img-0223.com")
     * .productVariant(new ProductVariant())
     * .imageName("mctoluene-0033")
     * .variantAwaitingApproval(new VariantAwaitingApproval()).build();
     * 
     * when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(
     * Optional.of(productVariant));
     * when(variantTypeInternalService.findVariantTypeByPublicId(any())).thenReturn(
     * variantType);
     * when(variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(any())).
     * thenReturn(variantAwaitingApproval);
     * when(imageCatalogInternalService.saveImageCatalogsToDb(any())).thenReturn(
     * getImageCatalogList());
     * when(variantVersionInternalService.findMostRecentVariantVersion(any())).
     * thenReturn(variantVersion);
     * when(imageCatalogInternalService.findByPublicIdAndProductVariantId(any(),
     * any())).thenReturn(imageCatalog);
     * when(locationClientInternalService.getCountryByPublicId(any())).thenReturn(
     * getCountryMock(publicId));
     * when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(false);
     * 
     * var response = variantServiceImpl.updateVariant(publicId, requestDto);
     * 
     * assertThat(response).isNotNull();
     * assertThat(response.getMessage())
     * .isEqualTo(messageSourceService.getMessageByKey(
     * "variant.updated.successfully"));
     * 
     * }
     */

    @Test
    void updateVariantWhenVariantNotActiveTest() {
        UUID publicId = UUID.randomUUID();
        UpdateVariantRequestDto requestDto = buildUpdateVariantRequestDto(publicId);
        ProductVariant productVariant = buildProductVariant(publicId, Status.INACTIVE);

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(locationClientInternalService.getCountryByPublicId(any())).thenReturn(getCountryMock(publicId));

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantServiceImpl.updateVariant(publicId, requestDto));

    }

    @Test
    void updateVariantForEmptyImageCatalogCaseTest() {
        UUID publicId = UUID.randomUUID();
        UpdateVariantRequestDto requestDto = new UpdateVariantRequestDto();
        requestDto.setVariantTypePublicId(UUID.randomUUID());
        requestDto.setVariantName("Variant");
        requestDto.setCountryPublicId(publicId);
        requestDto.setVariantDescription("Desc");
        requestDto.setCostPrice(BigDecimal.ONE);
        requestDto.setModifiedBy("Mod");
        requestDto.setVatValue(BigDecimal.ZERO);

        ProductVariant productVariant = buildProductVariant(publicId, Status.ACTIVE);
        VariantType variantType = buildVariantType();
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .variantName("variant")
                .variantType(variantType)
                .approvalStatus("ACTIVE")
                .build();

        Product product = new Product();
        product.setId(UUID.randomUUID());

        final String sku = "testsku";
        VariantVersion variantVersion = VariantVersion.builder().variantName("anme")
                .variantType(new VariantType()).variantDescription("desc").costPrice(BigDecimal.ONE)
                .product(product).sku(sku).status("ACTIVE").productVariant(productVariant).build();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(new ProductVariant())
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(new VariantAwaitingApproval()).build();

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantTypeInternalService.findVariantTypeByPublicId(any())).thenReturn(variantType);
        when(variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(any())).thenReturn(variantAwaitingApproval);
        when(imageCatalogInternalService.saveImageCatalogsToDb(any())).thenReturn(getImageCatalogList());
        when(variantVersionInternalService.findMostRecentVariantVersion(any())).thenReturn(variantVersion);
        when(imageCatalogInternalService.findByPublicIdAndProductVariantId(any(), any())).thenReturn(imageCatalog);
        when(locationClientInternalService.getCountryByPublicId(any())).thenReturn(getCountryMock(publicId));

        var response = variantServiceImpl.updateVariant(publicId, requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.updated.successfully"));

    }

    private ResponseEntity<AppResponse<List<CountryDto>>> getCountryListMock(UUID publicId) {
        CountryDto countryDto = CountryDto.builder()
                .countryName("Nigeria")
                .createdBy("test")
                .dialingCode("01")
                .publicId(publicId)
                .threeLetterCode("NGN")
                .twoLetterCode("NG")
                .status(Status.ACTIVE.name())
                .build();
        AppResponse<List<CountryDto>> appResponse = new AppResponse<>(HttpStatus.OK.value(), "", "",
                List.of(countryDto), null);
        return ResponseEntity.ok(appResponse);
    }

    private ResponseEntity<AppResponse<CountryDto>> getCountryMock(UUID publicId) {
        CountryDto countryDto = CountryDto.builder()
                .countryName("Nigeria")
                .createdBy("test")
                .dialingCode("01")
                .publicId(publicId)
                .threeLetterCode("NGN")
                .twoLetterCode("NG")
                .status(Status.ACTIVE.name())
                .build();
        AppResponse<CountryDto> appResponse = new AppResponse<>(HttpStatus.OK.value(), "", "",
                countryDto, null);
        return ResponseEntity.ok(appResponse);
    }

    @Test
    void updateVariantForMissingImageCatalogPublicIdCaseTest() {
        UUID publicId = UUID.randomUUID();
        UpdateVariantRequestDto requestDto = new UpdateVariantRequestDto();
        requestDto.setVariantTypePublicId(UUID.randomUUID());
        requestDto.setVariantName("Variant");
        requestDto.setVariantDescription("Desc");
        requestDto.setCostPrice(BigDecimal.ONE);
        requestDto.setModifiedBy("Mod");
        requestDto.setCountryPublicId(publicId);
        requestDto.setImageCatalogs(List.of(UpdateImageCatalogRequestDto.builder()
                .imageName("test")
                .imageDescription("desc")
                .imageUrl("url")
                .modifiedBy("mod")
                .build()));

        ProductVariant productVariant = buildProductVariant(publicId, Status.ACTIVE);
        VariantType variantType = buildVariantType();
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .variantName("variant")
                .variantType(variantType)
                .approvalStatus("ACTIVE")
                .build();

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setVated(false);

        final String sku = "testsku";
        VariantVersion variantVersion = VariantVersion.builder().variantName("anme")
                .variantType(new VariantType()).variantDescription("desc").costPrice(BigDecimal.ONE)
                .product(product).sku(sku).status("ACTIVE").productVariant(productVariant).build();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(new ProductVariant())
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(new VariantAwaitingApproval()).build();

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantTypeInternalService.findVariantTypeByPublicId(any())).thenReturn(variantType);
        when(variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(any())).thenReturn(variantAwaitingApproval);
        when(imageCatalogInternalService.saveImageCatalogsToDb(any())).thenReturn(getImageCatalogList());
        when(variantVersionInternalService.findMostRecentVariantVersion(any())).thenReturn(variantVersion);
        when(imageCatalogInternalService.findByPublicIdAndProductVariantId(any(), any())).thenReturn(imageCatalog);
        when(locationClientInternalService.getCountryByPublicId(any())).thenReturn(getCountryMock(publicId));
        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(true);

        var response = variantServiceImpl.updateVariant(publicId, requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.updated.successfully"));
    }

    @Test
    void updateVariantPrevVariantIsEmptyTest() {
        UUID publicId = UUID.randomUUID();
        UpdateVariantRequestDto requestDto = buildUpdateVariantRequestDto(publicId);

        when(locationClientInternalService.getCountryByPublicId(any())).thenReturn(getCountryMock(publicId));
        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantServiceImpl.updateVariant(publicId, requestDto));

    }

    @Test
    void validateImageCatalogTest() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(true);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any())).thenReturn(false);

        variantServiceImpl.validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID());
    }

    @Test
    void validateImageCatalogWhenCheckIfNameExistIsTrueTest() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(false);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any())).thenReturn(false);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID()));
    }

    @Test
    void validateImageCatalogWhenDuplicateEntryFoundTest() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(false);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any())).thenReturn(true);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID()));
    }

    @Test
    void validateImageCatalogWithPublicId() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(true);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any(), any())).thenReturn(false);

        variantServiceImpl.validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID());
    }

    @Test
    void validateImageCatalogWithPublicIdNameExists() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(false);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any(), any())).thenReturn(false);

        Assertions.assertThrows(ValidatorException.class, () -> variantServiceImpl
                .validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID()));
    }

    @Test
    void validateImageCatalogWithPublicIdDuplicateEntry() {

        UpdateImageCatalogRequestDto updateImageCatalogRequestDto = buildUpdateImageCatalogRequestDto();

        when(imageCatalogInternalService.checkIfNameExist(any())).thenReturn(true);
        when(imageCatalogInternalService.checkForImageProductDuplicateEntry(any(), any())).thenReturn(true);

        Assertions.assertThrows(ValidatorException.class, () -> variantServiceImpl
                .validateImageCatalog(updateImageCatalogRequestDto, UUID.randomUUID()));
    }

    @Test
    void getAllVariantsTest() {

        List<ProductVariant> productVariants = List.of(buildProductVariant());

        Page<ProductVariant> productVariantPage = new PageImpl<>(productVariants);

        LocalDateTime fromDate = LocalDate.parse("2023-04-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-04-25").atTime(LocalTime.MAX);

        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add("ACTIVE");
        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        when(productVariantInternalService.findAllProductVariantsPageable(any(), any(), any(),
                any(), any(), anyBoolean(), any(), any())).thenReturn(productVariantPage);

        var response = variantServiceImpl.getAllVariants("abd", "NGN", "2023-04-02",
                "2023-04-25", listOfStatus, 1, 10, false);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getAllVariantsFromDateAfterToDate() {

        VariantVersion variantVersion = buildVariantVersion();
        List<VariantVersion> variantVersions = List.of(variantVersion);

        Page<VariantVersion> variantVersionPage = new PageImpl<>(variantVersions);

        LocalDateTime fromDate = LocalDate.parse("2023-05-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-04-25").atTime(LocalTime.MAX);

        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add("ACTIVE");

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionPage, null);
        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        when(variantInternalService.findAllVariantsPageable("ABD", fromDate, toDate,
                ApprovalStatus.APPROVED.name(), listOfStatus, PageRequest.of(0, 10))).thenReturn(variantVersionPage);
        when(variantService.getAllVariants("ABD", "NGN", "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10, null)).thenReturn(appResponse);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.getAllVariants("ABD", "NGN", "2023-05-02", "2023-04-25", listOfStatus, 0, 10,
                        null));

    }

    @Test
    void getAllVariantsListOfStatusEmpty() {

        List<ProductVariant> productVariants = List.of(buildProductVariant());

        Page<ProductVariant> productVariantPage = new PageImpl<>(productVariants);

        List<String> listOfStatus = new ArrayList<>();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                productVariantPage, null);
        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        when(productVariantInternalService.findAllProductVariantsPageable(any(), any(), any(),
                any(), any(), anyBoolean(), any(), any())).thenReturn(productVariantPage);
        when(variantService.getAllVariants("ABD", "NGN", "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10, false)).thenReturn(appResponse);

        AppResponse response = variantServiceImpl.getAllVariants("ABD", "NGN", "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10, false);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getAllVariantsListOfStatusContainsDeleted() {

        VariantVersion variantVersion = buildVariantVersion();
        List<VariantVersion> variantVersions = List.of(variantVersion);

        Page<VariantVersion> variantVersionPage = new PageImpl<>(variantVersions);

        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add(Status.DELETED.name());

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionPage, null);

        when(variantInternalService.findAllVariantsPageable(any(), any(), any(),
                any(), any(), any())).thenReturn(variantVersionPage);
        when(variantService.getAllVariants("ABD", "NGN", "2023-04-02",
                "2023-04-25", listOfStatus, 0, 10, null)).thenReturn(appResponse);
        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantServiceImpl.getAllVariants("ABD", "NGN", "2023-04-02", "2023-04-25", listOfStatus, 0, 10,
                        null));
    }

    @Test
    void getVariantByPublicIdProductVariantNotFound() {
        ProductVariant productVariant = buildProductVariant();
        VariantVersion variantVersion = buildVariantVersion();
        UUID publicId = UUID.randomUUID();
        given(variantInternalService.findByPublicIdAndStatusNot(publicId, Status.DELETED.name()))
                .willReturn(Optional.empty());
        given(variantInternalService.findVariantByProductVariant(productVariant))
                .willReturn(Optional.of(variantVersion));

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantServiceImpl.getVariantByPublicId(publicId));
    }

    @Test
    void getVariantByPublicIdVariantVersionNotFound() {
        ProductVariant productVariant = buildProductVariant();

        UUID publicId = UUID.randomUUID();
        given(variantInternalService.findByPublicIdAndStatusNot(publicId, Status.DELETED.name()))
                .willReturn(Optional.of(productVariant));
        given(variantInternalService.findVariantByProductVariant(productVariant)).willReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantServiceImpl.getVariantByPublicId(publicId));
    }

    @Test
    void getVariantsByProductCategoryPublicIdTest() {

        ProductCategory productCategory = buildProductCategory();
        VariantVersion variantVersion = buildVariantVersion();
        int page = 1, size = 10;
        UUID publicId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(1, 2);

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId))
                .thenReturn(productCategory);
        when(variantVersionInternalService.findVariantsByProductCategoryId(any(), any()))
                .thenReturn(new PageImpl<>(List.of(variantVersion), pageRequest, 1));

        var response = variantServiceImpl.getVariantsByProductCategoryPublicId(publicId, page, size);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getVariantsByProductCategoryPublicIdPageableExceptionTest() {

        int page = -1, size = -10;
        UUID publicId = UUID.randomUUID();

        Assertions.assertThrows(PageableException.class,
                () -> variantServiceImpl.getVariantsByProductCategoryPublicId(publicId, page, size));
    }

    @Test
    void getVariantsBySkuListTest() {
        VariantVersion variant = buildVariantVersion();
        List<String> skuList = List.of("SKU1", "SKU2");
        when(variantVersionInternalService.findAllBySkuIn(skuList)).thenReturn(List.of(variant));

        var response = variantServiceImpl.getVariantsBySkuList(skuList);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void searchVariantsTest() {
        int page = 1, size = 10;

        VariantVersion variantVersion = buildVariantVersion();

        when(variantInternalService.searchVariants(any(), any()))
                .thenReturn(new PageImpl<>(List.of(variantVersion), PageRequest.of(page, size), 1));

        var response = variantServiceImpl.searchVariants("search", page, size);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getProductVariantsByPublicIdsTest() {

        ProductVariant productVariant = buildProductVariant();
        VariantVersion variantVersion = buildVariantVersion();

        List<UUID> variantPublicIdList = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(variantInternalService.findProductVariantsByPublicIds(variantPublicIdList))
                .thenReturn(List.of(productVariant));
        when(variantVersionInternalService.findByProductVariantIdAndStatus(any(), any())).thenReturn(variantVersion);

        var response = variantServiceImpl.getProductVariantsByPublicIds(variantPublicIdList);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getProductVariantsByPublicIdsWhenProductDetailsIsNullTest() {

        ProductVariant productVariant = buildProductVariant();

        List<UUID> variantPublicIdList = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        when(variantInternalService.findProductVariantsByPublicIds(variantPublicIdList))
                .thenReturn(List.of(productVariant));
        when(variantVersionInternalService.findByProductVariantIdAndStatus(any(), any())).thenReturn(null);

        var response = variantServiceImpl.getProductVariantsByPublicIds(variantPublicIdList);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));

    }

    @Test
    void searchProductVariantsByPublicIdsTest() {
        String searchParam = "search";

        ProductVariant productVariant = buildProductVariant();
        VariantVersion variantVersion = buildVariantVersion();

        when(variantInternalService.findProductVariantsByPublicIds(any())).thenReturn(List.of(productVariant));
        when(variantVersionInternalService.searchVariantVersionByProductVariantsIn(any(), any()))
                .thenReturn(List.of(variantVersion));

        var response = variantServiceImpl.searchProductVariantsByPublicIds(searchParam, List.of(UUID.randomUUID()));

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getVariantIdsBySkuListTest() {

        List<String> skuList = List.of("SKU1", "SKU2");
        VariantVersion variantVersion = buildVariantVersion();

        when(variantVersionInternalService.findAllBySkuIn(any())).thenReturn(List.of(variantVersion));

        var response = variantServiceImpl.getVariantIdsBySkuList(skuList);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void getVariantsByProductPublicIdTest() {
        Product product = buildProduct();
        VariantVersion variantVersion = buildVariantVersion();

        when(productInternalService.findByPublicId(any())).thenReturn(product);
        when(variantInternalService.findAllByStatusAndProductVariantIn(any())).thenReturn(List.of(variantVersion));

        var response = variantServiceImpl.getVariantsByProductPublicId(UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.list.fetched.successfully"));
    }

    @Test
    void getVariantsByProductPublicIdNullProductTest() {

        when(productInternalService.findByPublicId(any())).thenReturn(null);

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantServiceImpl.getVariantsByProductPublicId(UUID.randomUUID()));
    }

    @Test
    void getVariantBySkuTest() {
        VariantVersion variantVersion = buildVariantVersion();

        when(variantVersionInternalService.findBySku(any())).thenReturn(variantVersion);

        var response = variantServiceImpl.getVariantBySku("sku");

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void approveVariantAwaitingApprovalEmptyImages() {

        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName("Test_WH_Loft_Africa");
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        ProductSkuDetail productSkuDetail = ProductSkuDetail.builder()
                .breath(BigDecimal.ZERO)
                .height(BigDecimal.ZERO)
                .weight(BigDecimal.ZERO)
                .length(BigDecimal.ZERO)
                .skuName(variantAwaitingApproval.getVariantName())
                .skuType("FG")
                .sku(variantAwaitingApproval.getSku())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .measuringUnit(variantAwaitingApproval.getProductVariant().getProduct().getMeasurementUnit().getName())
                .skuCategoryName(variantAwaitingApproval.getProduct().getProductCategory().getProductCategoryName())
                .skuDescription(variantAwaitingApproval.getProduct().getProductName())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .build();

        productSkuDetails.add(productSkuDetail);
        productRequestDto.setSkuDetails(productSkuDetails);

        VariantVersion variantVersion = buildVariantVersion();

        List<ImageCatalog> imageCatalogList = getImageCatalogList(variantAwaitingApproval.getProductVariant(),
                variantAwaitingApproval);

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);
        when(variantVersionInternalService.saveVariantVersionToDb(any()))
                .thenReturn(VariantVersion.builder().version(BigInteger.ZERO).build());
        when(imageCatalogInternalService.findByProductVariantId(any(UUID.class))).thenReturn(new ArrayList<>());
        when(stockOneProductInternalService.createProduct(productRequestDto)).thenReturn(ResponseEntity.ok().build());
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any(UUID.class)))
                .thenReturn(getImageCatalogList(variantAwaitingApproval.getProductVariant(), variantAwaitingApproval));
        when(variantVersionInternalService.findMostRecentVariantVersion(any(UUID.class))).thenReturn(variantVersion);

        doNothing().when(eventPublisher).publishEvent(any(ProductVariantApprovedEvent.class));
        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.approveVariantAwaitingApproval(publicId, requestDto));
    }

    @Test
    void approveVariantAwaitingApprovalNullProduct() {

        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        variantAwaitingApproval.setProductVariant(null);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName("Test_WH_Loft_Africa");
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        ProductSkuDetail productSkuDetail = ProductSkuDetail.builder()
                .breath(BigDecimal.ZERO)
                .height(BigDecimal.ZERO)
                .weight(BigDecimal.ZERO)
                .length(BigDecimal.ZERO)
                .skuName(variantAwaitingApproval.getVariantName())
                .skuType("FG")
                .sku(variantAwaitingApproval.getSku())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .measuringUnit(buildMeasuringUnit().getName())
                .skuCategoryName(variantAwaitingApproval.getProduct().getProductCategory().getProductCategoryName())
                .skuDescription(variantAwaitingApproval.getProduct().getProductName())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .build();

        productSkuDetails.add(productSkuDetail);
        productRequestDto.setSkuDetails(productSkuDetails);

        VariantVersion variantVersion = buildVariantVersion();

        List<ImageCatalog> imageCatalogList = getImageCatalogList(variantAwaitingApproval.getProductVariant(),
                variantAwaitingApproval);

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);
        when(variantVersionInternalService.saveVariantVersionToDb(any()))
                .thenReturn(VariantVersion.builder().version(BigInteger.ZERO).build());
        when(stockOneProductInternalService.createProduct(productRequestDto)).thenReturn(ResponseEntity.ok().build());
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any(UUID.class)))
                .thenReturn(getImageCatalogList(variantAwaitingApproval.getProductVariant(), variantAwaitingApproval));
        when(variantVersionInternalService.findMostRecentVariantVersion(any(UUID.class))).thenReturn(variantVersion);
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any())).thenReturn(imageCatalogList);

        doNothing().when(eventPublisher).publishEvent(any(ProductVariantApprovedEvent.class));

        var approvedResponse = variantServiceImpl.approveVariantAwaitingApproval(publicId, requestDto);

        assertThat(approvedResponse.getData()).isNotNull();
        assertThat(approvedResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.successfully.approved"));
        assertThat(approvedResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void approveVariantAwaitingApprovalImagesForVAAIsEmpty() {

        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();
        variantAwaitingApproval.setApprovalStatus(ApprovalStatus.PENDING.name());
        variantAwaitingApproval.setProductVariant(null);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setWarehouseName("Test_WH_Loft_Africa");
        List<ProductSkuDetail> productSkuDetails = new ArrayList<>();

        ProductSkuDetail productSkuDetail = ProductSkuDetail.builder()
                .breath(BigDecimal.ZERO)
                .height(BigDecimal.ZERO)
                .weight(BigDecimal.ZERO)
                .length(BigDecimal.ZERO)
                .skuName(variantAwaitingApproval.getVariantName())
                .skuType("FG")
                .sku(variantAwaitingApproval.getSku())
                .defaultImageUrl(variantAwaitingApproval.getDefaultImageUrl())
                .measuringUnit(buildMeasuringUnit().getName())
                .skuCategoryName(variantAwaitingApproval.getProduct().getProductCategory().getProductCategoryName())
                .skuDescription(variantAwaitingApproval.getProduct().getProductName())
                .costPrice(variantAwaitingApproval.getCostPrice())
                .build();

        productSkuDetails.add(productSkuDetail);
        productRequestDto.setSkuDetails(productSkuDetails);

        VariantVersion variantVersion = buildVariantVersion();

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);
        when(variantVersionInternalService.saveVariantVersionToDb(any()))
                .thenReturn(VariantVersion.builder().version(BigInteger.ZERO).build());
        when(stockOneProductInternalService.createProduct(productRequestDto)).thenReturn(ResponseEntity.ok().build());
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any(UUID.class)))
                .thenReturn(getImageCatalogList(variantAwaitingApproval.getProductVariant(), variantAwaitingApproval));
        when(variantVersionInternalService.findMostRecentVariantVersion(any(UUID.class))).thenReturn(variantVersion);
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any())).thenReturn(new ArrayList<>());

        doNothing().when(eventPublisher).publishEvent(any(ProductVariantApprovedEvent.class));

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.approveVariantAwaitingApproval(publicId, requestDto));
    }

    @Test
    void deleteVariantTest() {
        ProductVariant productVariant = buildProductVariant();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), null, null, null, null);

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantVersionInternalService.findByProductVariantId(any())).thenReturn(null);
        when(algoliaClientInternalService.deleteProductInAlgolia(any())).thenReturn(ResponseEntity.ok(appResponse));
        when(variantInternalService.deleteProductVariant(any())).thenReturn(productVariant);

        var response = variantServiceImpl.deleteVariant(UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.deleted.successfully"));
    }

    @Test
    void deleteVariantInUseTest() {
        ProductVariant productVariant = buildProductVariant();
        VariantVersion variantVersion = buildVariantVersion();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), null, null, null, null);

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantVersionInternalService.findByProductVariantId(any())).thenReturn(variantVersion);
        when(algoliaClientInternalService.deleteProductInAlgolia(any())).thenReturn(ResponseEntity.ok(appResponse));
        when(variantInternalService.deleteProductVariant(any())).thenReturn(productVariant);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantServiceImpl.deleteVariant(UUID.randomUUID()));
    }

    @Test
    void deleteVariantAlreadyDeletedTest() {
        ProductVariant productVariant = buildProductVariant();
        productVariant.setStatus(Status.DELETED.name());
        VariantVersion variantVersion = buildVariantVersion();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), null, null, null, null);

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantVersionInternalService.findByProductVariantId(any())).thenReturn(variantVersion);
        when(algoliaClientInternalService.deleteProductInAlgolia(any())).thenReturn(ResponseEntity.ok(appResponse));
        when(variantInternalService.deleteProductVariant(any())).thenReturn(productVariant);

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantServiceImpl.deleteVariant(UUID.randomUUID()));
    }

    @Test
    void deleteVariantNotFoundTest() {
        ProductVariant productVariant = buildProductVariant();
        productVariant.setStatus(Status.DELETED.name());
        VariantVersion variantVersion = buildVariantVersion();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), null, null, null, null);

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.empty());
        when(variantVersionInternalService.findByProductVariantId(any())).thenReturn(variantVersion);
        when(algoliaClientInternalService.deleteProductInAlgolia(any())).thenReturn(ResponseEntity.ok(appResponse));
        when(variantInternalService.deleteProductVariant(any())).thenReturn(productVariant);

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantServiceImpl.deleteVariant(UUID.randomUUID()));
    }

    @Test
    void deleteVariantAlgoliaTest() {
        ProductVariant productVariant = buildProductVariant();

        when(variantInternalService.findProductVariantByPublicId(any())).thenReturn(Optional.of(productVariant));
        when(variantVersionInternalService.findByProductVariantId(any())).thenReturn(null);
        when(algoliaClientInternalService.deleteProductInAlgolia(any())).thenReturn(ResponseEntity.notFound().build());
        when(variantInternalService.deleteProductVariant(any())).thenReturn(productVariant);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantServiceImpl.deleteVariant(UUID.randomUUID()));
    }

    @Test
    void getVariantsAwaitingApprovalTest() {

        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        String name = "";
        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add("ACTIVE");

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        when(variantAwaitingApprovalInternalService.searchVariantsAwaitingApproval(any(), any(), any(), any(), any(),
                anyList(), any()))
                .thenReturn(pagedProduct);

        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        AppResponse response = variantServiceImpl.getVariantsAwaitingApproval(name, "NGN", "2015-08-04", "2023-08-04",
                ApprovalStatus.PENDING.name(), listOfStatus, 1, 5);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.list.fetched.successfully"));
    }

    @Test
    void getVariantsAwaitingApprovalFromDateAfterToDateTest() {

        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        String name = "";
        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add("ACTIVE");

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        when(variantAwaitingApprovalInternalService.searchVariantsAwaitingApproval(any(), any(), any(), any(), any(),
                anyList(), any()))
                .thenReturn(pagedProduct);

        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.getVariantsAwaitingApproval(name, "NGN",
                        "2025-09-04", "2023-08-04", ApprovalStatus.PENDING.name(), listOfStatus, 1, 5));
    }

    private ResponseEntity<AppResponse<CountryDto>> getCountryMock() {
        CountryDto countryDto = CountryDto.builder().publicId(UUID.randomUUID()).build();
        AppResponse<CountryDto> appResponse = new AppResponse<>(HttpStatus.OK.value(), "", "",
                countryDto, null);
        return ResponseEntity.ok(appResponse);
    }

    @Test
    void getVariantsAwaitingApprovalEmptyListOfStatusTest() {

        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        String name = "";
        List<String> listOfStatus = new ArrayList<>();

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        when(variantAwaitingApprovalInternalService.searchVariantsAwaitingApproval(any(), any(), any(), any(), any(),
                anyList(), any()))
                .thenReturn(pagedProduct);

        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        AppResponse response = variantServiceImpl.getVariantsAwaitingApproval(name, "NGN", "2015-08-04", "2023-08-04",
                ApprovalStatus.PENDING.name(), listOfStatus, 1, 5);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.list.fetched.successfully"));
    }

    @Test
    void getVariantsAwaitingApprovalAlreadyDeletedTest() {

        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        String name = "";
        List<String> listOfStatus = new ArrayList<>();
        listOfStatus.add(Status.DELETED.name());

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        when(variantAwaitingApprovalInternalService.searchVariantsAwaitingApproval(any(), any(), any(), any(), any(),
                anyList(), any()))
                .thenReturn(pagedProduct);
        when(locationClientInternalService.findCountryByCode(any())).thenReturn(getCountryMock());

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantServiceImpl.getVariantsAwaitingApproval(name, "NGN", "2015-09-04", "2023-08-04",
                        ApprovalStatus.PENDING.name(), listOfStatus, 1, 5));
    }

    @Test
    void testGetVariantsWithMissingImagesInvalidDates() {
        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        Pageable pageable = PageRequest.of(0, 5);

        String name = "";

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        LocalDateTime fromDate = LocalDate.parse("2023-08-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-08-05").atTime(LocalTime.MAX);

        when(variantAwaitingApprovalInternalService.getVariantsWithMissingImages(name, fromDate, toDate, pageable))
                .thenReturn(pagedProduct);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.getVariantsWithMissingImages(name, "2023-08-02T", "T2023-08-05", 1, 5));
    }

    @Test
    void testGetVariantsWithMissingImagesStartDateAfterToDate() {
        VariantAwaitingApproval variants = buildVariantAwaitingApproval();

        Pageable pageable = PageRequest.of(0, 5);

        String name = "";

        List<VariantAwaitingApproval> productList = List.of(variants);
        Page<VariantAwaitingApproval> pagedProduct = new PageImpl<>(productList);

        LocalDateTime fromDate = LocalDate.parse("2023-08-02").atStartOfDay();
        LocalDateTime toDate = LocalDate.parse("2023-08-05").atTime(LocalTime.MAX);

        when(variantAwaitingApprovalInternalService.getVariantsWithMissingImages(name, fromDate, toDate, pageable))
                .thenReturn(pagedProduct);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.getVariantsWithMissingImages(name, "2024-08-02", "2023-08-05", 1, 5));
    }

    @Test
    void rejectVariantAwaitingApprovalAlreadyApproved() throws Exception {

        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus(ApprovalStatus.APPROVED.name())
                .build();
        RejectVariantRequestDto requestDto = new RejectVariantRequestDto();
        requestDto.setRejectedBy("admin@mail.com");
        requestDto.setRejectionReason("Incorrect data");

        UUID publicId = UUID.randomUUID();
        when(variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(publicId))
                .thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any())).thenReturn(any());

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.rejectVariantAwaitingApproval(publicId, requestDto));

    }

    @Test
    void rejectVariantAwaitingApprovalAlreadyRejected() throws Exception {

        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus(ApprovalStatus.REJECTED.name())
                .build();
        RejectVariantRequestDto requestDto = new RejectVariantRequestDto();
        requestDto.setRejectedBy("admin@mail.com");
        requestDto.setRejectionReason("Incorrect data");

        UUID publicId = UUID.randomUUID();

        when(variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(publicId))
                .thenReturn(variantAwaitingApproval);
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any())).thenReturn(any());

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.rejectVariantAwaitingApproval(publicId, requestDto));
    }

    @Test
    void editVariantAwaitingApproval() {

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();

        when(variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(any()))
                .thenReturn(variantAwaitingApproval);

        var variantResponse = variantServiceImpl.editVariantAwaitingApproval(UUID.randomUUID(),
                EditVariantAwaitingApprovalRequestDto.builder().lastModifiedBy("Dilip").build());

        assertThat(variantResponse).isNotNull();
        assertThat(variantResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.updated.successfully"));
    }

    @Test
    void updateVariantAwaitingApproval() {

        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();

        when(variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(any()))
                .thenReturn(variantAwaitingApproval);
        when(variantTypeInternalService.findVariantTypeById(any())).thenReturn(buildVariantType());

        var variantResponse = variantServiceImpl.editVariantAwaitingApproval(UUID.randomUUID(),
                EditVariantAwaitingApprovalRequestDto.builder()
                        .variantTypeId(UUID.randomUUID())
                        .variantName("Test")
                        .variantDescription("Desc")
                        .defaultImageUrl("url")
                        .costPrice(BigDecimal.valueOf(100))
                        .lastModifiedBy("Dilip")
                        .build());

        assertThat(variantResponse).isNotNull();
        assertThat(variantResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("variant.updated.successfully"));
    }

    @Test
    void getApprovedAndUnApprovedVariantIdsBySkuList() {

        List<String> skuList = new ArrayList<>();
        skuList.add("sku");

        VariantVersion variantVersion = buildVariantVersion();
        when(variantVersionInternalService.findAllBySkuIn(any())).thenReturn(List.of(variantVersion));

        var response = variantServiceImpl.getApprovedAndUnApprovedVariantIdsBySkuList(skuList);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void archiveProductVariant() {
        ProductVariant productVariant = buildProductVariant();
        productVariant.setStatus(Status.INACTIVE.name());

        when(productVariantInternalService.findByPublicId(any())).thenReturn(productVariant);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.archiveProductVariant(UUID.randomUUID()));
    }

    @Test
    void unarchiveProductVariant() {
        ProductVariant productVariant = buildProductVariant();
        productVariant.setStatus(Status.ACTIVE.name());

        when(productVariantInternalService.findByPublicId(any())).thenReturn(productVariant);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.unarchiveProductVariant(UUID.randomUUID()));
    }

    @Test
    void getVariantAwaitingByPublicId() {
        VariantAwaitingApproval variantAwaitingApproval = buildVariantAwaitingApproval();
        ImageCatalog imageCatalog = ImageCatalog.builder().build();

        when(variantAwaitingApprovalInternalService.findByPublicId(any())).thenReturn(variantAwaitingApproval);
        when(imageCatalogInternalService.findByVariantAwaitingApprovalId(any())).thenReturn(List.of(imageCatalog));

        var response = variantServiceImpl.getVariantAwaitingByPublicId(UUID.randomUUID());
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void searchVariantBySkuListAndProductName() {
        VariantVersion variantVersion = buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(variantVersionInternalService.searchVariantBySkuListAndProductName(any(), any(), any()))
                .thenReturn(variantVersions);

        var response = variantServiceImpl.searchVariantBySkuListAndProductName(List.of("sku1"), "search", 1, 10);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.fetched.successfully"));
    }

    @Test
    void searchVariantBySkuListAndProductNamePageableException() {
        VariantVersion variantVersion = buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(variantVersionInternalService.searchVariantBySkuListAndProductName(any(), any(), any()))
                .thenReturn(variantVersions);

        Assertions.assertThrows(PageableException.class,
                () -> variantServiceImpl.searchVariantBySkuListAndProductName(List.of("sku1"), "search", -2, -3));
    }

    @Test
    void testGetStockOneProductsPageableException() throws Exception {
        String traceId = UUID.randomUUID().toString();

        AppResponse inventoryResponse = new AppResponse(HttpStatus.OK.value(),
                "Warehouse Fetched Successfully",
                "Warehouse Fetched Successfully",
                null, null);

        AppResponse stockOneAppResponse = new AppResponse(HttpStatus.OK.value(),
                "success",
                "success",
                null, null);

        when(inventoryClientInternalService.getWarehouseByName(traceId, "test"))
                .thenReturn(inventoryResponse);
        when(stockOneProductInternalService.getProducts(traceId, "test", "", 1, 1))
                .thenReturn(stockOneAppResponse);

        Assertions.assertThrows(PageableException.class,
                () -> variantServiceImpl.getProductsFromStockOne(traceId, "test", "", -1, -1));
    }

    @Test
    void testGetStockOneProductsStockOneException() throws Exception {
        String traceId = UUID.randomUUID().toString();

        AppResponse inventoryResponse = new AppResponse(HttpStatus.OK.value(),
                "Warehouse Fetched Successfully",
                "Warehouse Fetched Successfully",
                null, null);

        when(inventoryClientInternalService.getWarehouseByName(traceId, "test"))
                .thenReturn(inventoryResponse);
        when(stockOneProductInternalService.getProducts(traceId, "test", "", 1, 1))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(StockOneException.class,
                () -> variantServiceImpl.getProductsFromStockOne(traceId, "test", "", 1, 1));
    }

    @Test
    void editThresholdAndLeadTimeException() {

        var categoryPublicId = UUID.randomUUID();
        VariantVersion variantVersion = buildVariantVersion();

        EditLiveInventoryRequestDto requestDto = EditLiveInventoryRequestDto.builder()
                .leadTime(7)
                .threshold(5000)
                .modifiedBy("Dilip")
                .sku("SKU12345")
                .build();

        VariantAwaitingApproval variantAwaitingApproval = VariantHelper.buildVariantAwaitingApproval(variantVersion,
                requestDto);
        variantAwaitingApproval.setId(UUID.randomUUID());
        variantAwaitingApproval.setLeadTime(requestDto.getLeadTime());
        variantAwaitingApproval.setThreshold(requestDto.getThreshold());

        when(variantVersionInternalService.findVariantBySku(requestDto.getSku())).thenReturn(variantVersion);
        when(variantAwaitingApprovalInternalService.findBySkuAndApprovalStatus(any(), anyString()))
                .thenReturn(buildVariantAwaitingApproval());
        when(variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(any()))
                .thenReturn(variantAwaitingApproval);

        Assertions.assertThrows(ValidatorException.class,
                () -> variantServiceImpl.editThresholdAndLeadTime(requestDto));
    }

    private UpdateImageCatalogRequestDto buildUpdateImageCatalogRequestDto() {
        return UpdateImageCatalogRequestDto.builder()
                .imageName("test")
                .imageDescription("desc")
                .imageUrl("url")
                .imageCatalogPublicId(UUID.randomUUID())
                .modifiedBy("mod")
                .build();
    }

    private ProductVariant buildProductVariant(UUID originalPublicId, Status status) {
        ProductVariant productVariant = ProductVariant.builder()
                .originalPublicId(originalPublicId)
                .status(status.name())
                .product(Product.builder()
                        .productName("test")
                        .vated(false)
                        .build())
                .build();
        productVariant.setPublicId(originalPublicId);
        productVariant.setId(originalPublicId);
        return productVariant;
    }

    private VariantType buildVariantType() {
        VariantType variantType = VariantType.builder()
                .variantTypeName("variantType")
                .status(Status.ACTIVE.name())
                .build();
        variantType.setPublicId(UUID.randomUUID());
        variantType.setId(UUID.randomUUID());
        return variantType;
    }

    private UpdateVariantRequestDto buildUpdateVariantRequestDto(UUID publicId) {
        UpdateVariantRequestDto requestDto = new UpdateVariantRequestDto();
        requestDto.setVariantTypePublicId(UUID.randomUUID());
        requestDto.setVariantName("Variant");
        requestDto.setVariantDescription("Desc");
        requestDto.setCountryPublicId(publicId);
        requestDto.setImageCatalogs(List.of(UpdateImageCatalogRequestDto.builder()
                .imageName("test")
                .imageDescription("desc")
                .imageUrl("url")
                .imageCatalogPublicId(UUID.randomUUID())
                .modifiedBy("mod")
                .build()));
        requestDto.setCostPrice(BigDecimal.ONE);
        requestDto.setModifiedBy("Mod");
        return requestDto;
    }

    private Product buildProduct() {
        Brand brand = buildBrand();
        ProductCategory productCategory = buildProductCategory();
        Manufacturer manufacturer = buildManufacturer();
        MeasuringUnit measuringUnit = buildMeasuringUnit();
        WarrantyType warrantyType = buildWarrantyType();

        Product product = Product.builder()
                .productName("name")
                .status(Status.ACTIVE.name())
                .productCategory(productCategory)
                .brand(brand)
                .productDescription("desc")
                .productNotes("notes")
                .productHighlights("Highlights")
                .manufacturer(manufacturer)
                .measurementUnit(measuringUnit)
                .warrantyType(warrantyType)
                .warrantyDuration("duration")
                .warrantyAddress("address")
                .warrantyCover("cover")
                .productListing("listings")
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        product.setVated(false);
        product.setVersion(BigInteger.ZERO);
        product.setCreatedBy("test");
        product.setCreatedDate(LocalDateTime.now());
        product.setLastModifiedBy("test");
        product.setLastModifiedDate(LocalDateTime.now());

        return product;
    }

    private WarrantyType buildWarrantyType() {
        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName("WarrantyTypeName")
                .status(Status.ACTIVE)
                .description("desc")
                .build();

        warrantyType.setId(UUID.randomUUID());
        warrantyType.setPublicId(UUID.randomUUID());
        warrantyType.setVersion(BigInteger.ZERO);
        warrantyType.setCreatedBy("test");
        warrantyType.setCreatedDate(LocalDateTime.now());
        warrantyType.setLastModifiedBy("test");
        warrantyType.setLastModifiedDate(LocalDateTime.now());

        return warrantyType;
    }

    private Brand buildBrand() {
        Brand brand = Brand.builder()
                .brandName("Samsung")
                .description("Samsung")
                .status(Status.ACTIVE)
                .build();
        brand.setId(UUID.randomUUID());
        brand.setPublicId(UUID.randomUUID());
        brand.setVersion(BigInteger.ZERO);
        brand.setCreatedBy("test");
        brand.setCreatedDate(LocalDateTime.now());
        brand.setLastModifiedBy("test");
        brand.setLastModifiedDate(LocalDateTime.now());
        brand.setManufacturer(buildManufacturer());

        return brand;
    }

    private ProductCategory buildProductCategory() {
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .status(Status.ACTIVE)
                .depth(10)
                .imageUrl("url")
                .description("desc")
                .build();

        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(UUID.randomUUID());
        productCategory.setVersion(BigInteger.ZERO);
        productCategory.setCreatedBy("test");
        productCategory.setCreatedDate(LocalDateTime.now());
        productCategory.setLastModifiedBy("test");
        productCategory.setLastModifiedDate(LocalDateTime.now());

        return productCategory;
    }

    private MeasuringUnit buildMeasuringUnit() {
        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Test")
                .abbreviation("Test")
                .description("Test")
                .status(Status.ACTIVE.name())
                .build();

        measuringUnit.setId(UUID.randomUUID());
        measuringUnit.setPublicId(UUID.randomUUID());
        measuringUnit.setVersion(BigInteger.ZERO);
        measuringUnit.setCreatedBy("test");
        measuringUnit.setCreatedDate(LocalDateTime.now());
        measuringUnit.setLastModifiedBy("test");
        measuringUnit.setLastModifiedDate(LocalDateTime.now());

        return measuringUnit;
    }

    private Manufacturer buildManufacturer() {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .description("desc")
                .status(Status.ACTIVE)
                .build();

        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setVersion(BigInteger.ZERO);
        manufacturer.setCreatedBy("test");
        manufacturer.setCreatedDate(LocalDateTime.now());
        manufacturer.setLastModifiedBy("test");
        manufacturer.setLastModifiedDate(LocalDateTime.now());

        return manufacturer;
    }

    private ProductVariant buildProductVariant() {
        Product product = buildProduct();
        ProductVariant productVariant = new ProductVariant();
        productVariant.setIsVated(false);
        productVariant.setVariantName("LG X 1");
        productVariant.setStatus(Status.ACTIVE.name());
        productVariant.setProduct(product);
        productVariant.setOriginalPublicId(UUID.randomUUID());
        productVariant.setApprovedBy("test");
        productVariant.setApprovedDate(LocalDateTime.now());
        productVariant.setId(UUID.randomUUID());
        productVariant.setPublicId(UUID.randomUUID());
        productVariant.setVersion(BigInteger.ZERO);
        productVariant.setCreatedBy("test");
        productVariant.setCreatedDate(LocalDateTime.now());
        productVariant.setLastModifiedBy("test");
        productVariant.setLastModifiedDate(LocalDateTime.now());
        productVariant.setVariantType(new VariantType("Colour", "Different colour", "ACTIVE"));
        return productVariant;
    }

    private VariantVersion buildVariantVersion() {
        return VariantVersion.builder()
                .id(UUID.randomUUID())
                .product(buildProduct())
                .productVariant(buildProductVariant())
                .sku("testsku")
                .variantType(buildVariantType())
                .variantName("name")
                .variantDescription("desc")
                .costPrice(BigDecimal.valueOf(100))
                .defaultImageUrl("url")
                .threshold(100)
                .leadTime(100)
                .status(Status.ACTIVE.name())
                .approvalStatus(ApprovalStatus.APPROVED.name())
                .version(BigInteger.ZERO)
                .approvedDate(LocalDateTime.now())
                .approvedBy("test")
                .build();
    }

    private VariantAwaitingApproval buildVariantAwaitingApproval() {
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .variantType(buildVariantType())
                .product(buildProduct())
                .variantName("variant name")
                .variantDescription("desc")
                .sku("testSku")
                .costPrice(BigDecimal.valueOf(100))
                .defaultImageUrl("url")
                .leadTime(100)
                .threshold(100)
                .status(Status.ACTIVE.name())
                .productVariant(buildProductVariant())
                .approvalStatus(ApprovalStatus.APPROVED.name())
                .completedDate(LocalDateTime.now())
                .completedBy("test")
                .rejectedReason("reason")
                .productVariantDetails("details")
                .build();

        variantAwaitingApproval.setId(UUID.randomUUID());
        variantAwaitingApproval.setPublicId(UUID.randomUUID());
        variantAwaitingApproval.setVersion(BigInteger.ZERO);
        variantAwaitingApproval.setCreatedBy("test");
        variantAwaitingApproval.setCreatedDate(LocalDateTime.now());
        variantAwaitingApproval.setLastModifiedBy("test");
        variantAwaitingApproval.setLastModifiedDate(LocalDateTime.now());

        return variantAwaitingApproval;
    }

    private List<ImageCatalog> getImageCatalogList() {
        List<ImageCatalog> result = new ArrayList<>();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(new ProductVariant())
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(new VariantAwaitingApproval()).build();
        result.add(imageCatalog);

        return result;
    }

    private List<ImageCatalog> getImageCatalogList(ProductVariant productVariant,
            VariantAwaitingApproval variantAwaitingApproval) {
        List<ImageCatalog> result = new ArrayList<>();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(productVariant)
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(variantAwaitingApproval)
                .build();
        result.add(imageCatalog);

        return result;
    }
}
