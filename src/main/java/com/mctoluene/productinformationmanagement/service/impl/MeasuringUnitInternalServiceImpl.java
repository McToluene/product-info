package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;
import com.mctoluene.productinformationmanagement.repository.MeasuringUnitRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.MeasuringUnitInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MeasuringUnitInternalServiceImpl implements MeasuringUnitInternalService {

    private final MessageSourceService messageSourceService;

    private final MeasuringUnitRepository measuringUnitRepository;

    @Override
    public MeasuringUnit saveNewMeasuringUnit(MeasuringUnit measuringUnit) {
        log.info("About to create MeasuringUnit {}", measuringUnit);
        Optional<MeasuringUnit> m1 = measuringUnitRepository.findByNameIgnoreCase(measuringUnit.getName());
        if (m1.isPresent()) {
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.already.exists"));
        }
        Optional<MeasuringUnit> m2 = measuringUnitRepository.findByAbbreviationIgnoreCase(measuringUnit.getName());
        if (m2.isPresent()) {
            throw new ValidatorException(
                    messageSourceService.getMessageByKey("measuring.unit.abbreviation.already.exists"));
        }
        return saveNewMeasuringUnitToDb(measuringUnit);
    }

    @Override
    public MeasuringUnit saveNewMeasuringUnitToDb(MeasuringUnit measuringUnit) {
        try {
            return measuringUnitRepository.save(measuringUnit);
        } catch (Exception e) {
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.already.exists"));
        }
    }

    @Override
    public MeasuringUnit findByPublicId(UUID publicId) {
        return measuringUnitRepository.findByPublicId(publicId).orElseThrow(
                () -> new ModelNotFoundException(messageSourceService.getMessageByKey("Measuring.unit.not.found")));
    }

    @Override
    public Page<MeasuringUnit> findAllBy(Pageable pageable) {
        return measuringUnitRepository.findAll(pageable);
    }

    @Override
    public MeasuringUnit updateMeasuringUnit(MeasuringUnit measuringUnit) {
        try {
            return saveNewMeasuringUnitToDb(measuringUnit);
        } catch (Exception e) {
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.already.exists"));
        }
    }

    @Override
    public MeasuringUnit findByPublicIdAndMeasuringUnitName(UUID publicId, UpdateMeasuringUnitRequestDto requestDto) {

        Optional<MeasuringUnit> measuringUnit = measuringUnitRepository.findByNameIgnoreCase(requestDto.getName());
        if (measuringUnit.isPresent() && !measuringUnit.get().getPublicId().equals(publicId))
            throw new ValidatorException(messageSourceService.getMessageByKey("measuring.unit.already.exists"));

        Optional<MeasuringUnit> measuringUnitExist = measuringUnitRepository
                .findByAbbreviationIgnoreCase(requestDto.getAbbreviation());
        if (measuringUnitExist.isPresent() && !measuringUnitExist.get().getPublicId().equals(publicId))
            throw new ValidatorException(messageSourceService.getMessageByKey("abbreviation.already.exists"));

        return measuringUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("Measuring.unit.not.found")));
    }

    @Override
    public MeasuringUnit deleteMeasuringUnit(MeasuringUnit measuringUnit) {
        return saveNewMeasuringUnitToDb(measuringUnit);
    }

    @Override
    public MeasuringUnit findByName(String name) {
        return measuringUnitRepository
                .findByNameIgnoreCase(name)
                .orElseGet(this::fetchDefaultMeasuringUnit);
    }

    private MeasuringUnit fetchDefaultMeasuringUnit() {
        return measuringUnitRepository
                .findByNameIgnoreCase("KG")
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("Measuring.unit.not.found")));
    }

}
