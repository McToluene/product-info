package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ProductController;
import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BulkProductUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.UpdateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ProductCatalogueResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.ProductVersionResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.*;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
@Import(ProductService.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public ProductService productService;

    @MockBean
    public TraceService traceService;

    @MockBean
    public FailedProductService failedProductService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    ProductControllerTest() {

    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void createNewProduct() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.created.successfully"),
                messageSourceService.getMessageByKey("product.created.successfully"),
                null, null);
        CreateProductRequestDto requestDto = buildProductRequestDto();
        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.createNewProduct(requestDto, Boolean.FALSE, "NGN")).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID())
                    .header("x-country-code", "NGN"))
                    .andExpect(status().isCreated());
        }

    }

    @Test
    void getProductsByCategorySuccessfully() throws Exception {

        UUID categoryId = UUID.randomUUID();

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.getProductsByProductCategoryId(any(UUID.class), any(Integer.class), any(Integer.class)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/product/category/" + categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    public static CreateProductRequestDto buildProductRequestDto() {
        CreateProductRequestDto product = CreateProductRequestDto.builder().build();
        product.setProductName("Product one".toUpperCase());
        product.setProductDescription("This is test product");
        product.setBrandPublicId(UUID.randomUUID());
        product.setManufacturerPublicId(UUID.randomUUID());
        product.setCategoryPublicId(UUID.randomUUID());
        product.setMeasurementUnitPublicId(UUID.randomUUID());
        product.setProductListings(Collections.singleton(ProductListing.AGENTAPP));
        product.setProductHighlights("This is test");
        product.setWarrantyAddress("Test");
        product.setWarrantyCover("All");
        product.setWarrantyDuration("6 months");
        product.setWarrantyTypePublicId(UUID.randomUUID().toString());
        product.setCreatedBy(UUID.randomUUID().toString());
        product.setVated(Boolean.TRUE);
        product.setMinVat(BigDecimal.ZERO);
        product.setMaxVat(BigDecimal.ONE);
        return product;

    }

    @Test
    void getProductList() throws Exception {

        int page = 0;
        int size = 5;
        int count = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"),
                getProductResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.getAllProducts(page, size, "", null, null, null,
                    null, null, null, null)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private Page<Product> getProductResponse(PageRequest pageRequest, int count) {
        List<Product> productVersionResponse = new ArrayList<>();
        final int start = (int) pageRequest.getOffset();
        final int end = Math.min((start + pageRequest.getPageSize()), productVersionResponse.size());
        Page<Product> page = new PageImpl<>(productVersionResponse.subList(start, end), pageRequest,
                productVersionResponse.size());
        for (int i = 0; i <= count; i++) {

            productVersionResponse.add(Product.builder()
                    .productName("product-" + i)
                    .status(Status.ACTIVE.name())
                    .productDescription("product")
                    .manufacturer(new Manufacturer())
                    .productCategory(new ProductCategory())
                    .brand(new Brand())
                    .warrantyType(new WarrantyType())
                    .build());

        }
        return page;
    }

    @Test
    void updateProductByPublicId() throws Exception {
        var publicId = UUID.randomUUID();
        var categoryPublicId = UUID.randomUUID();
        var productCountry = UUID.randomUUID();
        var warrantyDuration = "today";
        var warrantyAddress = "address";
        var warrantyCover = "warrantyCover";
        var productListing = "productListing";
        var productHighlights = "productHighlights";
        var manufacturerPublicId = UUID.randomUUID();
        var warrantyTypePublicId = UUID.randomUUID();
        var warrantyType = "warrantyType";
        var defaultImageUrl = "defaultImageUrl";
        var productDescription = "productDescription";
        var modifiedBy = "modifiedBy";
        var measurementUnit = "measurementUnit";
        var xtraceid = UUID.randomUUID();

        UpdateProductRequestDto requestDto = UpdateProductRequestDto.builder()
                .brandPublicId(publicId)
                .categoryPublicId(categoryPublicId)
                .countryId(productCountry)
                .productListing(productListing)
                .productHighlights(productHighlights)
                .manufacturerPublicId(manufacturerPublicId)
                .warrantyTypePublicId(warrantyTypePublicId)
                .defaultImageUrl(defaultImageUrl)
                .productDescription(productDescription)
                .modifiedBy(modifiedBy)
                .measurementUnitPublicId(UUID.randomUUID())
                .warrantyCover(warrantyCover)
                .warrantyAddress(warrantyAddress)
                .warrantyDuration(warrantyDuration)
                .build();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                null, null);

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.updateProductByPublicId(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(xtraceid);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", xtraceid))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void checkValidProductsList() throws Exception {
        List<UUID> list = new ArrayList<>();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                list, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.getApprovedProductsByPublicIdList(any(List.class)))
                    .thenReturn(response);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/approved-list")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(list.toString())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());

        }
    }

    @Test
    void checkValidProductsListUsingSku() throws Exception {
        List<UUID> list = new ArrayList<>();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                list, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));
            when(productService.getApprovedProductsPublicIdListUsingSku(any(List.class)))
                    .thenReturn(response);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/sku/approved-list")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(list.toString())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());

        }
    }

    @Test
    void getProductsByBrandPublicId() throws Exception {
        List<ProductVersionResponseDto> products = new ArrayList<>();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                products, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));
            when(productService.getProductsByBrand(any(UUID.class))).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/brand/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getProductByPublicId() throws Exception {
        UUID publicId = UUID.randomUUID();

        final var product = Product.builder()
                .productName("product")
                .status(Status.ACTIVE.name())
                .productDescription("product")
                .manufacturer(new Manufacturer())
                .productCategory(new ProductCategory())
                .warrantyType(new WarrantyType())
                .brand(new Brand())
                .build();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                product, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));
            when(productService.getProductByPublicId(publicId)).thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void uploadProductUsingExcel() throws Exception {
        Mono<AppResponse> responseEntityMono = Mono.just(new AppResponse(HttpStatus.OK.value(),
                "bulk upload of product in process",
                "bulk upload of product in process", null, null));

        List<String> bulkUploadTemplate = new ArrayList<>();
        bulkUploadTemplate.add("test");
        BulkProductUploadRequest requestDto = BulkProductUploadRequest.builder()
                .imageUploadTemplateRequests(List.of())
                .createdBy("shafat")
                .build();

        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", "Hello".getBytes(), MediaType.MULTIPART_FORM_DATA);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.uploadProductUsingExcel(any(), any(), any())).thenReturn(responseEntityMono.block());
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/link-using-excel")
                    .queryParam("createdBy", "abc")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void archiveProduct() throws Exception {

        UUID productPublicId = UUID.randomUUID();

        final var product = Product.builder()
                .productName("product")
                .status(Status.INACTIVE.name())
                .productDescription("product")
                .manufacturer(new Manufacturer())
                .productCategory(new ProductCategory())
                .warrantyType(new WarrantyType())
                .brand(new Brand())
                .build();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.archived.successfully"),
                messageSourceService.getMessageByKey("product.archived.successfully"),
                product, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.updateProductArchiveStatus(productPublicId, Status.INACTIVE.name()))
                    .thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/" + productPublicId + "/archive")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void unarchiveProduct() throws Exception {

        UUID productPublicId = UUID.randomUUID();

        final var product = Product.builder()
                .productName("product")
                .status(Status.ACTIVE.name())
                .productDescription("product")
                .manufacturer(new Manufacturer())
                .productCategory(new ProductCategory())
                .warrantyType(new WarrantyType())
                .brand(new Brand())
                .build();

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.unarchived.successfully"),
                messageSourceService.getMessageByKey("product.unarchived.successfully"),
                product, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.updateProductArchiveStatus(productPublicId, Status.ACTIVE.name()))
                    .thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product/" + productPublicId + "/unarchive")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getProductCatalogueTest() throws Exception {
        UUID warehouseId = UUID.randomUUID();
        int page = 1;
        int size = 3;
        String searchValue = "tes";
        UUID traceId = UUID.randomUUID();
        UUID stateId = UUID.randomUUID();
        UUID lgaId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();

        ProductCatalogueResponseDto productCatalogueResponseDto = ProductCatalogueResponseDto.builder()
                .sku("test")
                .variantName("testVariant")
                .supplierName("testSupplier")
                .individualPricing(100D)
                .maximumOrderQuantity(BigInteger.TEN)
                .minimumOrderQuantity(BigInteger.ONE)
                .warehouseId(warehouseId)
                .build();
        List<ProductCatalogueResponseDto> productCatalogueResponseDtos = List.of(productCatalogueResponseDto);

        PageImpl<ProductCatalogueResponseDto> productCatalogueResponsePage = new PageImpl<>(
                productCatalogueResponseDtos, PageRequest.of(page, size), productCatalogueResponseDtos.size());

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"),
                productCatalogueResponsePage, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.createProductCatalogue(traceId, warehouseId, stateId, cityId, lgaId, searchValue, page,
                    size)).thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(traceId);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product")
                    .queryParam("warehouseId", String.valueOf(warehouseId))
                    .queryParam("cityId", String.valueOf(cityId))
                    .queryParam("searchValue", searchValue)
                    .queryParam("stateId", String.valueOf(stateId))
                    .queryParam("lgaId", String.valueOf(lgaId))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", traceId))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void createProductCatalogue() throws Exception {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"),
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(productService.createProductCatalogue(any(), any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product/product-catalogue")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getProductsByProductCategoryIds() throws Exception {

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                null, null);
        String payload = this.mapToJson(List.of(UUID.randomUUID().toString()));

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(productService.getProductsByProductCategoryIds(any(), any(), any())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product/get-for-categories")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(payload)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteProduct() throws Exception {

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.deleted.successfully"),
                messageSourceService.getMessageByKey("product.deleted.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(productService.deleteProduct(any())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product/{publicId}", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getListOfProductIdsByProductCategory() throws Exception {

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                messageSourceService.getMessageByKey("products.retrieved.success"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(productService.getProductsByProductCategory(any())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/product/category/id-list/{categoryId}", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void createNewProductWithoutNameTest() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "field should not be empty",
                "field should not be empty",
                null, List.of("product name cannot be empty"));
        CreateProductRequestDto requestDto = new CreateProductRequestDto();
        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productService.createNewProduct(requestDto, Boolean.FALSE, "NGN")).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID())
                    .header("x-country-code", "NGN"))
                    .andExpect(status().isBadRequest());
        }

    }
}
