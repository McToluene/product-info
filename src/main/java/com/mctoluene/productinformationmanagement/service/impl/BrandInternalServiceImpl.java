package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.repository.BrandRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.BrandInternalService;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandInternalServiceImpl implements BrandInternalService {

    private final BrandRepository brandRepository;
    private final MessageSourceService messageSourceService;
    private static final String ALREADYEXISTKEY = "brand.name.already.exist";

    @Override
    public Brand saveNewBrand(Brand brand) {
        log.info("About to create brand {}", brand);
        Optional<Brand> brandExist = brandRepository.findByBrandNameIgnoreCase(brand.getBrandName());

        if (brandExist.isPresent())
            throw new ValidatorException(messageSourceService.getMessageByKey(ALREADYEXISTKEY));

        return saveBrandToDb(brand);
    }

    @Override
    public Brand saveBrandToDb(Brand brand) {
        try {
            return brandRepository.save(brand);
        } catch (Exception e) {
            throw new UnProcessableEntityException(messageSourceService.getMessageByKey("could.not.process.request"));
        }
    }

    @Override
    public Brand findByPublicId(UUID publicId) {
        return brandRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService.getMessageByKey("brand.not.found")));
    }

    @Override
    public Page<Brand> findAllBy(Pageable pageable) {
        return brandRepository.findAllBy(pageable);
    }

    @Override
    public Page<Brand> findAllByCriteria(String brandName, Pageable pageable) {
        return brandRepository.findAllByCriteria(brandName, pageable);
    }

    @Override
    public Brand updateBrand(Brand brand) {
        try {
            return saveBrandToDb(brand);
        } catch (Exception e) {
            throw new ValidatorException(messageSourceService.getMessageByKey(ALREADYEXISTKEY));
        }
    }

    @Override
    public Brand findByPublicIdAndBrandName(UUID publicId, EditBrandRequestDto requestDto) {
        Optional<Brand> brandExist = brandRepository.findByBrandNameIgnoreCase(requestDto.getBrandName().trim());
        if (brandExist.isPresent() && publicId != brandExist.get().getPublicId())
            throw new ValidatorException(messageSourceService.getMessageByKey(ALREADYEXISTKEY));

        return brandRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ModelNotFoundException(messageSourceService.getMessageByKey("brand.not.found")));
    }

    @Override
    public Optional<Brand> findByBrandName(String brandName) {
        return brandRepository.findByBrandNameIgnoreCase(brandName);
    }

    @Override
    public Page<Brand> findByManufacturer(Manufacturer manufacturer, Pageable pageable) {
        return brandRepository.findByManufacturer(manufacturer, pageable);
    }

    public Optional<Brand> findByBrandNameAndManufacturer(String brandName, Manufacturer manufacturer) {
        return brandRepository.findByBrandNameAndManufacturer(brandName, manufacturer);
    }

    @Override
    public Brand deleteBrand(Brand brand) {
        return saveBrandToDb(brand);
    }

    @Override
    public Page<Brand> findAllByCriteria(String brandName, Manufacturer manufacturer, Pageable pageable) {
        return brandRepository.findAllByCriteria(brandName, manufacturer, pageable);
    }

}
