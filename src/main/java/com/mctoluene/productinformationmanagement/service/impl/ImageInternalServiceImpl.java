package com.mctoluene.productinformationmanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageUploadRequestDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.NotificationHelper;
import com.mctoluene.productinformationmanagement.model.Image;
import com.mctoluene.productinformationmanagement.repository.ImageCatalogRepository;
import com.mctoluene.productinformationmanagement.repository.ImageRepository;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.productinformationmanagement.service.internal.NotificationInternalService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ImageInternalServiceImpl implements ImageInternalService {

    @Value("${com.cloudinary.cloud_name}")
    private String mCloudName;

    @Value("${com.cloudinary.api_key}")
    private String mApiKey;

    @Value("${com.cloudinary.api_secret}")
    private String mApiSecret;

    private final ImageRepository imageRepository;

    private final ImageCatalogRepository imageCatalogRepository;

    private final Cloudinary cloudinary;

    private final RestTemplate restTemplate;
    private final NotificationInternalService internalService;

    public ImageInternalServiceImpl(ImageRepository imageRepository, ImageCatalogRepository imageCatalogRepository,
            Cloudinary cloudinary, RestTemplateBuilder restTemplateBuilder,
            NotificationInternalService internalService) {
        this.imageRepository = imageRepository;
        this.imageCatalogRepository = imageCatalogRepository;
        this.cloudinary = cloudinary;
        this.restTemplate = restTemplateBuilder.build();
        this.internalService = internalService;
    }

    @Override
    public List<Image> uploadImages(MultipartFile[] file, Image[] image) {

        log.info("About to upload images {} file {} image", file, image);
        return uploadImagesToCloudinary(file, image);
    }

    @Override
    public String uploadBase64ImageString(String base64ImageString) {
        Cloudinary c = new Cloudinary("cloudinary://" + mApiKey + ":" + mApiSecret + "@" + mCloudName);

        Map uploadResult = null;
        try {
            uploadResult = c.uploader().upload(base64ImageString, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("could not process request ", e);
            throw new UnProcessableEntityException("unable to decode the image");
        }
        String url = (String) uploadResult.get("secure_url");
        log.info("upload result {} ", url);
        return url;
    }

    @Override
    public List<Image> uploadImagesToCloudinary(MultipartFile[] files, Image[] images) {
        Cloudinary c = new Cloudinary("cloudinary://" + mApiKey + ":" + mApiSecret + "@" + mCloudName);
        List<Image> imageResponse = new ArrayList<>();
        int counter = 0;
        try {
            for (MultipartFile file : files) {
                File f = Files.createTempFile("temp", file.getOriginalFilename()).toFile();
                file.transferTo(f);
                c.url().transformation(new Transformation()
                        .width(625)
                        .height(625)
                        .crop("scale")
                        .quality("auto:good")
                        .background("white")
                        .defaultImage(images[counter].getImageName()));

                var response = c.uploader().upload(f,
                        ObjectUtils.asMap("public_id", images[counter].getImageName()));

                images[counter].setUrl(String.valueOf(response.get("secure_url")));
                imageResponse.add(images[counter]);
                counter++;

            }
            imageResponse = imageRepository.saveAll(imageResponse);

        } catch (Exception e) {
            throw new UnProcessableEntityException("Could not process request");
        }
        return imageResponse;
    }

    @Override
    @Transactional
    public String deleteImage(String imageName) {
        log.info("About to delete image {} ", imageName);
        try {
            imageRepository.deleteByImageName(Status.DELETED, imageName);
            imageCatalogRepository.deleteByImageName(Status.DELETED.name(), imageName);
            return deleteImagesFromCloudinary(imageName).toString();
        } catch (IOException ee) {
            throw new ValidatorException("Image not found");
        }
    }

    private Object deleteImagesFromCloudinary(String imageName) throws IOException {
        Cloudinary c = new Cloudinary("cloudinary://" + mApiKey + ":" + mApiSecret + "@" + mCloudName);
        return c.uploader().destroy(imageName,
                ObjectUtils.emptyMap()).get("result");
    }

    @Override
    public boolean checkIfNameExist(String name) {
        var response = imageRepository.findByImageName(name);
        return response.isPresent();
    }

    @Override
    public boolean checkIfUrlExist(String url) {
        var response = imageRepository.findByUrl(url);
        return response.isPresent();
    }

    @Override
    public Image uploadImagesFromUrl(String imageUrl, String imageName, String uploadedBy) {
        log.info("about to decode image  from url for image with name {} ", imageName);
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<?, ?> uploadResult;
            byte[] imageBytes = Objects.requireNonNull(response.getBody());
            log.info("about to upload image to cloudinary for image with name {} ", imageName);
            try {
                cloudinary.url().transformation(new Transformation()
                        .width(625)
                        .height(625)
                        .crop("scale")
                        .quality("auto:good")
                        .background("white"));
                uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.error("Error occurred while processing request {} ", e.getMessage());
                throw new UnProcessableEntityException("Failed to upload image to cloudinary:: " + e.getMessage());
            }

            return Image.builder()
                    .imageName(imageName)
                    .url((String) uploadResult.get("secure_url"))
                    .build();
        } else {
            if (!uploadedBy.isEmpty())
                internalService.send(NotificationHelper.buildNotificationRequest(uploadedBy));
            throw new UnProcessableEntityException("Failed to fetch image from supplied url");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Image> processImages(List<ImageUploadRequestDto> imageUploadRequestDtos, String uploadedBy) {
        List<Image> images = imageUploadRequestDtos.stream()
                .map(imageUploadRequestDto -> {
                    Image image = uploadImagesFromUrl(imageUploadRequestDto.getImageUrl(),
                            imageUploadRequestDto.getImageName(), uploadedBy);
                    return buildImageDetails(uploadedBy, image);
                }).toList();
        try {
            images = imageRepository.saveAll(images);
        } catch (DataIntegrityViolationException e) {
            if (!uploadedBy.isEmpty())
                internalService.send(NotificationHelper.buildNotificationRequest(uploadedBy));
            log.error("Error occurred while persisting to the database {} ", e.getMessage());
        }
        return images;
    }

    private static Image buildImageDetails(String uploadedBy, Image image) {
        image.setPublicId(UUID.randomUUID());
        image.setCreatedBy(uploadedBy);
        image.setStatus(Status.ACTIVE);
        image.setCreatedDate(LocalDateTime.now());
        image.setLastModifiedBy(uploadedBy);
        image.setLastModifiedDate(LocalDateTime.now());
        return image;
    }

    @Override
    public Page<Image> findAllByNameAndCreatedDate(String imageName,
            LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable) {
        return imageRepository.findByImageNameIgnoreCaseAndCreatedByAndCreatedDate(imageName, fromDate,
                toDate, pageable);
    }

}
