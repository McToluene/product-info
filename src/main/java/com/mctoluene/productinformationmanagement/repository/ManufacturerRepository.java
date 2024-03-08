package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.Manufacturer;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID> {

    Optional<Manufacturer> findByPublicId(UUID publicId);

    @Query(nativeQuery = true, value = "select * from manufacturers where" +
            " status IN('ACTIVE','INACTIVE') order by created_date desc")
    Page<Manufacturer> findAllBy(Pageable pageable);

    Optional<Manufacturer> findByManufacturerNameIgnoreCase(String manufacturerName);

    @Query(value = "SELECT m FROM Manufacturer m " +
            "WHERE (?1 IS NULL OR LOWER(m.manufacturerName) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (m.status IN ('ACTIVE', 'INACTIVE') OR m.status IS NULL)")
    Page<Manufacturer> findAllByCriteria(String manufacturerName, Pageable pageable);

}
