package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.VariantLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantLocationRepository extends JpaRepository<VariantLocation, UUID> {
    Optional<VariantLocation> findByVariantPublicIdAndLocationPublicIdAndStatus(UUID variantPublicId,
            UUID locationPublicId, String status);

    List<VariantLocation> findAllByVariantPublicIdAndStatus(UUID variantPublicId, String status);
}
