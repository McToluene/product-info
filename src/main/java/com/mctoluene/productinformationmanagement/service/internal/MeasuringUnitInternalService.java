package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;

import java.util.UUID;

public interface MeasuringUnitInternalService {

    MeasuringUnit saveNewMeasuringUnit(MeasuringUnit measuringUnit);

    MeasuringUnit saveNewMeasuringUnitToDb(MeasuringUnit measuringUnit);

    MeasuringUnit findByPublicId(UUID publicId);

    Page<MeasuringUnit> findAllBy(Pageable pageable);

    MeasuringUnit updateMeasuringUnit(MeasuringUnit measuringUnit);

    MeasuringUnit findByPublicIdAndMeasuringUnitName(UUID publicId, UpdateMeasuringUnitRequestDto requestDto);

    MeasuringUnit deleteMeasuringUnit(MeasuringUnit measuringUnit);

    MeasuringUnit findByName(String name);

}
