package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ProductVariantController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.product.ApproveRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.RejectVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditLiveInventoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.EditVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.UpdateVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.VariantFilterRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.VariantCompleteResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.VariantResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.variantAwaitingApproval.VariantsAwaitingApprovalResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.VariantHelper;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantService;
import com.mctoluene.commons.response.AppResponse;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductVariantController.class)
@Import(VariantService.class)
class ProductVariantControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    public WebApplicationContext webApplicationContext;
    @MockBean
    public MessageSourceService messageSourceService;
    @MockBean
    public VariantService variantService;
    @MockBean
    public TraceService traceService;
    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    ProductVariantControllerTest() {
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void testGetVariantBySku() throws Exception {

        VariantResponseDto responseDto = new VariantResponseDto();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                responseDto, null);

        String sku = "testsku";

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.findVariantBySku(sku)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/by-sku/" + sku)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void findVariantBySku() throws Exception {

        VariantCompleteResponseDto responseDto = new VariantCompleteResponseDto();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                responseDto, null);

        String sku = "testsku";

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantBySku(sku)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/internal/" + sku)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void testGetVariantSearch() throws Exception {

        VariantResponseDto responseDto = new VariantResponseDto();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                messageSourceService.getMessageByKey("variant.retrieved.successfully"),
                responseDto, null);

        String searchValue = "Peak";

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.searchVariants(searchValue, 1, 10)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/search-variants/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID())
                    .param("page", "1")
                    .param("searchValue", searchValue)
                    .param("size", "10"))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantByPublicId() throws Exception {
        VariantVersion responseDto = getResponseDto();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDto, null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantByPublicId(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void approveVariantAwaitingApproval() throws Exception {
        ApproveRequestDto requestDto = new ApproveRequestDto();
        requestDto.setApprovedBy("app");

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.successfully.approved"),
                messageSourceService.getMessageByKey("variant.successfully.approved"),
                null, null);
        UUID variantPublicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.approveVariantAwaitingApproval(any(), any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            String inOutJson = this.mapToJson(requestDto);
            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/" + variantPublicId + "/approve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inOutJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void rejectVariantAwaitingApproval() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                messageSourceService.getMessageByKey("variant.successfully.rejected"),
                null, null);
        UUID variantPublicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.rejectVariantAwaitingApproval(any(), any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            RejectVariantRequestDto requestDto = new RejectVariantRequestDto();
            requestDto.setRejectedBy("admin@mail.com");
            requestDto.setRejectionReason("Incorrect data");

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/" + variantPublicId + "/reject")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID())
                    .content(mapToJson(requestDto)))
                    .andExpect(status().isOk());
        }

    }

    private VariantVersion getResponseDto() {

        ProductVariant productVariant = new ProductVariant();
        productVariant.setCreatedBy(UUID.randomUUID().toString());
        productVariant.setLastModifiedBy(UUID.randomUUID().toString());
        productVariant.setCreatedDate(LocalDateTime.now());
        productVariant.setLastModifiedDate(LocalDateTime.now());

        VariantVersion variant = new VariantVersion();
        variant.setId(UUID.randomUUID());
        variant.setVariantName("Variant Name one");
        variant.setVariantDescription("This variant name one");
        variant.setStatus(Status.ACTIVE.name());
        variant.setProductVariant(productVariant);
        variant.setVersion(BigInteger.ZERO);
        return variant;

    }

    private String mapListToArray(List<UUID> uuidList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(uuidList);
    }

    @Test
    void getVariantsByProductCategoryPublicId() throws Exception {
        UUID publicId = UUID.randomUUID();
        VariantResponseDto responseDto = new VariantResponseDto();
        List<VariantResponseDto> responseDtoList = List.of(responseDto);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDtoList, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantsByProductCategoryPublicId(publicId, 1, 10)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/by-product-category/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID())
                    .param("page", "1")
                    .param("size", "10"))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void testSearchVariantsByPublicIds() throws Exception {

        VariantResponseDto responseDto = new VariantResponseDto();
        List<VariantResponseDto> responseDtoList = List.of(responseDto);

        final var publicIdList = List.of(UUID.randomUUID());

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDtoList, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.searchProductVariantsByPublicIds("e", publicIdList)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/search-by-publicIds/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapToJson(publicIdList))
                    .header("x-trace-id", UUID.randomUUID())
                    .param("page", "1")
                    .param("size", "10"))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantsByProductPublicId() throws Exception {

        VariantVersion variant = VariantVersion.builder()
                .variantName("Adidas")
                .variantType(VariantType.builder().build())
                .costPrice(BigDecimal.TEN)
                .sku("sku12345")
                .variantDescription("We are sports manufacturer")
                .status(Status.ACTIVE.name())
                .build();

        UUID productPublicId = UUID.randomUUID();

        List<VariantVersion> variantList = List.of(variant, variant);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                variantList, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantsByProductPublicId(productPublicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/product/" + productPublicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void testGetRejectedVariants() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getRejectedVariants("s", "NGN", "2015-08-04", "2015-08-04", 0, 10))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/variant/rejected")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-country-code", "NGA")
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void testGetVariantsWithMissingImages() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                messageSourceService.getMessageByKey("variants.retrieved.success"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantsWithMissingImages("s", "2015-08-03", "2015-08-04", 0, 10))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/variant/missing-images")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "1")
                    .param("size", "5")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantsAwaitingApproval() throws Exception {

        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .variantName("Adidas")
                .variantType(VariantType.builder().build())
                .costPrice(BigDecimal.TEN)
                .sku("sku12345")
                .variantDescription("We are sports manufacturer")
                .status(Status.ACTIVE.name())
                .build();

        List<VariantAwaitingApproval> variantAwaitingApprovalList = List.of(variantAwaitingApproval,
                variantAwaitingApproval);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.list.fetched.successfully"),
                variantAwaitingApprovalList, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantsAwaitingApproval(
                    "ABD", "NGN", "2023-01-15", "2023-04-15", "PENDING",
                    List.of("ACTIVE"), 1, 5)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/awaiting-approval")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID())
                    .header("x-country-code", "NGA")
                    .param("page", "1")
                    .param("size", "5")
                    .param("searchParam", "ABD")
                    .param("startDate", "2023-01-15")
                    .param("endDate", "2023-04-15")
                    .param("approvalStatus", "PENDING")
                    .param("status", "ACTIVE"))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getAllVariants() throws Exception {
        Product product = Product.builder()
                .status("ACTIVE")
                .build();
        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("Dilip")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setProduct(product);
        variantVersion.setVariantType(VariantType.builder().build());
        variantVersion.setProductVariant(productVariant);
        List<VariantVersion> variantVersions = List.of(variantVersion);
        Page variantVersionPage = new PageImpl(variantVersions);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionPage, null);

        UUID publicId = UUID.randomUUID();

        List<String> listOfStatus = new ArrayList();
        listOfStatus.add("ACTIVE");

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getAllVariants("ABD", "NGN", "2023-01-15",
                    "2023-04-15", listOfStatus, 1, 5, null)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-country-code", "NGA")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void updateVariantAwaitingApproval() throws Exception {

        VariantsAwaitingApprovalResponseDto responseDto = VariantsAwaitingApprovalResponseDto.builder().build();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                responseDto, null);

        EditVariantAwaitingApprovalRequestDto requestDto = EditVariantAwaitingApprovalRequestDto.builder()
                .variantName("Variant")
                .variantDescription("Description")
                .variantTypeId(UUID.randomUUID())
                .costPrice(BigDecimal.valueOf(5000))
                .defaultImageUrl("www.example.url.com")
                .lastModifiedBy("Dilip")
                .build();

        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.editVariantAwaitingApproval(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/awaiting-approval/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void archiveProductVariant() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                messageSourceService.getMessageByKey("product.variant.archived.successfully"),
                null, null);

        UUID productVariantPublicId = UUID.randomUUID();
        UUID traceId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.archiveProductVariant(productVariantPublicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(traceId);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/archive/" + productVariantPublicId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", traceId))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void unArchiveProductVariant() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                messageSourceService.getMessageByKey("product.variant.unarchived.successfully"),
                null, null);

        UUID productVariantPublicId = UUID.randomUUID();
        UUID traceId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.unarchiveProductVariant(productVariantPublicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(traceId);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/unarchive/" + productVariantPublicId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", traceId))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getAllVariantsByCategoryPublicIds() throws Exception {

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
                .status("ACTIVE")
                .productCategory(productCategory)
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());

        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("Dilip")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setProduct(product);
        variantVersion.setVariantType(VariantType.builder().build());
        variantVersion.setProductVariant(productVariant);

        List<VariantVersion> variantVersions = List.of(variantVersion);
        Page variantVersionPage = new PageImpl(variantVersions);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                variantVersionPage, null);

        UUID publicId = UUID.randomUUID();

        List<String> listOfStatus = new ArrayList();
        listOfStatus.add("ACTIVE");

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getAllVariantsByCategoryPublicIds("ABD", List.of(categoryPublicId), "2023-01-15",
                    "2023-04-15", listOfStatus, 1, 5)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            List<UUID> listOfCategoryPublicIds = new ArrayList<>();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/byCategoryPublicIds")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(listOfCategoryPublicIds.toString())
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantsByPublicIdsAndStatus() throws Exception {

        List<UUID> publicIdList = List.of(UUID.randomUUID(), UUID.randomUUID());
        String list = this.mapListToArray(publicIdList);
        VariantResponseDto responseDto = new VariantResponseDto();
        List<VariantResponseDto> responseDtoList = List.of(responseDto);

        VariantFilterRequestDto requestDto = VariantFilterRequestDto.builder()
                .variantPublicIds(publicIdList)
                .status("ACTIVE")
                .categoryPublicIds(List.of(UUID.randomUUID()))
                .build();
        String inoutInJson = this.mapToJson(requestDto);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDtoList, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getProductVariantsByPublicIdAndStatusAndFilter(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/get-by-publicIds/filter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID())
                    .content(inoutInJson))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getAllVariantsByCategoryPublicIdMap() throws Exception {

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
                .status("ACTIVE")
                .productCategory(productCategory)
                .build();
        product.setId(UUID.randomUUID());
        product.setBrand(new Brand());
        product.setPublicId(UUID.randomUUID());
        product.setLastModifiedBy("ME");

        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("Dilip")
                .product(product)
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();

        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setProduct(product);
        variantVersion.setVariantName("var1");
        variantVersion.setVariantType(VariantType.builder().build());
        variantVersion.setProductVariant(productVariant);

        Map<UUID, BigDecimal> markUpMap = new HashMap<>();
        markUpMap.put(categoryPublicId, BigDecimal.valueOf(20));

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                List.of(VariantHelper.buildCompleteVariantMarkupResponse(variantVersion, markUpMap)), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getAllVariantsByCategoryPublicIdsMap(markUpMap)).thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/byCategoriesMap")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.mapToJson(markUpMap))
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantByVariantAwaitingPublicId() throws Exception {
        VariantVersion responseDto = getResponseDto();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                responseDto, null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getVariantAwaitingByPublicId(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/variant-awaiting/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantBySkuListAndProductName() throws Exception {
        Integer page = 1;
        Integer size = 2;
        UUID traceId = UUID.randomUUID();
        Product product = Product.builder()
                .status("ACTIVE")
                .productName("test")
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder().build();
        variantVersion.setId(UUID.randomUUID());
        variantVersion.setStatus("ACTIVE");
        variantVersion.setSku("PEN-01");
        variantVersion.setProduct(product);
        List<VariantVersion> variantVersions = List.of(variantVersion);
        Page variantVersionPage = new PageImpl(variantVersions);
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.version.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.version.fetched.successfully"),
                variantVersionPage, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.searchVariantBySkuListAndProductName(List.of("PEN-01"), "test", page, size))
                    .thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(traceId);
            String inOutJson = this.mapToJson(List.of("PEN-01"));
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/search-by-sku-product-name")
                    .content(inOutJson)
                    .param("searchValue", "test")
                    .param("page", page.toString())
                    .param("size", size.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", traceId))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getStockOneProducts() throws Exception {

        UUID traceId = UUID.randomUUID();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("stockOne.products.fetched.successfully"),
                messageSourceService.getMessageByKey("stockOne.products.fetched.successfully"),
                null, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getProductsFromStockOne(traceId.toString(), "test", "", 1, 1)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant/stockOneGetProducts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("warehouseName", "test")
                    .header("x-trace-id", traceId))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void editLiveInventory() throws Exception {

        VariantsAwaitingApprovalResponseDto responseDto = VariantsAwaitingApprovalResponseDto.builder().build();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.require.approval"),
                messageSourceService.getMessageByKey("variant.require.approval"),
                responseDto, null);

        EditLiveInventoryRequestDto requestDto = EditLiveInventoryRequestDto.builder()
                .sku("sku")
                .leadTime(8)
                .threshold(6000)
                .modifiedBy("Dilip")
                .build();

        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.editThresholdAndLeadTime(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/live-inventory")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void updateVariantTest() throws Exception {
        UpdateVariantRequestDto requestDto = new UpdateVariantRequestDto();
        requestDto.setVariantTypePublicId(UUID.randomUUID());
        requestDto.setVariantName("name");
        requestDto.setCostPrice(BigDecimal.ONE);
        requestDto.setModifiedBy("test");
        requestDto.setCountryPublicId(UUID.randomUUID());

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                messageSourceService.getMessageByKey("variant.updated.successfully"),
                null, null);
        String inputJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.updateVariant(any(), any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant/{publicId}", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inputJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantsByPublicIds() throws Exception {

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                null, null);

        String payload = this.mapToJson(List.of(UUID.randomUUID().toString()));
        doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantService.getProductVariantsByPublicIds(anyList())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/get-by-publicIds/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantsBySkuList() throws Exception {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                null, null);

        String payload = this.mapToJson(List.of("sku1"));

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(variantService.getVariantsBySkuList(anyList())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/get-by-sku-list/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantIdsBySkuListTest() throws Exception {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                null, null);

        String payload = this.mapToJson(List.of("sku1"));

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(variantService.getVariantIdsBySkuList(anyList())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/get-by-sku-list/ids")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getApprovedAndUnApprovedVariantIdsBySkuList() throws Exception {
        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.fetched.successfully"),
                null, null);

        String payload = this.mapToJson(List.of("sku1"));

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            when(variantService.getApprovedAndUnApprovedVariantIdsBySkuList(anyList())).thenReturn(appResponse);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant/get-by-sku-list/approved/unapproved/ids")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }
}