package com.mctoluene.productinformationmanagement.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandHelper {

    public static Brand buildBrandEntity(CreateBrandRequestDto requestDto, Manufacturer manufacturer) {
        String brandName = StringSanitizerUtils.sanitizeInput(requestDto.getBrandName());
        Brand brand = Brand.builder()
                .brandName(WordUtils.capitalize(StringSanitizerUtils.sanitizeInput(brandName.trim())))
                .description(requestDto.getDescription().isEmpty() ? ""
                        : StringSanitizerUtils.sanitizeInput(requestDto.getDescription().trim()))
                .manufacturer(manufacturer)
                .status(Status.ACTIVE)
                .build();

        brand.setPublicId(UUID.randomUUID());
        brand.setCreatedBy(requestDto.getCreatedBy());
        brand.setLastModifiedDate(LocalDateTime.now());
        brand.setLastModifiedBy(requestDto.getCreatedBy());
        brand.setCreatedDate(LocalDateTime.now());
        brand.setVersion(BigInteger.ZERO);

        return brand;
    }

    public static BrandResponseDto buildBrandResponse(Brand brand) {
        return BrandResponseDto.builder()
                .publicId(brand.getPublicId())
                .brandName(brand.getBrandName().trim())
                .description(brand.getDescription())
                .createdDate(brand.getCreatedDate())
                .createdBy(brand.getCreatedBy())
                .lastModifiedBy(brand.getLastModifiedBy())
                .lastModifiedDate(brand.getLastModifiedDate())
                .status(brand.getStatus().name())
                .version(brand.getVersion())
                .manufacturerId(brand.getManufacturer() == null ? null : brand.getManufacturer().getPublicId())
                .build();
    }
}
