package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.repository.WarrantyTypeRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.WarrantyTypeInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarrantyTypeInternalServiceImpl implements WarrantyTypeInternalService {

    private final WarrantyTypeRepository warrantyTypeRepository;
    private final MessageSourceService messageSourceService;

    @Override
    public WarrantyType saveWarrantyTypeToDb(WarrantyType warrantyType) {
        Optional<WarrantyType> optionalWarrantyType = warrantyTypeRepository
                .findByWarrantyTypeNameIgnoreCase(warrantyType.getWarrantyTypeName());
        if (optionalWarrantyType.isPresent())
            throw new ValidatorException(messageSourceService.getMessageByKey("warranty.type.name.not.unique"));
        return warrantyTypeRepository.save(warrantyType);
    }

    public WarrantyType saveNewWarrantyTypeToDb(WarrantyType warrantyType) {
        try {
            return warrantyTypeRepository.save(warrantyType);
        } catch (Exception e) {
            log.info("in catch block ", e.getMessage());
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("could.not.process.request"));
        }
    }

    private WarrantyType saveWarrantyType(WarrantyType warrantyType) {
        try {
            return warrantyTypeRepository.save(warrantyType);
        } catch (Exception e) {
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("warranty.type.not.found"));
        }
    }

    @Override
    public WarrantyType findByPublicId(UUID publicId) {
        return warrantyTypeRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("warranty.type.not.found")));
    }

    @Override
    public WarrantyType deleteWarrantyType(WarrantyType warrantyType) {
        return saveWarrantyType(warrantyType);
    }

    @Override
    public WarrantyType findWarrantyTypeByPublicId(UUID publicId) {
        return warrantyTypeRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(
                        messageSourceService.getMessageByKey("warranty.type.not.found")));
    }

    @Override
    public WarrantyType updateWarrantyType(WarrantyType warrantyType) {
        log.info("About to update warranty type {}", warrantyType);
        try {
            return saveNewWarrantyTypeToDb(warrantyType);
        } catch (Exception e) {
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("warranty.type.already.exists"));
        }
    }

    @Override
    public WarrantyType findByPublicIdAndWarrantyType(UUID publicId, UpdateWarrantyTypeRequestDto requestDto) {
        Optional<WarrantyType> m1 = warrantyTypeRepository
                .findByWarrantyTypeNameIgnoreCaseAndPublicIdNot(requestDto.getWarrantyTypeName(), publicId);

        if (m1.isPresent()) {
            throw new ValidatorException(messageSourceService
                    .getMessageByKey("warrantyType.Name.already.exists.Try.with.different.WarrantyType.name"));
        } else {
            return warrantyTypeRepository.findByPublicId(publicId)
                    .orElseThrow(() -> new ModelNotFoundException(messageSourceService
                            .getMessageByKey("warranty.type.not.found")));
        }
    }

    @Override
    public PageImpl<WarrantyType> getAllWarrantyType(Pageable pageable) {
        return warrantyTypeRepository.findAll(pageable);
    }

}
