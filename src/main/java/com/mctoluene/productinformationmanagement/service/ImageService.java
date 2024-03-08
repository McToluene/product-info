package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageResponseDto;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.commons.response.AppResponse;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    AppResponse<List<ImageResponseDto>> uploadImages(BulkUploadImageRequestDto requestDto);

    AppResponse<String> deleteImage(String imageName);

    AppResponse<Image> uploadImageFromUrl(ImageRequestDto imageRequestDto);

    AppResponse<Void> uploadFile(MultipartFile file, String uploadedBy);

    AppResponse<Page<ImageResponseDto>> findAllByNameAndCreatedDate(String imageName,
            String startDate, String endDate, Integer page, Integer size);

    ByteArrayResource download(String imageName,
            String startDate, String endDate, Integer page, Integer size);
}
