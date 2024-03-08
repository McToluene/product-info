package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.MeasuringUnitController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.MeasuringUnitResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.MeasuringUnitHelper;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MeasuringUnitService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
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
import org.springframework.context.annotation.Import;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MeasuringUnitController.class)
@Import(MeasuringUnitService.class)
public class MeasuringUnitControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    public MeasuringUnitService measuringUnitService;
    @MockBean
    public TraceService traceService;
    @Autowired
    public WebApplicationContext webApplicationContext;
    @MockBean
    public MessageSourceService messageSourceService;
    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    MeasuringUnitControllerTest() {
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext).build();
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void getMesauringUnitList() throws Exception {

        int page = 0;
        int size = 5;
        int count = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("list.of.measuring.units.retrieved.successfully"),
                messageSourceService.getMessageByKey("list.of.measuring.units.retrieved.successfully"),
                getMeasuringUnitResponse(pageRequest, count), null);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(measuringUnitService.getAllMeasuringUnits(page, size)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/measuring-unit")
                    .queryParam("page", String.valueOf(page))
                    .queryParam("size", String.valueOf(size))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private Page<MeasuringUnit> getMeasuringUnitResponse(PageRequest pageRequest, int count) {
        List<MeasuringUnit> measuringUnitsResponse = new ArrayList<>();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), measuringUnitsResponse.size());
        Page<MeasuringUnit> page = new PageImpl<>(measuringUnitsResponse.subList(start, end), pageRequest,
                measuringUnitsResponse.size());
        for (int i = 0; i <= count; i++) {

            measuringUnitsResponse.add(MeasuringUnit.builder()
                    .name("unit-" + i)
                    .status(Status.ACTIVE.name())
                    .description("describe")
                    .build());

        }
        return page;
    }

    @Test
    void getMeasuringUnit() throws Exception {

        MeasuringUnitResponseDto responseDto = getMeasuringUnitResponseDto();

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                messageSourceService.getMessageByKey("manufacturer.fetched.successfully"),
                responseDto, null);

        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(measuringUnitService.getMeasuringUnitById(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/measuring-unit/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("Nykaa"))
                    .andExpect(jsonPath("$.data.description").value("We serve Beauty products"));
        }

    }

    private static MeasuringUnitResponseDto getMeasuringUnitResponseDto() {
        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Nykaa")
                .description("We serve Beauty products")
                .abbreviation("nyka")
                .status(Status.ACTIVE.name())
                .build();

        return MeasuringUnitHelper.buildMeasuringUnitResponse(measuringUnit);
    }

    @Test
    void editMeasuringUnit() throws Exception {

        MeasuringUnitResponseDto responseDto = MeasuringUnitResponseDto.builder()
                .name("inches")
                .description("length")
                .abbreviation("abb")
                .status(Status.ACTIVE.name())
                .build();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("measuring.unit.updated.successfully"),
                messageSourceService.getMessageByKey("measuring.unit.updated.successfully"),
                responseDto, null);

        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("inch")
                .description("length")
                .abbreviation("abb")
                .modifiedBy("Dilip")
                .build();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            String inoutInJson = this.mapToJson(requestDto);

            UUID publicId = UUID.randomUUID();
            when(measuringUnitService.editMeasuringUnit(publicId, requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/measuring-unit/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    @Test
    void deleteBrand() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("measuring.unit.deleted.successfully"),
                messageSourceService.getMessageByKey("measuring.unit.deleted.successfully"),
                null, null);

        UUID publicId = UUID.randomUUID();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(measuringUnitService.deleteMeasuringUnit(publicId)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/measuring-unit/" + UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

}
