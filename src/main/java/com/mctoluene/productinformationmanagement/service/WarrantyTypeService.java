package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.warrantyType.WarrantyTypeResponse;
import com.mctoluene.productinformationmanagement.filter.search.WarrantyTypeFilter;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WarrantyTypeService {

    AppResponse createWarrantyType(CreateWarrantyTypeRequestDto requestDto);

    AppResponse deleteWarrantyType(UUID publicId);

    AppResponse getWarrantyTypeById(UUID publicId);

    AppResponse updateWarrantyType(UUID publicId, UpdateWarrantyTypeRequestDto requestDto);

    AppResponse getAllWarrantyTypes(Integer page, Integer size);

    AppResponse<Page<WarrantyTypeResponse>> filterWarrantyType(WarrantyTypeFilter warrantyTypeFilter,
            Pageable pageable);

}
