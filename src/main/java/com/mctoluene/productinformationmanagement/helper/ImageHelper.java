package com.mctoluene.productinformationmanagement.helper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageResponseDto;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;

public class ImageHelper {
    private ImageHelper() {
    }

    public static Image[] buildImageEntities(BulkUploadImageRequestDto requestDto) {

        Image[] image = new Image[requestDto.getImage().length];

        for (int i = 0; i < requestDto.getImage().length; i++) {
            image[i] = new Image();
            image[i].setStatus(Status.ACTIVE);
            image[i].setImageName("mctoluene-img-" + UUID.randomUUID().toString().replace("-", ""));
            image[i].setCreatedBy(requestDto.getCreatedBy());
            image[i].setLastModifiedBy(requestDto.getCreatedBy());
            image[i].setCreatedDate(LocalDateTime.now());
            image[i].setLastModifiedDate(LocalDateTime.now());
            image[i].setPublicId(UUID.randomUUID());
            image[i].setVersion(BigInteger.ZERO);

        }
        return image;
    }

    public static ImageResponseDto[] buildImageResponses(List<Image> image) {

        ImageResponseDto[] responseDto = new ImageResponseDto[image.size()];
        for (int i = 0; i < image.size(); i++) {
            responseDto[i] = ImageResponseDto.builder()
                    .publicId(image.get(i).getPublicId())
                    .imageName(image.get(i).getImageName())
                    .url(image.get(i).getUrl())
                    .createdBy(image.get(i).getCreatedBy())
                    .lastModifiedBy(image.get(i).getLastModifiedBy())
                    .createdDate(image.get(i).getCreatedDate())
                    .lastModifiedDate(image.get(i).getLastModifiedDate())
                    .status(image.get(i).getStatus().name())
                    .version(image.get(i).getVersion())
                    .build();
        }
        return responseDto;
    }

    public static List<ImageResponseDto> buildCatImageResponse(List<ImageCatalog> imageCatalogs) {
        List<ImageResponseDto> imageResponseList = new ArrayList<>();
        for (ImageCatalog response : imageCatalogs) {
            imageResponseList.add(ImageResponseDto.builder()
                    .publicId(response.getPublicId())
                    .imageName(response.getImageName())
                    .url(response.getImageUrl())
                    .createdDate(response.getCreatedDate())
                    .createdBy(response.getCreatedBy())
                    .lastModifiedBy(response.getLastModifiedBy())
                    .lastModifiedDate(response.getLastModifiedDate())
                    .status(response.getStatus())
                    .version(response.getVersion())
                    .build());
        }
        return imageResponseList;
    }

    public static ImageResponseDto buildImageResponse(Image image) {
        return ImageResponseDto.builder()
                .publicId(image.getPublicId())
                .imageName(image.getImageName())
                .url(image.getUrl())
                .createdBy(image.getCreatedBy())
                .lastModifiedBy(image.getLastModifiedBy())
                .createdDate(image.getCreatedDate())
                .lastModifiedDate(image.getLastModifiedDate())
                .status(image.getStatus().name())
                .version(image.getVersion())
                .build();
    }

}
