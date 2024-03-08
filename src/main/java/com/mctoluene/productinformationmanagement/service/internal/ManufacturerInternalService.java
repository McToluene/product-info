package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.model.Manufacturer;

import java.util.Optional;
import java.util.UUID;

public interface ManufacturerInternalService {

    Manufacturer saveNewManufacturer(Manufacturer manufacturer);

    Manufacturer saveNewManufacturerToDb(Manufacturer manufacturer);

    Optional<Manufacturer> findByPublicId(UUID publicId);

    Page<Manufacturer> findAllBy(Pageable pageable);

    Manufacturer updateManufacturer(Manufacturer manufacturer);

    Manufacturer deleteManufacturer(Manufacturer manufacturer);

    Manufacturer findByPublicIdAndManufacturer(UUID publicId, UpdateManufacturerRequestDto requestDto);

    Optional<Manufacturer> findByManufacturerName(String manufacturerName);

    Optional<Manufacturer> findById(UUID id);

    Page<Manufacturer> findAllByCriteria(String name, Pageable pageable);
}
