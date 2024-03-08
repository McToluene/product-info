package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.repository.VariantTypeRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.VariantTypeInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantTypeInternalServiceImpl implements VariantTypeInternalService {

    private final MessageSourceService messageSourceService;

    private final VariantTypeRepository variantTyeRepository;

    @Override
    public VariantType saveVariantTypeToDb(VariantType variantType) {
        try {
            return variantTyeRepository.save(variantType);
        } catch (Exception e) {
            throw new UnProcessableEntityException("Could not process request");
        }
    }

    @Override
    public VariantType saveNewVariantType(VariantType variantType) {
        log.info("about to save new variant type");
        return saveVariantTypeToDb(variantType);
    }

    @Override
    public VariantType findVariantTypeByPublicId(UUID publicId) {
        return variantTyeRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("variant.type.not.found")));
    }

    @Override
    public VariantType findVariantTypeByName(String name) {
        return variantTyeRepository.findByVariantTypeName(name).orElse(null);
    }

    @Override
    public boolean checkIfNameExist(String name) {
        var response = variantTyeRepository.findByVariantTypeName(name);
        return response.isPresent();
    }

    @Override
    public PageImpl<VariantType> getAllVariantTypes(Pageable pageable) {
        return variantTyeRepository.findAll(pageable);
    }

    @Override
    public Page<VariantType> getAllVariantTypesFiltered(String searchParam, Pageable pageable) {
        return variantTyeRepository.findByVariantTypeNameContainingIgnoreCase(searchParam, pageable);
    }

    @Transactional
    @Override
    public VariantType updateExistingVariantType(VariantType variantType) {
        log.info("about to update variant type {}", variantType.getVariantTypeName());
        return saveVariantTypeToDb(variantType);
    }

    @Transactional
    @Override
    public VariantType deleteVariantType(VariantType variantType) {
        log.info("about to delete variant type {}", variantType.getId());
        return saveVariantTypeToDb(variantType);
    }

    @Override
    public VariantType findVariantTypeById(UUID id) {
        return variantTyeRepository.findById(id).orElseThrow(() -> new ModelNotFoundException(
                messageSourceService.getMessageByKey("variant.type.not.found")));
    }

    @Override
    public Optional<VariantType> findVariantTypeByNameIgnoreCase(String variantTypeName) {
        return variantTyeRepository.findByVariantTypeNameIgnoreCase(variantTypeName);
    }
}
