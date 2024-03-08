package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;

import java.util.Optional;
import java.util.UUID;

public interface BrandInternalService {

    Brand saveNewBrand(Brand band);

    Brand saveBrandToDb(Brand brand);

    Brand findByPublicId(UUID publicId);

    Page<Brand> findAllBy(Pageable pageable);

    Brand updateBrand(Brand brand);

    Brand deleteBrand(Brand brand);

    Brand findByPublicIdAndBrandName(UUID publicId, EditBrandRequestDto requestDto);

    Optional<Brand> findByBrandName(String brandName);

    Page<Brand> findByManufacturer(Manufacturer manufacturer, Pageable pageable);

    Page<Brand> findAllByCriteria(String brandName, Pageable pageable);

    Page<Brand> findAllByCriteria(String brandName, Manufacturer manufacturer, Pageable pageable);

    Optional<Brand> findByBrandNameAndManufacturer(String brandName, Manufacturer manufacturer);
}
