package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponse;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.filter.search.WarrantyTypeFilter;
import com.mctoluene.productinformationmanagement.helper.WarrantyTypeHelper;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.WarrantyTypeService;
import com.mctoluene.productinformationmanagement.service.internal.WarrantyTypeInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarrantyTypeServiceImpl implements WarrantyTypeService {

    private final WarrantyTypeInternalService warrantyTypeInternalService;

    private final MessageSourceService messageSourceService;

    @Override
    public AppResponse createWarrantyType(CreateWarrantyTypeRequestDto requestDto) {
        WarrantyType warrantyType = WarrantyTypeHelper.buildWarrantyTypeEntity(requestDto);
        warrantyType = warrantyTypeInternalService.saveWarrantyTypeToDb(warrantyType);

        WarrantyTypeResponseDto responseDto = WarrantyTypeHelper.buildWarrantyTypeResponse(warrantyType);
        log.info("Warranty type created successfully {}", responseDto);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("warranty.type.created.successfully"),
                messageSourceService.getMessageByKey("warranty.type.created.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse deleteWarrantyType(UUID publicId) {
        WarrantyType warrantyType = warrantyTypeInternalService.findByPublicId(publicId);
        if (warrantyType.getStatus().equals(Status.DELETED)) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("warranty.type.not.found"));
        }
        warrantyType.setStatus(Status.DELETED);
        warrantyType.setWarrantyTypeName(warrantyType.getWarrantyTypeName() + LocalDateTime.now());

        warrantyTypeInternalService.deleteWarrantyType(warrantyType);

        log.info("Warranty type deleted successfully");
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.deleted.successfully"),
                messageSourceService.getMessageByKey("warranty.type.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse getWarrantyTypeById(UUID publicId) {

        WarrantyType warrantyType = warrantyTypeInternalService.findWarrantyTypeByPublicId(publicId);
        WarrantyTypeResponseDto responseDto = WarrantyTypeHelper.buildWarrantyTypeResponse(warrantyType);

        log.info("Warranty type with publicId {} fetched successfully.. response :: {}", publicId, responseDto);

        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.fetched.successfully"),
                messageSourceService.getMessageByKey("warranty.type.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getAllWarrantyTypes(Integer page, Integer size) {

        page = (page <= 0) ? 0 : page - 1;
        size = (size <= 0) ? 10 : size;
        Pageable pageable = PageRequest.of(page, size);

        Page<WarrantyType> warrantyTypes = warrantyTypeInternalService.getAllWarrantyType(pageable);
        Page<WarrantyTypeResponseDto> warrantyTypeDtos = warrantyTypes
                .map(WarrantyTypeHelper::buildWarrantyTypeResponse);

        log.info("Warranty types with page {} and size of {} fetched successfully", page + 1, size);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.types.fetched.successfully"),
                messageSourceService.getMessageByKey("warranty.types.fetched.successfully"),
                warrantyTypeDtos, null);
    }

    @Override
    public AppResponse<Page<WarrantyTypeResponse>> filterWarrantyType(WarrantyTypeFilter warrantyTypeFilter,
            Pageable pageable) {
        QueryBuilder<WarrantyType, WarrantyTypeResponse> queryBuilder = QueryBuilder.build(WarrantyType.class,
                WarrantyTypeResponse.class);
        warrantyTypeFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<WarrantyTypeResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }

    @Override
    public AppResponse updateWarrantyType(UUID publicId, UpdateWarrantyTypeRequestDto requestDto) {
        WarrantyType warrantyType = warrantyTypeInternalService.findByPublicIdAndWarrantyType(publicId, requestDto);
        WarrantyTypeResponseDto warrantyTypeResponseDto = WarrantyTypeHelper
                .buildWarrantyTypeResponse(updateWarrantyType(warrantyType, requestDto));

        log.info("Warranty type with publicId {} updated successfully.. response:: {}", publicId,
                warrantyTypeResponseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("warranty.type.updated.successfully"),
                messageSourceService.getMessageByKey("warranty.type.updated.successfully"),
                warrantyTypeResponseDto, null);
    }

    private WarrantyType updateWarrantyType(WarrantyType warrantyType, UpdateWarrantyTypeRequestDto requestDto) {
        if (!Objects.isNull(requestDto.getWarrantyTypeName()) && !requestDto.getWarrantyTypeName().isEmpty())
            warrantyType.setWarrantyTypeName(requestDto.getWarrantyTypeName().trim());

        if (!Objects.isNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty())
            warrantyType.setDescription(requestDto.getDescription());

        if (!Objects.isNull(requestDto.getLastModifiedBy()) && !requestDto.getLastModifiedBy().isEmpty())
            warrantyType.setLastModifiedBy(requestDto.getLastModifiedBy());

        return warrantyTypeInternalService.updateWarrantyType(warrantyType);
    }
}
