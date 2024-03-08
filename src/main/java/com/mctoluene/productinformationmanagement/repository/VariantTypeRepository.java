package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.VariantType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantTypeRepository extends JpaRepository<VariantType, UUID> {

    Optional<VariantType> findByPublicId(UUID publicId);

    Optional<VariantType> findByPublicIdAndStatus(UUID publicId, String status);

    @Query(value = "select vt.* from variant_types vt where upper(vt.variant_type_name) = upper(:name) ", nativeQuery = true)
    Optional<VariantType> findByVariantTypeName(String name);

    Page<VariantType> findByVariantTypeNameContainingIgnoreCase(String searchParam, Pageable pageable);

    PageImpl<VariantType> findAllByStatus(String status, Pageable pageable);

    PageImpl<VariantType> findAll(Pageable pageable);

    Optional<VariantType> findByVariantTypeNameIgnoreCase(String variantTypeName);

}
