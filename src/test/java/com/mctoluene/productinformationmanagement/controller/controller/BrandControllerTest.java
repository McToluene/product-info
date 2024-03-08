package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.BrandController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.BrandHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.BrandService;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BrandController.class)
@Import(BrandService.class)
class BrandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public BrandService brandService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    BrandControllerTest() {
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createBrand() throws Exception {
        AppResponse<BrandResponseDto> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("brand.created.successfully"),
                messageSourceService.getMessageByKey("brand.created.successfully"),
                getBrandResponseDto(), null);

        CreateBrandRequestDto requestDto = CreateBrandRequestDto.builder()
                .brandName("Adidas")
                .description("we are giant")
                .createdBy("Dilip")
                .manufacturerId(UUID.randomUUID())
                .build();

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.createBrand(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brand")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void getBrand() throws Exception {
        BrandResponseDto responseDto = getBrandResponseDto();
        AppResponse<BrandResponseDto> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("brand.fetched.successfully"),
                messageSourceService.getMessageByKey("brand.fetched.successfully"),
                responseDto, null);

        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.getBrandByPublicId(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.brandName").value("Adidas"))
                    .andExpect(jsonPath("$.data.description").value("We are sports industry"))
                    .andExpect(jsonPath("$.data.createdBy").value("Dilip"));
        }

    }

    private static BrandResponseDto getBrandResponseDto() {

        Brand brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .build();

        brand.setVersion(BigInteger.ZERO);
        brand.setLastModifiedBy("Dilip");
        brand.setCreatedBy("Dilip");
        brand.setLastModifiedDate(LocalDateTime.now());
        brand.setCreatedDate(LocalDateTime.now());
        brand.setManufacturer(Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve Beauty products")
                .status(Status.ACTIVE)
                .build());

        return BrandHelper.buildBrandResponse(brand);
    }

    @Test
    void getBrands() throws Exception {
        int page = 0;
        int size = 5;
        int count = 30;
        PageRequest pageRequest = PageRequest.of(page, size);

        AppResponse<Page<BrandResponseDto>> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                getBrandResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.getBrands(page, size)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private Page<BrandResponseDto> getBrandResponse(PageRequest pageRequest, int count) {
        List<BrandResponseDto> brandResponse = new ArrayList<>();
        final int start = (int) pageRequest.getOffset();
        final int end = Math.min((start + pageRequest.getPageSize()), brandResponse.size());
        Page<BrandResponseDto> page = new PageImpl<>(brandResponse.subList(start, end), pageRequest,
                brandResponse.size());

        for (int i = 0; i <= count; i++) {

            Brand brand = Brand.builder().brandName("Brand-" + i)
                    .status(Status.ACTIVE)
                    .description("No description needed")
                    .build();

            brand.setPublicId(UUID.randomUUID());
            brand.setVersion(BigInteger.ZERO);
            brand.setLastModifiedBy("Dilip");
            brand.setLastModifiedDate(LocalDateTime.now());
            brand.setCreatedBy("Dilip");
            brand.setCreatedDate(LocalDateTime.now());
            brand.setManufacturer(Manufacturer.builder()
                    .manufacturerName("Nykaa")
                    .description("We serve Beauty products")
                    .status(Status.ACTIVE)
                    .build());

            brandResponse.add(BrandHelper.buildBrandResponse(brand));

        }
        return page;
    }

    @Test
    void editBrand() throws Exception {
        AppResponse<BrandResponseDto> response = new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                messageSourceService.getMessageByKey("brand.updated.successfully"),
                getBrandResponseDto(), null);

        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .lastModifiedBy("Dilip")
                .build();

        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.editBrand(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/brand/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteBrand() throws Exception {
        AppResponse<Void> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("brand.deleted.successfully"),
                messageSourceService.getMessageByKey("brand.deleted.successfully"),
                null, null);

        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.deleteBrand(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/brand/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void createBrandWithoutNameTest() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "field should not be empty",
                "field should not be empty",
                null, List.of("Brand name must be provided"));

        CreateBrandRequestDto requestDto = new CreateBrandRequestDto();

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.createBrand(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/brand")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

    }

    @Test
    void getBrandsByManufacture() throws Exception {
        int page = 0;
        int size = 5;
        int count = 30;
        PageRequest pageRequest = PageRequest.of(page, size);
        UUID manufacturePublicId = UUID.randomUUID();

        AppResponse<Page<BrandResponseDto>> response = new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                messageSourceService.getMessageByKey("brands.retrieved.successfully"),
                getBrandResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(brandService.getBrandsByManufacturer(manufacturePublicId, page, size)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/brand/manufacture/" + manufacturePublicId)
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }
}