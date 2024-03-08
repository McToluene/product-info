package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.measuringunit.CreateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.commons.response.AppResponse;

import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;

public interface MeasuringUnitService {

    AppResponse createMeasuringUnit(CreateMeasuringUnitRequestDto measuringUnitRequestDto);

    AppResponse getMeasuringUnitById(UUID publicId);

    AppResponse getAllMeasuringUnits(Integer page, Integer size);

    AppResponse editMeasuringUnit(UUID publicId, UpdateMeasuringUnitRequestDto requestDto);

    AppResponse deleteMeasuringUnit(UUID publicId);

    ByteArrayResource download(Integer page, Integer size);
}
