package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.UpdateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponse;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponseDto;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponse;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.search.QueryBuilder;
import com.mctoluene.productinformationmanagement.filter.search.VariantTypeFilter;
import com.mctoluene.productinformationmanagement.helper.VariantTypeHelper;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.VariantTypeService;
import com.mctoluene.productinformationmanagement.service.internal.VariantTypeInternalService;
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
public class VariantTypeServiceImpl implements VariantTypeService {

    private final VariantTypeInternalService variantTypeInternalService;

    private final MessageSourceService messageSourceService;

    public AppResponse createVariantType(CreateVariantTypeRequestDto requestDto) {
        if (variantTypeInternalService.checkIfNameExist(requestDto.getVariantTypeName().toUpperCase().trim())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.type.name.already.exist"));
        }
        VariantType variantType = VariantTypeHelper.buildVariantType(requestDto);
        variantTypeInternalService.saveNewVariantType(variantType);
        VariantTypeResponseDto responseDto = VariantTypeHelper.buildVariantTypeResponseDto(variantType);

        log.info("Variant type created successfully {}", responseDto);

        return new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("variant.type.created.successfully"),
                messageSourceService.getMessageByKey("variant.type.created.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse updateVariantType(UpdateVariantTypeRequestDto requestDto, UUID publicId) {
        VariantType variantTypeDetails = variantTypeInternalService.findVariantTypeByPublicId(publicId);
        VariantType validateVariantName = variantTypeInternalService
                .findVariantTypeByName(requestDto.getVariantTypeName().trim().toUpperCase());

        if (validateVariantName != null &&
                validateVariantName.getVariantTypeName().toUpperCase().trim()
                        .equals(requestDto.getVariantTypeName().trim().toUpperCase())
                && !publicId.equals(validateVariantName.getPublicId())) {
            throw new ValidatorException(messageSourceService.getMessageByKey("variant.type.name.already.exist"));
        }

        var variantType = buildVariantTypeUpdate(requestDto, variantTypeDetails);
        variantTypeInternalService.updateExistingVariantType(variantType);

        VariantTypeResponseDto responseDto = VariantTypeHelper.buildVariantTypeResponseDto(variantType);

        log.info("Variant type updated successfully {}", responseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.updated.successfully"),
                messageSourceService.getMessageByKey("variant.type.updated.successfully"),
                responseDto, null);

    }

    @Override
    public AppResponse deleteVariantType(UUID publicId) {
        VariantType variantType = variantTypeInternalService.findVariantTypeByPublicId(publicId);
        variantType.setStatus(Status.DELETED.name());
        variantType.setVariantTypeName(variantType.getVariantTypeName() + "-" + LocalDateTime.now());
        variantTypeInternalService.deleteVariantType(variantType);

        log.info("Variant type deleted successfully");
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.deleted.successfully"),
                messageSourceService.getMessageByKey("variant.type.deleted.successfully"),
                null, null);
    }

    @Override
    public AppResponse getVariantTypeById(UUID publicId) {
        VariantType variantType = variantTypeInternalService.findVariantTypeByPublicId(publicId);

        VariantTypeResponseDto responseDto = VariantTypeHelper.buildVariantTypeResponseDto(variantType);

        log.info("Variant type with publicId {} fetched successfully.. response :: {}", publicId, responseDto);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse getAllVariantTypes(String searchParam, Integer page, Integer size) {
        log.info("about to get variant type by page {} and size {} ", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<VariantType> variantTypes = variantTypeInternalService.getAllVariantTypesFiltered(searchParam, pageable);
        log.info("Variant types of page {} and size {} retrieved ", page, size);
        return buildAppResponse(variantTypes);
    }

    private AppResponse buildAppResponse(Page<VariantType> variantTypes) {
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.types.fetched.successfully"),
                variantTypes, null);
    }

    private VariantType buildVariantTypeUpdate(UpdateVariantTypeRequestDto requestDto, VariantType variantType) {

        if (Objects.nonNull(requestDto.getVariantTypeName()) && !requestDto.getVariantTypeName().isEmpty())
            variantType.setVariantTypeName(requestDto.getVariantTypeName().trim().toUpperCase());

        if (Objects.nonNull(requestDto.getDescription()) && !requestDto.getDescription().isEmpty())
            variantType.setDescription(requestDto.getDescription());

        if (Objects.nonNull(requestDto.getLastModifiedBy()) && !requestDto.getLastModifiedBy().isEmpty())
            variantType.setLastModifiedBy(requestDto.getLastModifiedBy());
        variantType.setLastModifiedDate(LocalDateTime.now());
        return variantType;
    }

    @Override
    public AppResponse getVariantTypeByName(String name) {

        VariantType variantType = variantTypeInternalService.findVariantTypeByName(name);
        if (Objects.isNull(variantType)) {
            return new AppResponse(HttpStatus.NO_CONTENT.value(),
                    messageSourceService.getMessageByKey("variant.type.not.found"),
                    messageSourceService.getMessageByKey("variant.type.not.found"),
                    null, null);
        }

        VariantTypeResponseDto responseDto = VariantTypeHelper.buildVariantTypeResponseDto(variantType);
        return new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                messageSourceService.getMessageByKey("variant.type.fetched.successfully"),
                responseDto, null);
    }

    @Override
    public AppResponse<Page<VariantTypeResponse>> filterVariantType(VariantTypeFilter variantTypeFilter,
            Pageable pageable) {
        QueryBuilder<VariantType, VariantTypeResponse> queryBuilder = QueryBuilder.build(VariantType.class,
                VariantTypeResponse.class);
        variantTypeFilter.filter(queryBuilder);
        queryBuilder.orderBy("createdDate", true);
        Page<VariantTypeResponse> result = queryBuilder.getResult(pageable);
        return new AppResponse<>(HttpStatus.OK.value(), "success", "success", result, null);
    }
}
