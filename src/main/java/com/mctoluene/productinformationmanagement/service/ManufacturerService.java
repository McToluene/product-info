package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponse;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.productinformationmanagement.filter.search.ManufacturerFilter;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

public interface ManufacturerService {

    AppResponse<ManufacturerResponseDto> createManufacturer(CreateManufacturerRequestDto manufacturerRequestDto);

    AppResponse<ManufacturerResponseDto> getManufacturer(UUID publicId);

    AppResponse<Page<ManufacturerResponseDto>> getAllManufacturers(Integer page, Integer size);

    AppResponse<Page<ManufacturerResponseDto>> getAllManufacturers(String name, Integer page,
            Integer size, SortCriteria sort);

    AppResponse<ManufacturerResponseDto> updateManufacturer(UUID publicId, UpdateManufacturerRequestDto requestDto);

    AppResponse<Void> deleteManufacturer(UUID publicId);

    AppResponse<ManufacturerResponseDto> enableManufacturerStatus(UUID publicId);

    AppResponse disableManufacturerStatus(UUID publicID);

    AppResponse uploadManufacturerUsingExcel(MultipartFile file, String createdBy, UUID traceId);

    ByteArrayResource download(Integer page, Integer size);

    AppResponse<Page<ManufacturerResponse>> filterBrand(ManufacturerFilter manufacturerFilter, Pageable pageable);

}
