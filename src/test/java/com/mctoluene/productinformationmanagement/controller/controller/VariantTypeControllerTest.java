package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.VariantTypeController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.UpdateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.VariantTypeService;
import com.mctoluene.productinformationmanagement.service.internal.VariantTypeInternalService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = VariantTypeController.class)
@Import(VariantTypeService.class)
class VariantTypeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public VariantTypeService variantTypeService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;
    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    @MockBean
    public VariantTypeInternalService variantTypeInternalService;

    VariantTypeControllerTest() {
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
    void createVariantType() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("variant.type.created.successfully"),
                messageSourceService.getMessageByKey("variant.type.created.successfully"),
                null, null);
        CreateVariantTypeRequestDto requestDto = CreateVariantTypeRequestDto.builder()
                .variantTypeName("Variant type")
                .description("This is Test")
                .createdBy("Ugo")
                .build();
        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.createVariantType(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant-type")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isCreated());
        }

    }

    @Test
    void updateVariantType() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.updated.successfully"),
                messageSourceService.getMessageByKey("variant.type.updated.successfully"),
                null, null);
        UpdateVariantTypeRequestDto requestDto = UpdateVariantTypeRequestDto.builder()
                .variantTypeName("Variant type")
                .description("This is Test")
                .lastModifiedBy("Ugo")
                .build();
        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.updateVariantType(requestDto, publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/variant-type/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantTypeByPublicId() throws Exception {
        VariantType responseDto = getResponseDto();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                responseDto, null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.getVariantTypeById(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant-type/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteVariantType() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.deleted.successfully"),
                messageSourceService.getMessageByKey("variant.type.deleted.successfully"),
                null, null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.deleteVariantType(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/variant-type/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getVariantTypesTest() throws Exception {

        VariantType responseDto = getResponseDto();
        List<VariantType> variantTypes = new ArrayList<>();
        variantTypes.add(responseDto);
        var variantTypesPage = new PageImpl<>(variantTypes);
        Pageable pageable = PageRequest.of(0, 1);
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                variantTypesPage, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.getAllVariantTypes("", pageable.getPageNumber(), pageable.getPageSize()))
                    .thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/variant-type?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void createVariantTypeWithoutNameTest() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "field should not be empty",
                "field should not be empty",
                null, List.of("variant type name cannot be empty"));
        CreateVariantTypeRequestDto requestDto = new CreateVariantTypeRequestDto();

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(variantTypeService.createVariantType(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/variant-type")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isBadRequest());
        }

    }

    @Test
    void searchByVariantTypeName() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                getResponseDto(), null);

        var name = "teste-search";

        when(variantTypeService.getVariantTypeByName(name)).thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/variant-type/by-name")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("name", name)
                .header("x-trace-id", UUID.randomUUID()))
                .andExpect(status().isOk());

    }

    private VariantType getResponseDto() {
        VariantType variantType = new VariantType();
        variantType.setPublicId(UUID.randomUUID());
        variantType.setVariantTypeName("Variant Name one");
        variantType.setDescription("This variant name one");
        variantType.setStatus(Status.ACTIVE.name());
        variantType.setCreatedBy(UUID.randomUUID().toString());
        variantType.setLastModifiedBy(UUID.randomUUID().toString());
        variantType.setCreatedDate(LocalDateTime.now());
        variantType.setLastModifiedDate(LocalDateTime.now());
        variantType.setVersion(BigInteger.ZERO);
        return variantType;

    }
}
