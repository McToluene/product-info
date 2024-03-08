package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.commons.response.AppResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductUploadService {
    AppResponse uploadGenericProductFile(MultipartFile file, String uploadedBy, UUID traceId, String countryCode);

    AppResponse uploadGenericProductFile(MultipartFile file, String uploadedBy, UUID traceId, String countryCode,
            UUID brandPublicId);

    AppResponse uploadGenericProductVariantFile(MultipartFile file, String uploadedBy, UUID traceId, String countryCode,
            UUID productPublicId);

}