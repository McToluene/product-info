package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ProductCategoryController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.UpdateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryWithSubcategoryResponse;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductCategoryController.class)
class ProductCategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductCategoryService productCategoryService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;
    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    @Test
    void getProductCategoriesByPublicIdList() throws Exception {
        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();

        UUID publicId = UUID.randomUUID();
        List<UUID> publicIdList = new ArrayList<>();
        List<ProductCategoryResponseDto> response = new ArrayList<>();

        publicIdList.add(publicId);
        response.add(responseDto);
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                response, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getProductCategoriesByPublicIds(any(List.class))).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/product-category/get-categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapToJson(publicIdList))
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createProductCategoryWithoutParentCategorySuccessfully() throws Exception {

        CreateProductCategoryRequestDto requestDto = getCreateProductCategoryRequestDto();
        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                responseDto, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.createProductCategory(any(CreateProductCategoryRequestDto.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/product-category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapToJson(requestDto))
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productCategoryName").value("Automobiles"))
                    .andExpect(jsonPath("$.data.description").value("All things automobiles"))
                    .andExpect(jsonPath("$.data.createdBy").value("Stan Lee"))
                    .andExpect(jsonPath("$.data.imageUrl").value("https://imageRepo.com/1243"));
        }

    }

    @Test
    void createProductCategoryWithParentCategorySuccessfully() throws Exception {

        CreateProductCategoryRequestDto requestDto = getCreateProductCategoryRequestDto();

        UUID parentId1 = UUID.randomUUID();
        UUID parentId2 = UUID.randomUUID();
        requestDto.setProductCategoryParentPublicIds(List.of(parentId1, parentId2));

        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                messageSourceService.getMessageByKey("product.category.created.successfully"),
                responseDto, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.createProductCategory(any(CreateProductCategoryRequestDto.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/product-category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapToJson(requestDto))
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productCategoryName").value("Automobiles"))
                    .andExpect(jsonPath("$.data.description").value("All things automobiles"))
                    .andExpect(jsonPath("$.data.createdBy").value("Stan Lee"))
                    .andExpect(jsonPath("$.data.imageUrl").value("https://imageRepo.com/1243"));
        }

    }

    @Test
    void getProductCategoryByPublicIdSuccessfully() throws Exception {
        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();

        UUID publicId = UUID.randomUUID();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                responseDto, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getProductCategoryByPublicId(any(UUID.class))).thenReturn(response);

            mockMvc.perform(get("/api/v1/product-category/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productCategoryName").value("Automobiles"))
                    .andExpect(jsonPath("$.data.description").value("All things automobiles"))
                    .andExpect(jsonPath("$.data.createdBy").value("Stan Lee"))
                    .andExpect(jsonPath("$.data.imageUrl").value("https://imageRepo.com/1243"));
        }

    }

    @Test
    void getProductCategoriesWithFilter() throws Exception {
        UUID publicId = UUID.randomUUID();
        int page = 0;
        int size = 5;
        int count = 10;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(page, size);

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"),
                getProductCategoryResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getAllProductCategoriesFiltered(page, size,
                    getProductCategoryResponseDto().productCategoryName(), startDate, endDate)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product-category/filter/")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .queryParam("productCategoryName",
                            String.valueOf(getProductCategoryResponseDto().productCategoryName()))
                    .queryParam("startDate", String.valueOf(startDate))
                    .queryParam("endDate", String.valueOf(endDate))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private Page<ProductCategory> getProductCategoryResponse(PageRequest pageRequest, int count) {
        List<ProductCategory> categoryResponse = new ArrayList<>();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), categoryResponse.size());
        Page<ProductCategory> page = new PageImpl<>(categoryResponse.subList(start, end), pageRequest,
                categoryResponse.size());
        for (int i = 0; i <= count; i++) {

            categoryResponse.add(ProductCategory.builder()
                    .productCategoryName("Automobiles")
                    .description("All things automobiles")
                    .status(Status.ACTIVE)
                    .depth(8)
                    .imageUrl("https://imageRepo.com/1243")
                    .build());

        }
        return page;
    }

    @Test
    void getAllDirectChildrenOfProductCategorySuccessfully() throws Exception {
        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();
        List<ProductCategoryResponseDto> responseDtoList = List.of(responseDto);
        Page<ProductCategoryResponseDto> pageResponse = new PageImpl<>(responseDtoList);

        UUID publicId = UUID.randomUUID();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                pageResponse, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getProductCategoryByPublicId(any(UUID.class))).thenReturn(response);

            mockMvc.perform(get("/api/v1/product-category/" + publicId + "/children")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    @Test
    void testGetAllDirectChildrenOfProductCategorySuccessfully() throws Exception {
        ProductCategoryResponseDto responseDto = getProductCategoryResponseDto();
        List<ProductCategoryResponseDto> responseDtoList = List.of(responseDto);
        Page<ProductCategoryResponseDto> pageResponse = new PageImpl<>(responseDtoList);

        UUID publicId = UUID.randomUUID();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                pageResponse, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getAllNestedSubcategoryOfProductCategory(any(UUID.class))).thenReturn(response);

            mockMvc.perform(get("/api/v1/product-category/" + publicId + "/nested")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    private ProductCategoryResponseDto getProductCategoryResponseDto() {

        return ProductCategoryResponseDto.builder()
                .publicId(UUID.randomUUID())
                .productCategoryName("Automobiles")
                .description("All things automobiles")
                .createdBy("Stan Lee")
                .imageUrl("https://imageRepo.com/1243")
                .build();
    }

    private CreateProductCategoryRequestDto getCreateProductCategoryRequestDto() {

        return CreateProductCategoryRequestDto.builder()
                .productCategoryName("Automobiles")
                .description("All things automobiles")
                .createdBy("Stan Lee")
                .imageUrl("http://imageRepo.com/1243")
                .build();
    }

    private static String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void archiveProductCategory() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                null, null);

        UUID productCategoryPublicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.archiveProductCategory(productCategoryPublicId)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/product-category/" + productCategoryPublicId + "/archive")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("x-country-code", UUID.randomUUID())
                            .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void unArchiveProductCategory() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("category.unarchived.successfully"),
                messageSourceService.getMessageByKey("category.unarchived.successfully"),
                null, null);

        UUID productCategoryPublicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.unArchiveProductCategory(productCategoryPublicId)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/product-category/" + productCategoryPublicId + "/unarchive")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("x-trace-id", UUID.randomUUID())
                            .header("x-country-code", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void updateProductCategory() throws Exception {

        UpdateProductCategoryRequestDto requestDto = UpdateProductCategoryRequestDto.builder()
                .productCategoryName("test")
                .description("desc")
                .modifiedBy("test")
                .imageUrl("url")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .build();

        String inputJson = mapToJson(requestDto);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.updated.successfully"),
                messageSourceService.getMessageByKey("product.category.updated.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.updateProductCategory(any(), any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/product-category/{publicId}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inputJson)
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getAllProductCategories() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                messageSourceService.getMessageByKey("product.category.fetched.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.getAllProductCategories(any(), any(), any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/product-category/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("onlyParentCategory", "true")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteProductCategoryByPublicId() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.category.deleted.successfully"),
                messageSourceService.getMessageByKey("product.category.deleted.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.deleteProductCategory(any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/product-category/{publicId}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("onlyParentCategory", "true")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void createProductCategoryWithoutNameTest() throws Exception {

        CreateProductCategoryRequestDto requestDto = new CreateProductCategoryRequestDto();

        AppResponse response = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "field should not be empty",
                "field should not be empty",
                null, List.of("product category name cannot be empty"));

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(productCategoryService.createProductCategory(requestDto)).thenReturn(response);

            mockMvc.perform(post("/api/v1/product-category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapToJson(requestDto))
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isBadRequest());
        }

    }

    @Test
    void getProductCategoryWithImmediateChildren() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                "product category fetched successfully",
                "product category fetched successfully",
                new ProductCategoryWithSubcategoryResponse(), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));
            when(productCategoryService.getProductCategories(any(), any()))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/product-category/with-subcategory")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-country-code", UUID.randomUUID())
                    .header("x-trace-id", UUID.randomUUID()))

                    .andDo(print())
                    .andExpect(status().isOk());

        }
    }

}
