package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mctoluene.productinformationmanagement.domain.request.image.ImageUploadRequestDto;
import com.mctoluene.productinformationmanagement.model.Image;

import java.time.LocalDateTime;
import java.util.List;

public interface ImageInternalService {
    List<Image> uploadImages(MultipartFile[] file, Image[] image);

    String uploadBase64ImageString(String base64ImageString);

    List<Image> uploadImagesToCloudinary(MultipartFile[] file, Image[] image);

    String deleteImage(String imageName);

    boolean checkIfNameExist(String name);

    boolean checkIfUrlExist(String url);

    Image uploadImagesFromUrl(String imageUrl, String imageName, String uploadedBy);

    List<Image> processImages(List<ImageUploadRequestDto> imageUploadRequestDtos, String uploadedBy);

    Page<Image> findAllByNameAndCreatedDate(String imageName,
            LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable);
}
