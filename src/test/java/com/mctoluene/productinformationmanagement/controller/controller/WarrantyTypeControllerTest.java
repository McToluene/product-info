package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.WarrantyTypeController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.WarrantyTypeHelper;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.productinformationmanagement.service.WarrantyTypeService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = WarrantyTypeController.class)
@Import(WarrantyTypeService.class)
class WarrantyTypeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public WarrantyTypeService warrantyTypeService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    WarrantyTypeControllerTest() {
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createWarrantyType() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("warranty.type.created.successfully"),
                messageSourceService.getMessageByKey("warranty.type.created.successfully"),
                null, null);

        CreateWarrantyTypeRequestDto requestDto = CreateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("Warranty type")
                .description("Warranty type description")
                .createdBy(UUID.randomUUID().toString())
                .build();

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(warrantyTypeService.createWarrantyType(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/warranty-type")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getWarrantyTypeByPublicId() throws Exception {
        WarrantyType responseDto = getResponseDto();
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.fetched.successfully"),
                messageSourceService.getMessageByKey("warranty.type.fetched.successfully"),
                responseDto, null);
        UUID publicId = UUID.randomUUID();
        UUID traceid = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(warrantyTypeService.getWarrantyTypeById(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(traceid);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/warranty-type/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", traceid))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void getWarrantyTypes() throws Exception {

        List<WarrantyType> warrantyTypes = List.of(getResponseDto());

        var warrantyTypePage = new PageImpl<>(warrantyTypes);
        Pageable pageable = PageRequest.of(0, 1);

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.types.fetched.successfully"),
                messageSourceService.getMessageByKey("warranty.types.fetched.successfully"),
                warrantyTypePage, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(warrantyTypeService.getAllWarrantyTypes(pageable.getPageNumber(), pageable.getPageSize()))
                    .thenReturn(response);

            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/v1/warranty-type/?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private WarrantyType getResponseDto() {

        WarrantyType warrantyType = new WarrantyType();

        warrantyType.setDescription("new description");
        warrantyType.setId(UUID.randomUUID());
        warrantyType.setPublicId(UUID.randomUUID());
        warrantyType.setWarrantyTypeName("new warranty");
        warrantyType.setStatus(Status.ACTIVE);
        warrantyType.setCreatedDate(LocalDateTime.now());
        warrantyType.setLastModifiedBy("now");
        warrantyType.setCreatedBy("test");
        warrantyType.setVersion(BigInteger.ZERO);
        warrantyType.setLastModifiedBy("test");

        return warrantyType;

    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void deleteWarrantyType() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.deleted.successfully"),
                messageSourceService.getMessageByKey("warranty.type.deleted.successfully"),
                null, null);

        UUID publicId = UUID.randomUUID();
        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(warrantyTypeService.deleteWarrantyType(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/warranty-type/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void editWarrantyType() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.updated.successfully"),
                messageSourceService.getMessageByKey("warranty.type.updated.successfully"),
                getWarrantyResponseDto(), null);

        UpdateWarrantyTypeRequestDto requestDto = UpdateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("warranty-1")
                .description("We serve beauty products")
                .lastModifiedBy("Anawarul")
                .build();

        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();
        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(warrantyTypeService.updateWarrantyType(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/warranty-type/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private static WarrantyTypeResponseDto getWarrantyResponseDto() {
        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName("Nykaa")
                .description("We serve Beauty products")
                .status(Status.ACTIVE)
                .build();

        return WarrantyTypeHelper.buildWarrantyTypeResponse(warrantyType);
    }

}