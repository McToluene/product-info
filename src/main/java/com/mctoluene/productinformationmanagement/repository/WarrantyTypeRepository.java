package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.WarrantyType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarrantyTypeRepository extends JpaRepository<WarrantyType, UUID> {
    Optional<WarrantyType> findByWarrantyTypeNameIgnoreCase(String warrantyTypeName);

    Optional<WarrantyType> findByPublicId(UUID publicId);

    Optional<WarrantyType> findByWarrantyTypeNameIgnoreCaseAndPublicIdNot(String warrantyTypeName, UUID publicId);

    PageImpl<WarrantyType> findAll(Pageable pageable);

}
