package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ImageController;
import com.mctoluene.productinformationmanagement.domain.request.image.BulkUploadImageRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageRequestDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.service.ImageService;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.TraceService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ImageController.class)
@Import(ImageService.class)
public class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public ImageService imageService;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void uploadImages() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                "image.uploaded.successfully",
                "image.uploaded.successfully",
                null, null);

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

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageService.uploadImages(uploadImageRequestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/image")
                    .requestAttr("image", mockMultipartFiles)
                    .param("createdBy", "Dilip")
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andReturn();
            assertEquals(200, mvcResult.getResponse().getStatus());
        }

    }

    @Test
    void deleteImage() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                "image.deleted.successfully",
                "image.deleted.successfully",
                null, null);
        String imageName = "img1";

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageService.deleteImage(imageName)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/image/" + imageName)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andReturn();
            assertEquals(200, mvcResult.getResponse().getStatus());
        }

    }

    @Test
    void uploadImageFromUrl() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                "image.uploaded.successfully",
                "image.uploaded.successfully",
                null, null);
        ImageRequestDto imageRequestDto = new ImageRequestDto("url", "name");

        String payload = mapToJson(imageRequestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageService.uploadImageFromUrl(any())).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/image/upload-image-from-uri")
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk());
        }

    }

    private static String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
