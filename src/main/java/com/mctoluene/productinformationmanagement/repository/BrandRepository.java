package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    Optional<Brand> findByPublicId(UUID publicId);

    Optional<Brand> findByBrandNameIgnoreCase(String brandName);

    @Query(value = "select * from brands where status IN('ACTIVE', 'INACTIVE')", nativeQuery = true)
    Page<Brand> findAllBy(Pageable pageable);

    Page<Brand> findByManufacturer(Manufacturer manufacturer, Pageable pageable);

    @Query(value = "SELECT b FROM Brand b " +
            "WHERE (COALESCE(?1, '') = '' OR LOWER(b.brandName) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND b.manufacturer = ?2 " +
            "AND (b.status IN ('ACTIVE', 'INACTIVE') OR b.status IS NULL)")
    Page<Brand> findAllByCriteria(String brandName, Manufacturer manufacturer, Pageable pageable);

    @Query(value = "SELECT b FROM Brand b " +
            "WHERE (COALESCE(?1, '') = '' OR LOWER(b.brandName) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (b.status IN ('ACTIVE', 'INACTIVE') OR b.status IS NULL)")
    Page<Brand> findAllByCriteria(String brandName, Pageable pageable);

    Optional<Brand> findByBrandNameAndManufacturer(String brandName, Manufacturer manufacturer);
}
