package com.mctoluene.productinformationmanagement.service.impl;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.CreateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.productcategory.UpdateProductCategoryRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.response.productCategory.ProductCategoryResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.filter.RequestHeaderContextHolder;
import com.mctoluene.productinformationmanagement.filter.dto.RequestHeaderContext;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.LocationCacheInterfaceService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryHierarchyService;
import com.mctoluene.productinformationmanagement.service.ProductCategoryService;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.commons.exceptions.NotFoundException;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(SpringExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryInternalService productCategoryInternalService;

    @Mock
    private ProductCategoryHierarchyService productCategoryHierarchyService;

    @Mock
    private ProductCategoryHierarchyInternalService productCategoryHierarchyInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private PropertyInternalService propertyInternalService;

    @Mock
    private ProductInternalService productInternalService;

    @Mock
    private ProductVariantInternalService productVariantInternalService;

    @Mock
    private ProductCategoryService productCategoryService;

    @Mock
    private ImageInternalService imageInternalService;

    @Mock
    private LocationCacheInterfaceService locationCacheInterfaceService;

    @BeforeEach
    public void setup() {
        AutoCloseable autoCloseable = openMocks(this);
        productCategoryService = new ProductCategoryServiceImpl(productCategoryInternalService,
                productCategoryHierarchyService,
                productCategoryHierarchyInternalService, messageSourceService, propertyInternalService,
                productInternalService, productVariantInternalService, imageInternalService,
                locationCacheInterfaceService);
    }

    @Test
    void archiveProductCategory() {

        UUID publicId = UUID.randomUUID();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchy.builder()
                .productCategoryPublicId(publicId)
                .build();
        productCategoryHierarchy.setId(UUID.randomUUID());
        productCategoryHierarchy.setPublicId(UUID.randomUUID());

        Product product = Product.builder()
                .productName("product")
                .status(Status.ACTIVE.name())
                .productDescription("product")
                .manufacturer(new Manufacturer())
                .productCategory(new ProductCategory())
                .warrantyType(new WarrantyType())
                .brand(new Brand())
                .build();

        ProductVariant productVariant = ProductVariant.builder()
                .approvedBy("test")
                .approvedDate(LocalDateTime.now())
                .status(Status.ACTIVE.name())
                .build();
        productVariant.setPublicId(UUID.randomUUID());
        productVariant.setId(UUID.randomUUID());

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                null, null);

        when(productCategoryInternalService.findByPublicIdAndStatus(publicId, Status.ACTIVE))
                .thenReturn(productCategory);
        when(productCategoryInternalService.getAllChildrenOfProductCategory(publicId))
                .thenReturn(List.of(productCategory));
        when(productInternalService.archiveAllByproductCategoryIn(List.of(productCategory)))
                .thenReturn(List.of(product));
        when(productVariantInternalService.archiveAllByProductIdIn(List.of(product)))
                .thenReturn(List.of(productVariant));

        var response = productCategoryService.archiveProductCategory(publicId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("category.archived.successfully"));
    }

    @Test
    void unArchiveProductCategory() {

        UUID publicId = UUID.randomUUID();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.INACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        ProductCategory savedProductCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                messageSourceService.getMessageByKey("category.archived.successfully"),
                null, null);

        when(productCategoryInternalService.findByPublicIdAndStatus(publicId, Status.INACTIVE))
                .thenReturn(productCategory);
        when(productCategoryInternalService.saveProductCategoryToDb(productCategory))
                .thenReturn(savedProductCategory);

        var response = productCategoryService.unArchiveProductCategory(publicId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("category.archived.successfully"));
    }

    @Test
    void testGetNestedProductCategory() {

        UUID publicId = UUID.randomUUID();

        Pageable request = PageRequest.of(0, Integer.MAX_VALUE);

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.INACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        ProductCategory savedProductCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        savedProductCategory.setPublicId(UUID.randomUUID());
        savedProductCategory.setId(UUID.randomUUID());

        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchy.builder()
                .productCategoryParentPublicId(UUID.randomUUID())
                .productCategoryPublicId(UUID.randomUUID()).build();

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId))
                .thenReturn(productCategory);
        when(productCategoryInternalService.getAllProductCategories(request))
                .thenReturn(new PageImpl<>(List.of(productCategory)));
        when(productCategoryHierarchyInternalService.getAllProductCategory())
                .thenReturn(List.of(productCategoryHierarchy));
        when(productCategoryInternalService.getAllDirectChildrenOfProductCategoryUnpaged(productCategory.getPublicId()))
                .thenReturn(List.of(savedProductCategory));

        var response = productCategoryService.getAllNestedSubcategoryOfProductCategory(publicId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo(
                messageSourceService.getMessageByKey("category.archived.successfully"));
    }

    @Test
    void getProductCategoriesByPublicIds() {

        UUID productCategoryPublicId = UUID.randomUUID();

        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryName("abc");
        productCategory.setVersion(BigInteger.ONE);

        List<ProductCategory> productCategoryResponse = List.of(productCategory);
        List productCategoryPublicIds = List.of(productCategoryPublicId);
        given(productCategoryInternalService.findProductCategoryByPublicIds(productCategoryPublicIds))
                .willReturn(productCategoryResponse);
        AppResponse response = productCategoryService.getProductCategoriesByPublicIds(productCategoryPublicIds);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"));
    }

    @Test
    void testCreateProductCategory() {

        CreateProductCategoryRequestDto requestDto = CreateProductCategoryRequestDto.builder()
                .productCategoryName("Smartphones")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .description("Desc")
                .imageUrl("Test")
                .createdBy("Creator")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Property property = new Property();
        property.setValue("123");
        property.setId(1);
        property.setPublicId(UUID.randomUUID());

        List<CountryDto> countryDtos = new ArrayList<>();
        countryDtos.add(CountryDto.builder().publicId(UUID.fromString("f271dbc7-6559-4a6b-b419-fde0ab703c5f"))
                .threeLetterCode("NGN").build());

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(locationCacheInterfaceService.getLocationCache()).thenReturn(countryDtos);
            when(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                    .thenReturn(Optional.ofNullable(null));
            when(productCategoryInternalService.saveNewProductCategory(any())).thenReturn(productCategory);
            when(productCategoryInternalService.findProductCategoryByPublicId(any())).thenReturn(productCategory);
            when(locationCacheInterfaceService.getLocationCache()).thenReturn(countryDtos);
            when(propertyInternalService.findPropertyByName(any())).thenReturn(property);
            var response = productCategoryService.createProductCategory(requestDto);
            assertThat(response).isNotNull();
            assertThat(response.getMessage())
                    .isEqualTo(messageSourceService.getMessageByKey("product.category.created.successfully"));
        }

    }

    @Test
    void testCreateProductCategoryMaxDepthException() {

        CreateProductCategoryRequestDto requestDto = CreateProductCategoryRequestDto.builder()
                .productCategoryName("Smartphones")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .description("Desc")
                .imageUrl("Test")
                .createdBy("Creator")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(500)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Property property = new Property();
        property.setValue("123");
        property.setId(1);
        property.setPublicId(UUID.randomUUID());

        when(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .thenReturn(Optional.ofNullable(null));
        when(productCategoryInternalService.saveNewProductCategory(any())).thenReturn(productCategory);
        when(productCategoryInternalService.findProductCategoryByPublicId(any())).thenReturn(productCategory);
        when(propertyInternalService.findPropertyByName(any())).thenReturn(property);
        Assertions.assertThrows(ValidatorException.class,
                () -> productCategoryService.createProductCategory(requestDto));
    }

    @Test
    void testCreateProductCategoryException() {

        CreateProductCategoryRequestDto requestDto = CreateProductCategoryRequestDto.builder()
                .productCategoryName("Smartphones")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .description("Desc")
                .imageUrl("Test")
                .createdBy("Creator")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        when(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .thenReturn(Optional.of(productCategory));
        Assertions.assertThrows(ValidatorException.class,
                () -> productCategoryService.createProductCategory(requestDto));
    }

    @Test
    void testDeleteProductCategory() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId)).thenReturn(productCategory);
        when(productInternalService.checkIfCategoryIsInUse(any())).thenReturn(false);
        doNothing().when(productCategoryInternalService).deleteProductCategory(any());

        var response = productCategoryService.deleteProductCategory(publicId);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.category.deleted.successfully"));
    }

    @Test
    void testDeleteProductCategoryException() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId)).thenReturn(productCategory);
        when(productInternalService.checkIfCategoryIsInUse(any())).thenReturn(true);
        Assertions.assertThrows(ValidatorException.class, () -> productCategoryService.deleteProductCategory(publicId));
    }

    @Test
    void testGetProductCategoryByPublicId() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId)).thenReturn(productCategory);

        var response = productCategoryService.getProductCategoryByPublicId(publicId);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.category.fetched.successfully"));

    }

    @Test
    void getProductCategoriesByPublicIdsException() {

        UUID productCategoryPublicId = UUID.randomUUID();

        List<ProductCategory> productCategoryResponse = new ArrayList<>();
        List<UUID> productCategoryPublicIds = List.of(productCategoryPublicId);
        given(productCategoryInternalService.findProductCategoryByPublicIds(productCategoryPublicIds))
                .willReturn(productCategoryResponse);

        Assertions.assertThrows(NotFoundException.class,
                () -> productCategoryService.getProductCategoriesByPublicIds(productCategoryPublicIds));
    }

    @Test
    void testGetAllProductCategories() {

        int page = 1;
        int size = 10;
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Page<ProductCategory> productCategories = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryInternalService.getAllParentProductCategories(any())).thenReturn(productCategories);
        when(productCategoryInternalService.getAllProductCategories(any())).thenReturn(productCategories);

        var response = productCategoryService.getAllProductCategories(page, size, true);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("product categories fetched successfully");
    }

    @Test
    void testGetAllProductCategoriesFalse() {

        int page = 1;
        int size = 10;
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Page<ProductCategory> productCategories = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryInternalService.getAllParentProductCategories(any())).thenReturn(productCategories);
        when(productCategoryInternalService.getAllProductCategories(any())).thenReturn(productCategories);

        var response = productCategoryService.getAllProductCategories(page, size, false);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("product categories fetched successfully");
    }

    @Test
    void unArchiveProductCategoryExceptionTest() {

        UUID publicId = UUID.randomUUID();
        when(productCategoryInternalService.findByPublicIdAndStatus(publicId, Status.INACTIVE))
                .thenReturn(null);
        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryService.unArchiveProductCategory(publicId));
    }

    @Test
    void archiveProductCategoryExceptionTest() {
        UUID publicId = UUID.randomUUID();
        when(productCategoryInternalService.findByPublicIdAndStatus(publicId, Status.ACTIVE))
                .thenReturn(null);
        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryService.archiveProductCategory(publicId));
    }

    @Test
    void testGetAllDirectChildrenOfProductCategory() {
        int page = 1;
        int size = 10;
        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Page<ProductCategory> productCategories = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryInternalService.getAllDirectChildrenOfProductCategory(any(), any()))
                .thenReturn(productCategories);

        var response = productCategoryService.getAllDirectSubcategoryOfProductCategory(publicId, page, size);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.category.fetched.successfully"));
    }

    @Test
    void testGetAllChildrenOfProductCategory() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        when(productCategoryInternalService.getAllChildrenOfProductCategory(any()))
                .thenReturn(List.of(productCategory));

        var response = productCategoryService.getAllSubcategoryOfProductCategory(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.product.categories.retrieved.successfully"));
    }

    @Test
    void testUpdateProductCategory() {

        UpdateProductCategoryRequestDto requestDto = UpdateProductCategoryRequestDto.builder()
                .productCategoryName("test")
                .imageUrl("image://url")
                .description("desc")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .modifiedBy("test")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Property property = new Property();
        property.setValue("123");
        property.setId(1);
        property.setPublicId(UUID.randomUUID());

        when(productCategoryInternalService.findProductCategoryByPublicId(publicId)).thenReturn(productCategory);
        when(productCategoryInternalService.findProductCategoryByPublicId(any())).thenReturn(productCategory);
        when(propertyInternalService.findPropertyByName(any())).thenReturn(property);

        var response = productCategoryService.updateProductCategory(publicId, requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.category.updated.successfully"));

    }

    @Test
    void createNewProductCategoryDuplicateNameTest() {
        CreateProductCategoryRequestDto requestDto = CreateProductCategoryRequestDto.builder()
                .productCategoryName("Smartphones")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .description("Desc")
                .imageUrl("Test")
                .createdBy("Creator")
                .build();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();

        when(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .thenReturn(Optional.of(productCategory));

        Assertions.assertThrows(ValidatorException.class,
                () -> productCategoryService.createProductCategory(requestDto));
    }

    @Test
    void createNewProductCategoryWithWhiteSpaceName() {

        CreateProductCategoryRequestDto requestDto = CreateProductCategoryRequestDto.builder()
                .productCategoryName("  Smartphones  ")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .description("Desc")
                .imageUrl("Test")
                .createdBy("Creator")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Smartphones")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Property property = new Property();
        property.setValue("123");
        property.setId(1);
        property.setPublicId(UUID.randomUUID());

        List<CountryDto> countryDtos = new ArrayList<>();
        countryDtos.add(CountryDto.builder().publicId(UUID.randomUUID())
                .threeLetterCode("NGN").build());

        try (MockedStatic<RequestHeaderContextHolder> mockContext = Mockito
                .mockStatic(RequestHeaderContextHolder.class)) {
            mockContext.when(RequestHeaderContextHolder::getContext)
                    .thenReturn(new RequestHeaderContext(UUID.randomUUID().toString(), "NGN"));

            when(locationCacheInterfaceService.getLocationCache()).thenReturn(countryDtos);
            when(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                    .thenReturn(Optional.ofNullable(null));
            when(productCategoryInternalService.saveNewProductCategory(any())).thenReturn(productCategory);
            when(productCategoryInternalService.findProductCategoryByPublicId(any())).thenReturn(productCategory);
            when(propertyInternalService.findPropertyByName(any())).thenReturn(property);
            when(locationCacheInterfaceService.getLocationCache()).thenReturn(countryDtos);

            var response = productCategoryService.createProductCategory(requestDto);

            ProductCategoryResponseDto responseDto = (ProductCategoryResponseDto) response.getData();
            assertThat(responseDto.productCategoryName().equals(requestDto.getProductCategoryName().trim()));
        }

    }

    @Test
    void updateProductCategoryWithWhiteSpaceName() {

        UpdateProductCategoryRequestDto requestDto = UpdateProductCategoryRequestDto.builder()
                .productCategoryName("  TEST  ")
                .imageUrl("image://url")
                .description("desc")
                .productCategoryParentPublicIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
                .modifiedBy("test")
                .build();

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("NEW")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        ProductCategory updatedProductCategory = ProductCategory.builder()
                .productCategoryName("TEST")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(5)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Property property = new Property();
        property.setValue("123");
        property.setId(1);
        property.setPublicId(UUID.randomUUID());

        when(productCategoryInternalService.findProductCategoryByPublicId(any())).thenReturn(productCategory);
        when(propertyInternalService.findPropertyByName(any())).thenReturn(property);
        when(productCategoryInternalService.updateExistingProductCategory(productCategory))
                .thenReturn(updatedProductCategory);
        var response = productCategoryService.updateProductCategory(publicId, requestDto);

        ProductCategoryResponseDto responseDto = (ProductCategoryResponseDto) response.getData();
        assertThat(responseDto.productCategoryName().equals(requestDto.getProductCategoryName().trim()));

    }

    @Test
    void getProductCategoryWithImmediateDepth() {
        Pageable request = PageRequest.of(1, 5);

        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .imageUrl("abc@bcd.com")
                .status(Status.ACTIVE)
                .description("no description")
                .depth(0)
                .build();
        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(publicId);

        Page<ProductCategory> productCategories = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryInternalService.getAllParentProductCategories(any())).thenReturn(productCategories);

        when(productCategoryInternalService.getAllChildrenOfProductCategory(any()))
                .thenReturn(List.of(productCategory));

        AppResponse appResponse = productCategoryService.getProductCategories(1, 5);

        assertThat((int) appResponse.getStatus()).isEqualTo(200);

    }
}