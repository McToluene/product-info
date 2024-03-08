package com.mctoluene.productinformationmanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.productinformationmanagement.service.ImagePublisherService;
import com.mctoluene.productinformationmanagement.service.ImageService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ImageServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.mockito.MockitoAnnotations.openMocks;

public class ImageServiceImplTest {

    @Mock
    private ImageInternalService imageInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    private ImageService imageService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ImagePublisherService imagePublisherService;

    @Mock
    Cloudinary cloudinary;

    @Mock
    Uploader uploader;

    @BeforeEach
    public void setup() {
        AutoCloseable autoCloseable = openMocks(this);
        imageService = new ImageServiceImpl(imageInternalService, messageSourceService, imagePublisherService);
    }

    @Test
    void uploadImages() throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("my_image.jpg");
        MockMultipartFile fileOne = new MockMultipartFile("my_image.jpg", "my_image.jpg", "image/png", inputStream);

        final InputStream inputStream2 = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("my_image2.jpg");
        MockMultipartFile fileTwo = new MockMultipartFile("my_image.jpg", "my_image.jpg", "image/png", inputStream2);

        MockMultipartFile[] mockMultipartFiles = new MockMultipartFile[] { fileOne, fileTwo };

        BulkUploadImageRequestDto uploadImageRequestDto = BulkUploadImageRequestDto.builder()
                .image(mockMultipartFiles)
                .createdBy("Dilip")
                .build();

        given(imageInternalService.uploadImages(any(), any())).willReturn(convertToResponseDto());
        var uploadImage = imageService.uploadImages(uploadImageRequestDto);
        assertThat(uploadImage).isNotNull();
        assertThat(uploadImage.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("image.uploaded.successfully"));
    }

    private static List<Image> convertToResponseDto() {
        List<Image> list = new ArrayList<>();
        for (int i = 0; i <= 2; i++) {
            Image image = new Image();
            image.setUrl("http://res.cloudinary.com/dqbcbkps6/image/upload/v1674808549/quughd91rzis02ffq8xb.jpg");
            image.setStatus(Status.ACTIVE);
            image.setPublicId(UUID.randomUUID());
            image.setLastModifiedBy("Dilip");
            image.setLastModifiedDate(LocalDateTime.now());
            image.setCreatedBy("Dilip");
            image.setCreatedDate(LocalDateTime.now());
            image.setVersion(BigInteger.ZERO);
            list.add(image);
        }
        return list;
    }

    @Test
    void deleteImages() throws IOException {
        String obj = new String("ok");
        when(imageInternalService.deleteImage(any())).thenReturn(obj);
        var response = imageService.deleteImage("img1");
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void deleteImagesNotFound() throws IOException {
        String obj = new String("not found");
        when(imageInternalService.deleteImage(any())).thenReturn(obj);
        var response = imageService.deleteImage("img1");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("image.not.found"));
    }

}
