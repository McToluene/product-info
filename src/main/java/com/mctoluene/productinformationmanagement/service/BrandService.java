package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.enums.SortCriteria;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponse;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.filter.search.BrandFilter;
import com.mctoluene.commons.response.AppResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

public interface BrandService {

    AppResponse<BrandResponseDto> createBrand(CreateBrandRequestDto requestDto);

    AppResponse<BrandResponseDto> getBrandByPublicId(UUID publicId);

    AppResponse<Page<BrandResponseDto>> getBrands(Integer page, Integer size);

    AppResponse<Page<BrandResponseDto>> getBrands(String brandName, UUID manufacturerId, Integer page,
            Integer size, SortCriteria sort);

    AppResponse<BrandResponseDto> editBrand(UUID publicId, EditBrandRequestDto requestDto);

    AppResponse<Void> deleteBrand(UUID publicId);

    AppResponse<Void> uploadBrandFile(MultipartFile file, String uploadedBy, UUID traceId, UUID manufacturerId)
            throws IOException;

    AppResponse<Page<BrandResponseDto>> getBrandsByManufacturer(UUID manufacturerPublicId, Integer page,
            Integer size);

    ByteArrayResource download(Integer page, Integer size);

    AppResponse uploadBrandManufacturerFile(MultipartFile file, String uploadedBy, UUID traceId);

    AppResponse<Page<BrandResponse>> filterBrand(BrandFilter brandFilter, Pageable pageable);
}
