package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mctoluene.productinformationmanagement.model.MeasuringUnit;

import java.util.Optional;
import java.util.UUID;

public interface MeasuringUnitRepository extends JpaRepository<MeasuringUnit, UUID> {

    Optional<MeasuringUnit> findByNameIgnoreCase(String name);

    Optional<MeasuringUnit> findByAbbreviationIgnoreCase(String abbreviation);

    Optional<MeasuringUnit> findByPublicId(UUID publicId);
}
