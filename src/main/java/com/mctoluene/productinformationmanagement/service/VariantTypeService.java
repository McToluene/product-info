package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.UpdateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponse;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.VariantTypeFilter;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VariantTypeService {
    AppResponse createVariantType(CreateVariantTypeRequestDto requestDto);

    AppResponse updateVariantType(UpdateVariantTypeRequestDto requestDto, UUID publicId);

    AppResponse deleteVariantType(UUID publicId);

    AppResponse getVariantTypeById(UUID publicId);

    AppResponse getAllVariantTypes(String searchParam, Integer page, Integer size);

    AppResponse getVariantTypeByName(String name);

    AppResponse<Page<VariantTypeResponse>> filterVariantType(VariantTypeFilter variantTypeFilter, Pageable pageable);

}
