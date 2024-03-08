package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ManufacturerController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.ManufacturerHelper;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.ManufacturerService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManufacturerController.class)
@Import(ManufacturerService.class)
class ManufacturerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public ManufacturerService manufacturerService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    ManufacturerControllerTest() {
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createManufacturer() throws Exception {

        CreateManufacturerRequestDto requestDto = CreateManufacturerRequestDto.builder()
                .manufacturerName("mctoluene")
                .description("we are giant")
                .createdBy("Khushboo")
                .build();

        AppResponse<ManufacturerResponseDto> expectedResponse = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("Manufacturer.created.successfully"),
                messageSourceService.getMessageByKey("Manufacturer.created.successfully"),
                getManufacturerResponseDto(), null);

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.createManufacturer(requestDto)).thenReturn(expectedResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            AppResponse<ManufacturerResponseDto> actual = manufacturerService.createManufacturer(requestDto);
            actual.getMessage();
            assertThat(actual).isNotNull();

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/manufacturer")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isCreated());
        }

    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void getManufacturer() throws Exception {

        ManufacturerResponseDto responseDto = getManufacturerResponseDto();

        AppResponse<ManufacturerResponseDto> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                responseDto, null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            UUID publicId = UUID.randomUUID();
            when(manufacturerService.getManufacturer(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/manufacturer/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.manufacturerName").value("Nykaa"))
                    .andExpect(jsonPath("$.data.description").value("We serve Beauty products"));
        }

    }

    private static ManufacturerResponseDto getManufacturerResponseDto() {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve Beauty products")
                .status(Status.ACTIVE)
                .build();

        return ManufacturerHelper.buildManufacturerResponse(manufacturer);
    }

    @Test
    void getManufacturerList() throws Exception {

        int page = 0;
        int size = 5;
        int count = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        AppResponse<Page<ManufacturerResponseDto>> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"),
                getManufacturerResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.getAllManufacturers(page, size)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/manufacturer")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private Page<ManufacturerResponseDto> getManufacturerResponse(PageRequest pageRequest, int count) {
        List<ManufacturerResponseDto> manufacturerResponse = new ArrayList<>();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), manufacturerResponse.size());
        Page<ManufacturerResponseDto> page = new PageImpl<>(manufacturerResponse.subList(start, end), pageRequest,
                manufacturerResponse.size());
        for (int i = 0; i <= count; i++) {
            var manufacturer = Manufacturer.builder()
                    .manufacturerName("Manufacturer-" + i)
                    .status(Status.ACTIVE)
                    .description("Nykaa has fashion product also")
                    .build();
            manufacturerResponse.add(ManufacturerHelper.buildManufacturerResponse(manufacturer));
        }
        return page;
    }

    @Test
    void editManufacturer() throws Exception {
        AppResponse<ManufacturerResponseDto> response = new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.updated.successfully"),
                messageSourceService.getMessageByKey("manufacturer.updated.successfully"),
                getManufacturerResponseDto(), null);

        UpdateManufacturerRequestDto requestDto = UpdateManufacturerRequestDto.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .lastModifiedBy("Anawarul")
                .build();

        String inoutInJson = this.mapToJson(requestDto);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.updateManufacturer(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/manufacturer/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteManufcaturer() throws Exception {
        AppResponse<Void> response = new AppResponse<>(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("manufacturer.deleted.successfully"),
                messageSourceService.getMessageByKey("manufacturer.deleted.successfully"),
                null, null);

        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.deleteManufacturer(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/manufacturer/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void disableManufacturerStatus() throws Exception {
        AppResponse<ManufacturerResponseDto> appResponse = new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.status.disabled.successfully"),
                messageSourceService.getMessageByKey("manufacturer.status.disabled.successfully"),
                null,
                null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.disableManufacturerStatus(publicId)).thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manufacturer/" + publicId + "/disable")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andReturn();
        }

    }

    @Test
    void enableManufacturerStatus() throws Exception {
        AppResponse<ManufacturerResponseDto> appResponse = new AppResponse<>(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("manufacturer.status.enabled.successfully"),
                messageSourceService.getMessageByKey("manufacturer.status.enabled.successfully"),
                null,
                null);
        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.enableManufacturerStatus(publicId)).thenReturn(appResponse);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/manufacturer/" + publicId + "/enable")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andReturn();
        }

    }

    @Test
    void createManufacturerWithoutNameTest() throws Exception {

        AppResponse response = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "field should not be empty",
                "field should not be empty",
                null, List.of("manufacturer name must be provided"));

        CreateManufacturerRequestDto requestDto = new CreateManufacturerRequestDto();

        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(manufacturerService.createManufacturer(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/manufacturer")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isBadRequest());
        }

    }

}