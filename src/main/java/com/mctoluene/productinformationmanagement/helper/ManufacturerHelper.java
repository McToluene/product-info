package com.mctoluene.productinformationmanagement.helper;

import org.apache.commons.text.WordUtils;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ManufactureUploadRequest;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.util.StringSanitizerUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public class ManufacturerHelper {

    public ManufacturerHelper() {
    }

    public static Manufacturer buildManufacturerEntity(CreateManufacturerRequestDto manufacturerRequestDto) {
        String manufacturerName = StringSanitizerUtils.sanitizeInput(manufacturerRequestDto.getManufacturerName());
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName(WordUtils.capitalize(manufacturerName.trim()))
                .description(manufacturerRequestDto.getDescription())
                .status(Status.ACTIVE).build();

        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setVersion(BigInteger.ZERO);
        manufacturer.setCreatedBy(manufacturerRequestDto.getCreatedBy());
        manufacturer.setCreatedDate(LocalDateTime.now());

        return manufacturer;

    }

    public static ManufacturerResponseDto buildManufacturerResponse(Manufacturer manufacturer) {
        return ManufacturerResponseDto.builder()
                .publicId(manufacturer.getPublicId())
                .manufacturerName(manufacturer.getManufacturerName().trim())
                .createdDate(manufacturer.getCreatedDate())
                .description(manufacturer.getDescription())
                .createdBy(manufacturer.getCreatedBy())
                .lastModifiedBy(manufacturer.getLastModifiedBy())
                .lastModifiedDate(manufacturer.getLastModifiedDate())
                .status(manufacturer.getStatus().name())
                .version(manufacturer.getVersion())
                .build();
    }

    public static Manufacturer buildManufacturerEntity(ManufactureUploadRequest manufactureUploadRequest) {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName(WordUtils.capitalizeFully(
                        StringSanitizerUtils.sanitizeInput(manufactureUploadRequest.getManufacturerName().trim())))
                .description(manufactureUploadRequest.getDescription().isEmpty() ? ""
                        : StringSanitizerUtils.sanitizeInput(manufactureUploadRequest.getDescription().trim()))
                .status(Status.ACTIVE).build();
        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setVersion(BigInteger.ZERO);
        manufacturer.setCreatedBy(manufactureUploadRequest.getCreatedBy());
        manufacturer.setCreatedDate(LocalDateTime.now());
        return manufacturer;

    }
}