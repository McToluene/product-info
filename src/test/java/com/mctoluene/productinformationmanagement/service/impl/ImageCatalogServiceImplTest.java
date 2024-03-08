package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.CreateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogVariantAwaitingApprovalDto;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ImageCatalogInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ImageCatalogServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ProductInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ProductVariantInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalInternalService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ImageCatalogServiceImplTest {
    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ImageCatalogInternalServiceImpl imageCatalogInternalService;

    @Mock
    private ProductInternalServiceImpl productInternalService;

    @Mock
    private ProductVariantInternalService productVariantInternalService;

    @Mock
    private VariantAwaitingApprovalInternalService variantAwaitingApprovalInternalService;

    @Mock
    private ImageInternalService imageInternalService;

    @InjectMocks
    private ImageCatalogServiceImpl imageCatalogService;

    /*
     * @Test
     * void createImageCatalog() {
     * 
     * UUID publicId = UUID.randomUUID();
     * ProductVariant productVariantResponse = convertToProductVariantDto(publicId);
     * 
     * List<ImageCatalog> imageCatalogList = new ArrayList<>();
     * ImageCatalog imageCatalogResponse =
     * imageCatalogResponse(productVariantResponse);
     * imageCatalogList.add(imageCatalogResponse);
     * 
     * CreateImageCatalogRequestDto requestDto
     * =CreateImageCatalogRequestDto.builder().build();
     * requestDto.setProductVariantPublicId(publicId);
     * requestDto.setImageCatalogs(List.of(ImageCatalogRequestDto.builder()
     * .imageName("Test")
     * .imageUrl("http://localhost:8080")
     * .build()));
     * 
     * given(imageInternalService.checkIfNameExist(any())).willReturn(true);
     * given(imageInternalService.checkIfUrlExist(any())).willReturn(false);
     * given(productVariantInternalService.findByPublicId(publicId)).willReturn(
     * productVariantResponse);
     * given(imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogList)).
     * willReturn(imageCatalogList);
     * var createdResponse = imageCatalogService.createImageCatalog(requestDto);
     * assertThat(createdResponse).isNotNull();
     * assertThat(createdResponse.getMessage()).isEqualTo(messageSourceService.
     * getMessageByKey("variant.type.created.successfully"));
     * }
     */

    /*
     * @Test
     * void createImageCatalogForAwaitingVariant() {
     * 
     * UUID publicId = UUID.randomUUID();
     * VariantAwaitingApproval variantAwaitingApproval =
     * VariantAwaitingApproval.builder().build();
     * variantAwaitingApproval.setId(UUID.randomUUID());
     * variantAwaitingApproval.setPublicId(publicId);
     * 
     * List<ImageCatalog> imageCatalogList = new ArrayList<>();
     * ImageCatalog imageCatalogResponse =
     * imageCatalogResponse(variantAwaitingApproval);
     * imageCatalogList.add(imageCatalogResponse);
     * 
     * List<ImageCatalogRequestDto> imageCatalogDto = new ArrayList<>();
     * imageCatalogDto.add(imageCatalogRequestDto());
     * ImageCatalogVariantAwaitingApprovalDto requestDto =
     * ImageCatalogVariantAwaitingApprovalDto.builder().build();
     * requestDto.setPublicVariantAwaitingApprovalId(publicId);
     * requestDto.setImageCatalogs(imageCatalogDto);
     * 
     * given(imageInternalService.checkIfNameExist(any())).willReturn(true);
     * given(imageInternalService.checkIfUrlExist(any())).willReturn(true);
     * given(variantAwaitingApprovalInternalService.findByPublicId(publicId)).
     * willReturn(variantAwaitingApproval);
     * given(imageCatalogInternalService.saveImageCatalogsToDb(imageCatalogList)).
     * willReturn(imageCatalogList);
     * var createdResponse =
     * imageCatalogService.createImageCatalogForVariantAwaitingApproval(requestDto);
     * assertThat(createdResponse).isNotNull();
     * assertThat(createdResponse.getMessage()).isEqualTo(messageSourceService.
     * getMessageByKey("variant.type.created.successfully"));
     * }
     */

    private ImageCatalogRequestDto imageCatalogRequestDto() {
        return ImageCatalogRequestDto.builder()
                .imageName("Test Image")
                .imageDescription("test")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .createdBy("system").build();

    }

    private Product convertToProductDto(UUID publicId) {
        Product product = Product.builder().build();
        product.setId(UUID.randomUUID());
        product.setPublicId(publicId);
        return product;
    }

    private ProductVariant convertToProductVariantDto(UUID publicId) {
        ProductVariant productVariant = ProductVariant.builder().build();
        productVariant.setId(UUID.randomUUID());
        productVariant.setPublicId(publicId);
        return productVariant;
    }

    private ImageCatalog imageCatalogResponse(ProductVariant productVariant) {
        ImageCatalog imageCatalog = new ImageCatalog();
        imageCatalog.setPublicId(UUID.randomUUID());
        imageCatalog.setProductVariant(productVariant);
        imageCatalog.setImageDescription("test");
        imageCatalog.setImageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121");
        imageCatalog.setStatus(Status.ACTIVE.name());
        imageCatalog.setCreatedBy("system");
        imageCatalog.setLastModifiedBy("system");
        imageCatalog.setCreatedDate(LocalDateTime.now());
        imageCatalog.setLastModifiedDate(LocalDateTime.now());
        imageCatalog.setVersion(BigInteger.ZERO);
        return imageCatalog;
    }

    private ImageCatalog imageCatalogResponse(VariantAwaitingApproval variantAwaitingApproval) {
        ImageCatalog imageCatalog = new ImageCatalog();
        imageCatalog.setPublicId(UUID.randomUUID());
        imageCatalog.setVariantAwaitingApproval(variantAwaitingApproval);
        imageCatalog.setImageDescription("test");
        imageCatalog.setImageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121");
        imageCatalog.setStatus(Status.ACTIVE.name());
        imageCatalog.setCreatedBy("system");
        imageCatalog.setLastModifiedBy("system");
        imageCatalog.setCreatedDate(LocalDateTime.now());
        imageCatalog.setLastModifiedDate(LocalDateTime.now());
        imageCatalog.setVersion(BigInteger.ZERO);
        return imageCatalog;
    }

    @Test
    void getImageByName() {

        ImageCatalog catalog = (ImageCatalog.builder()
                .imageName("Nykaa")
                .imageDescription("We serve Beauty products")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .productVariant(ProductVariant.builder().build())
                .status(Status.ACTIVE.name())
                .build());

        List<ImageCatalog> images = Arrays.asList(catalog);

        String imageName = catalog.getImageName();
        given(imageCatalogInternalService.findByImageName(imageName))
                .willReturn(images);
        var createdResponse = imageCatalogService.getImageCatalogByImageName(imageName);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("image.fetched.successfully"));
    }

    @Test
    void getImageById() {
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .imageName("Nykaa")
                .imageDescription("We serve beauty products")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .productVariant(new ProductVariant())
                .status(Status.ACTIVE.name())
                .build();

        UUID publicId = UUID.randomUUID();
        given(imageCatalogInternalService.findByPublicId(publicId)).willReturn(imageCatalog);
        var createdResponse = imageCatalogService.getImageById(publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("image.fetched.successfully"));
    }

}
