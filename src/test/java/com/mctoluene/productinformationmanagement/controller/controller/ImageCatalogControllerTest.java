package com.mctoluene.productinformationmanagement.controller.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.controller.ImageCatalogController;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.CreateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogVariantAwaitingApprovalDto;
import com.mctoluene.productinformationmanagement.domain.response.ImageCatalogResponseDto;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.helper.ImageCatalogHelper;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.service.ImageCatalogService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ImageCatalogController.class)
@Import(ImageCatalogService.class)

class ImageCatalogControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    public TraceService traceService;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @MockBean
    public MessageSourceService messageSourceService;

    @MockBean
    public ImageCatalogService imageCatalogService;

    @MockBean
    public LocationCacheInterfaceService locationCacheInterfaceService;

    ImageCatalogControllerTest() {

    }

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void createImageCatalog() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                null, null);
        List<ImageCatalogRequestDto> imageCatalogDto = new ArrayList<>();
        imageCatalogDto.add(imageCatalogRequestDto());

        CreateImageCatalogRequestDto requestDto = CreateImageCatalogRequestDto.builder().build();
        requestDto.setProductVariantPublicId(UUID.randomUUID());
        requestDto.setImageCatalogs(imageCatalogDto);
        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageCatalogService.createImageCatalog(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/image-catalog")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isCreated());
        }

    }

    @Test
    void createImageCatalogForVariantAwaitingApproval() throws Exception {
        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                messageSourceService.getMessageByKey("image.catalog.created.successfully"),
                null, null);
        List<ImageCatalogRequestDto> imageCatalogDto = new ArrayList<>();
        imageCatalogDto.add(imageCatalogRequestDto());

        ImageCatalogVariantAwaitingApprovalDto requestDto = ImageCatalogVariantAwaitingApprovalDto.builder().build();
        requestDto.setPublicVariantAwaitingApprovalId(UUID.randomUUID());
        requestDto.setImageCatalogs(imageCatalogDto);
        String inoutInJson = this.mapToJson(requestDto);

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageCatalogService.createImageCatalogForVariantAwaitingApproval(requestDto)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/image-catalog/awaiting-variants")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(inoutInJson)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isCreated());
        }

    }

    private ImageCatalogRequestDto imageCatalogRequestDto() {
        return ImageCatalogRequestDto.builder()
                .imageDescription("test")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .createdBy(UUID.randomUUID().toString()).build();

    }

    @Test
    void getImageByImageName() throws Exception {

        List<ImageCatalogResponseDto> responseDto = getImageCatalogueResponseDto();

        AppResponse response = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                responseDto, null);
        String imageName = responseDto.get(0).imageName();

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(imageCatalogService.getImageCatalogByImageName(imageName)).thenReturn(response);
            doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/image-catalog/imageName/" + imageName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].imageName").value("Nykaa"));
        }

    }

    @Test
    void getImageById() throws Exception {

        List<ImageCatalogResponseDto> responseDto = getImageCatalogueResponseDto();

        AppResponse response = new AppResponse(HttpStatus.CREATED.value(),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                messageSourceService.getMessageByKey("image.fetched.successfully"),
                responseDto, null);

        UUID publicId = UUID.randomUUID();
        when(imageCatalogService.getImageById(publicId)).thenReturn(response);
        doNothing().when(traceService).propagateSleuthFields(UUID.randomUUID());
        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/image-catalog/" + publicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-trace-id", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].imageName").value("Nykaa"));
        }

    }

    private static List<ImageCatalogResponseDto> getImageCatalogueResponseDto() {
        List<ImageCatalog> imageCatalogList = new ArrayList<>();
        ImageCatalog catalog = (ImageCatalog.builder()
                .imageName("Nykaa")
                .imageDescription("We serve Beauty products")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .productVariant(ProductVariant.builder().build())
                .status(Status.ACTIVE.name())
                .build());
        imageCatalogList.add(catalog);

        return ImageCatalogHelper.buildImageCatalogResponseDto(imageCatalogList);
    }

}
