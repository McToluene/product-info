package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.CreateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.MeasuringUnitServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.MeasuringUnitInternalService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class MeasuringUnitServiceTest {

    @Mock
    private MeasuringUnitInternalService measuringUnitInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private MeasuringUnitServiceImpl measuringUnitService;

    @Test
    void createMeasuringUnitTest() {

        CreateMeasuringUnitRequestDto requestDto = CreateMeasuringUnitRequestDto.builder()
                .name("Kilogram")
                .description("KILO GRAM")
                .abbreviation("KG")
                .createdBy("creator")
                .build();

        MeasuringUnit responseDto = convertToResponseDto();
        when(measuringUnitInternalService.saveNewMeasuringUnit(any(MeasuringUnit.class))).thenReturn(responseDto);
        var createdResponse = measuringUnitService.createMeasuringUnit(requestDto);
        createdResponse.getMessage();
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("measuring.unit.created.successfully"));
    }

    private MeasuringUnit convertToResponseDto() {
        return MeasuringUnit.builder()
                .status(Status.ACTIVE.name())
                .build();
    }

    @Test
    void getMeasuringUnit() {
        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Nykaa")
                .description("We serve beauty products")
                .abbreviation("nyka")
                .status(Status.ACTIVE.name())
                .build();

        UUID publicId = UUID.randomUUID();
        given(measuringUnitInternalService.findByPublicId(publicId)).willReturn(measuringUnit);
        var createdResponse = measuringUnitService.getMeasuringUnitById(publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("measuring.units.fetched.successfully"));
    }

    @Test
    void getAllManufacturers() {
        int page = 0;
        int size = 5;
        int count = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        given(measuringUnitInternalService.findAllBy(any(Pageable.class)))
                .willReturn(getMeasuringUnitResponse(pageRequest, count));
        var createdResponse = measuringUnitService.getAllMeasuringUnits(page, size);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"));
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
    void editMeasuringUnit() {
        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("inch")
                .description("length")
                .abbreviation("abb")
                .modifiedBy("Dilip")
                .build();

        UUID publicId = UUID.randomUUID();
        MeasuringUnit responseDto = convertToResponseDto();

        given(measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(any(), any())).willReturn(responseDto);
        given(measuringUnitInternalService.updateMeasuringUnit(any())).willReturn(responseDto);

        var editResponse = measuringUnitService.editMeasuringUnit(publicId, requestDto);

        assertThat(editResponse).isNotNull();
        assertThat(editResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(editResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("measuring.unit.updated.successfully"));
    }

    @Test
    void deleteMeasuringUnit() {
        UUID publicId = UUID.randomUUID();

        MeasuringUnit responseDto = convertToResponseDto();

        given(measuringUnitInternalService.findByPublicId(publicId)).willReturn(responseDto);
        given(measuringUnitInternalService.deleteMeasuringUnit(any())).willReturn(responseDto);

        var deleteResponse = measuringUnitService.deleteMeasuringUnit(publicId);

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(deleteResponse.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("measuring.unit.deleted.successfully"));
    }
}
