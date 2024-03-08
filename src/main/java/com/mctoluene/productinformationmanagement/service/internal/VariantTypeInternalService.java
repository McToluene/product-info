package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.model.VariantType;

import java.util.Optional;
import java.util.UUID;

public interface VariantTypeInternalService {
    VariantType saveVariantTypeToDb(VariantType variantType);

    VariantType saveNewVariantType(VariantType variantType);

    VariantType findVariantTypeByPublicId(UUID publicId);

    VariantType findVariantTypeByName(String name);

    boolean checkIfNameExist(String name);

    PageImpl<VariantType> getAllVariantTypes(Pageable pageable);

    Page<VariantType> getAllVariantTypesFiltered(String searchParam, Pageable pageable);

    @Transactional
    VariantType updateExistingVariantType(VariantType variantType);

    @Transactional
    VariantType deleteVariantType(VariantType variantType);

    VariantType findVariantTypeById(UUID id);

    Optional<VariantType> findVariantTypeByNameIgnoreCase(String variantTypeName);
}
