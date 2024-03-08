package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.CreateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.MeasuringUnitResponseDto;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;

public class MeasuringUnitHelper {

    public MeasuringUnitHelper() {
    }

    public static MeasuringUnit buildMeasuringUnitEntity(CreateMeasuringUnitRequestDto requestDto) {

        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name(requestDto.getName().trim())
                .abbreviation(requestDto.getAbbreviation())
                .description(requestDto.getDescription())
                .status(Status.ACTIVE.name()).build();

        measuringUnit.setId(UUID.randomUUID());
        measuringUnit.setPublicId(UUID.randomUUID());
        measuringUnit.setVersion(BigInteger.ZERO);
        measuringUnit.setCreatedBy(requestDto.getCreatedBy());
        measuringUnit.setCreatedDate(LocalDateTime.now());
        return measuringUnit;
    }

    public static MeasuringUnitResponseDto buildMeasuringUnitResponse(MeasuringUnit measuringUnit) {

        return MeasuringUnitResponseDto.builder()
                .publicId(measuringUnit.getPublicId())
                .abbreviation(measuringUnit.getAbbreviation())
                .name(measuringUnit.getName().trim())
                .createdDate(measuringUnit.getCreatedDate())
                .description(measuringUnit.getDescription())
                .createdBy(measuringUnit.getCreatedBy())
                .lastModifiedBy(measuringUnit.getLastModifiedBy())
                .lastModifiedDate(measuringUnit.getLastModifiedDate())
                .status(measuringUnit.getStatus())
                .version(measuringUnit.getVersion())
                .build();
    }

}
