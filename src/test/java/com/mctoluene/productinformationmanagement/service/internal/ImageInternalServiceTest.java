package com.mctoluene.productinformationmanagement.service.internal;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.mctoluene.productinformationmanagement.repository.ImageCatalogRepository;
import com.mctoluene.productinformationmanagement.repository.ImageRepository;
import com.mctoluene.productinformationmanagement.service.impl.ImageInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.productinformationmanagement.service.internal.NotificationInternalService;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

class ImageInternalServiceTest {

    @Mock
    ImageRepository imageRepository;

    @Mock
    ImageCatalogRepository imageCatalogRepository;

    @Mock
    Cloudinary cloudinary;

    ImageInternalService imageInternalService;

    NotificationInternalService notificationInternalService;

    AutoCloseable autoCloseable;

    @Mock
    RestTemplateBuilder restTemplateBuilder;

    @Mock
    RestTemplate restTemplate;

    @Mock
    Uploader uploader;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        imageInternalService = new ImageInternalServiceImpl(imageRepository, imageCatalogRepository, cloudinary,
                restTemplateBuilder, notificationInternalService);
    }

    /*
     * @Test
     * void uploadImagesFromUrl() throws Exception {
     * 
     * String imageUrl =
     * "https://res.cloudinary.com/rensourceenergy/image/upload/v1680264061/production/kc5z70hljwucnrcg5ign.jpg";
     * String imageName = "testImage";
     * byte[] mockImageData = "image-data".getBytes();
     * ResponseEntity<byte[]> mockResponse = new ResponseEntity<>(mockImageData,
     * HttpStatus.OK);
     * Map<String, Object> uploadResult = Map.of("url",
     * "https://cloudinary.com/path/to/image");
     * 
     * when(restTemplate.getForEntity(imageUrl,
     * byte[].class)).thenReturn(mockResponse);
     * when(cloudinary.uploader()).thenReturn(uploader);
     * when(uploader.upload(any(), any())).thenReturn(uploadResult);
     * 
     * Image result = imageInternalService.uploadImagesFromUrl(imageUrl, imageName);
     * 
     * assertEquals(imageName, result.getImageName());
     * assertEquals("https://cloudinary.com/path/to/image", result.getUrl());
     * }
     */

}