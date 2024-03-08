package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.repository.ManufacturerRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ManufacturerInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManufacturerInternalServiceImpl implements ManufacturerInternalService {

    private final MessageSourceService messageSourceService;
    private final ManufacturerRepository manufacturerRepository;
    private static final String ALREADYEXISTKEY = "manufacturer.already.exists";

    @Override
    public Manufacturer saveNewManufacturer(Manufacturer manufacturer) {
        log.info("About to create manufacturer {}", manufacturer);
        manufacturerRepository
                .findByManufacturerNameIgnoreCase(manufacturer.getManufacturerName())
                .ifPresent(alreadyExist -> {
                    throw new ValidatorException(messageSourceService
                            .getMessageByKey("manufacturer.already.exists"));
                });

        return saveNewManufacturerToDb(manufacturer);

    }

    @Override
    public Manufacturer saveNewManufacturerToDb(Manufacturer manufacturer) {
        try {
            return manufacturerRepository.save(manufacturer);
        } catch (Exception e) {
            log.error("error occurred", e);
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("manufacturer.already.exists"));
        }
    }

    @Override
    public Optional<Manufacturer> findByPublicId(UUID publicId) {
        return Optional.ofNullable(manufacturerRepository.findByPublicId(publicId).orElseThrow(
                () -> new ModelNotFoundException("Manufacturer not found")));
    }

    @Override
    public Page<Manufacturer> findAllBy(Pageable pageable) {
        return manufacturerRepository.findAllBy(pageable);

    }

    @Override
    public Manufacturer updateManufacturer(Manufacturer manufacturer) {
        log.info("About to update manufacturer {}", manufacturer);
        try {
            return saveNewManufacturerToDb(manufacturer);
        } catch (Exception e) {
            log.error("Erro occured ", e);
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("manufacturer.already.exists"));
        }

    }

    @Override
    public Manufacturer deleteManufacturer(Manufacturer manufacturer) {
        return saveNewManufacturerToDb(manufacturer);
    }

    @Override
    public Manufacturer findByPublicIdAndManufacturer(UUID publicId, UpdateManufacturerRequestDto requestDto) {
        Optional<Manufacturer> conflictingManufacturer = manufacturerRepository
                .findByManufacturerNameIgnoreCase(requestDto.getManufacturerName().trim());

        conflictingManufacturer.ifPresent(manufacturer -> {
            if (manufacturer.getPublicId() != publicId) {
                throw new ValidatorException(messageSourceService
                        .getMessageByKey("manufacturerName.already.exists.Try.with.different.manufacturerName"));
            }
        });
        return manufacturerRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("manufacturer.not.found")));
    }

    @Override
    public Optional<Manufacturer> findByManufacturerName(String manufacturerName) {
        return manufacturerRepository.findByManufacturerNameIgnoreCase(manufacturerName);
    }

    @Override
    public Optional<Manufacturer> findById(UUID uuid) {
        return manufacturerRepository.findById(uuid);
    }

    @Override
    public Page<Manufacturer> findAllByCriteria(String name, Pageable pageable) {
        return manufacturerRepository.findAllByCriteria(name, pageable);
    }

}
