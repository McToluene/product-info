package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.configuration.CountryProperties;
import com.mctoluene.productinformationmanagement.configuration.PimVatProperties;
import com.mctoluene.productinformationmanagement.domain.enums.ProductListing;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.queuemessage.*;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.ImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.CreateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.product.UpdateProductRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.CreateVariantAwaitingApprovalRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.CreateVariantRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.*;
import com.mctoluene.productinformationmanagement.domain.response.location.country.CountryDto;
import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.helper.ProductHelper;
import com.mctoluene.productinformationmanagement.model.*;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.BrandInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.FailedProductsInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ImageCatalogInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ManufacturerInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ProductInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ProductServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantAwaitingApprovalInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantTypeInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.WarrantyTypeInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.*;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProductServiceImplTest {
    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ProductInternalServiceImpl productInternalService;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Mock
    private WarrantyTypeInternalServiceImpl warrantyTypeInternalService;

    @Mock
    private ManufacturerInternalServiceImpl manufacturerInternalService;

    @Mock
    private BrandInternalServiceImpl brandInternalService;

    @Mock
    private LocationClientInternalService locationClientInternalService;

    @Mock
    private ProductCategoryInternalServiceImpl productCategoryInternalService;

    @InjectMocks
    private ProductCategoryServiceImpl productCategoryService;

    @Mock
    private MeasuringUnitInternalService measuringUnitInternalService;

    @Mock
    private VariantTypeInternalServiceImpl variantTypeInternalService;
    @Mock
    private VariantInternalServiceImpl variantInternalService;
    @Mock
    private ImageCatalogInternalServiceImpl imageCatalogInternalService;

    @Mock
    private VariantAwaitingApprovalInternalServiceImpl variantAwaitingApprovalInternalService;

    @Mock
    private FailedProductsInternalServiceImpl failedProductsInternalService;

    @Mock
    private InventoryClientInternalService inventoryClientInternalService;

    @Mock
    private VariantVersionInternalService variantVersionInternalService;

    @Mock
    private ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    @Mock
    private AzureBusMessageQueueService azureBusMessageQueueService;
    @Mock
    private ProductVariantInternalService productVariantInternalService;

    @Mock
    private PimVatProperties pimVatProperties;

    @Spy
    private ObjectMapper mapper;

    @Test
    void createNewProduct() {
        List<CreateVariantRequestDto> createVariantRequestDto = new ArrayList<>();
        CreateVariantRequestDto variantRequestDto = buildVariantRequestDto();
        createVariantRequestDto.add(variantRequestDto);

        CreateProductRequestDto requestDto = buildProductRequestDto();

        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setPublicId(requestDto.getManufacturerPublicId());
        manufacturer.setId(UUID.randomUUID());

        Brand brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Adidas")
                .description("We are sports industry")
                .depth(2)
                .imageUrl("")
                .status(Status.ACTIVE)
                .build();

        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName("w1")
                .description("new")
                .status(Status.ACTIVE)
                .build();

        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Inches")
                .description("nothing")
                .abbreviation("abb")
                .status(Status.ACTIVE.name())
                .build();
        Product product = ProductHelper.buildProduct(requestDto, brand, manufacturer, productCategory, measuringUnit,
                warrantyType);
        UUID publicId = UUID.randomUUID();
        requestDto.getVariants().get(0).setCountryPublicId(publicId);

        CreateProductResponseDto responseDto = ProductHelper.buildProductResponseDto(product);
        given(productInternalService.checkIfNameExist(requestDto.getProductName())).willReturn(false);
        when(productInternalService.productNameIsNotUniqueToBrandAndManufacturer(requestDto.getProductName(), brand,
                manufacturer)).thenReturn(false);
        given(brandInternalService.findByPublicId(requestDto.getBrandPublicId())).willReturn(brand);
        given(warrantyTypeInternalService.findByPublicId(UUID.fromString(requestDto.getWarrantyTypePublicId())))
                .willReturn(warrantyType);
        given(productCategoryInternalService.findProductCategoryByPublicId(requestDto.getCategoryPublicId()))
                .willReturn(productCategory);
        given(measuringUnitInternalService.findByPublicId(requestDto.getMeasurementUnitPublicId()))
                .willReturn(measuringUnit);
        given(locationClientInternalService.getCountryByPublicId(any())).willReturn(getCountryMock());
        given(locationClientInternalService.findCountryByPublicIds(any())).willReturn(getCountryListMock(publicId));
        given(pimVatProperties.getCountries()).willReturn(getPimVatProperties());

        var createdResponse = productServiceImpl.createNewProduct(requestDto, Boolean.FALSE, "NGN");
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.created.successfully"));
    }

    @Test
    void testCreateNewProduct_with_invalid_manufacturer_publicId() {
        List<CreateVariantRequestDto> createVariantRequestDto = new ArrayList<>();
        CreateVariantRequestDto variantRequestDto = buildVariantRequestDto();
        createVariantRequestDto.add(variantRequestDto);

        CreateProductRequestDto requestDto = buildProductRequestDto();

        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setId(UUID.randomUUID());

        Brand brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();

        UUID publicId = UUID.randomUUID();

        given(brandInternalService.findByPublicId(any())).willReturn(brand);

        ValidatorException thrown = assertThrows(ValidatorException.class, () -> {
            productServiceImpl.createNewProduct(requestDto, Boolean.FALSE, "NGN");
        });

        assertEquals(messageSourceService.getMessageByKey("product.manufacturer.brand.mismatch"), thrown.getMessage());

    }

    private ResponseEntity<AppResponse<List<CountryDto>>> getCountryListMock(UUID publicId) {
        CountryDto countryDto = CountryDto.builder()
                .countryName("Nigeria")
                .createdBy("test")
                .dialingCode("01")
                .publicId(publicId)
                .threeLetterCode("NGN")
                .twoLetterCode("NG")
                .status(Status.ACTIVE.name())
                .build();
        AppResponse<List<CountryDto>> appResponse = new AppResponse<>(HttpStatus.OK.value(), "", "",
                List.of(countryDto), null);
        return ResponseEntity.ok(appResponse);
    }

    private ResponseEntity<AppResponse<CountryDto>> getCountryMock() {
        CountryDto countryDto = CountryDto.builder().threeLetterCode("NGN").build();
        AppResponse<CountryDto> appResponse = new AppResponse<>(HttpStatus.OK.value(), "", "",
                countryDto, null);
        return ResponseEntity.ok(appResponse);
    }

    @Test
    void getProductsByCategorySuccessfully() throws Exception {
        ProductCategory productCategory = getProductCategory();
        Product product = new Product();
        product.setProductName("abc");
        product.setVersion(BigInteger.ONE);
        UUID categoryId = UUID.randomUUID();
        productCategory.setPublicId(categoryId);

        List<Product> productList = List.of(product);

        when(productInternalService.findByCategory(categoryId)).thenReturn(productList);
        AppResponse response = productServiceImpl.getProductsByProductCategory(categoryId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("products.retrieved.success"));
    }

    @Test
    void updateProductByPublicId() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = getProductCategory();
        Brand brand = getBrand();
        Manufacturer manufacturer = getManufacturer();
        WarrantyType warrantyType = getWarrantyType();
        MeasuringUnit measuringUnit = getMeasuringUnit();

        Product product = new Product();
        product.setProductName("abc");
        product.setVersion(BigInteger.ONE);

        Product productResponse = Product.builder()
                .productName("Milo")
                .productCategory(productCategory)
                .brand(brand)
                .manufacturer(manufacturer)
                .vated(false)
                .warrantyType(warrantyType)
                .measurementUnit(measuringUnit)
                .status(Status.ACTIVE.name())
                .productListing("MERCHBUY")
                .build();

        given(productInternalService.findByPublicId(publicId)).willReturn(product);
        given(productInternalService.saveProductToDb(any())).willReturn(productResponse);

        AppResponse response = productServiceImpl.updateProductByPublicId(publicId, getUpdateProductRequestDto());
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.updated.successfully"));
    }

    private UpdateProductRequestDto getUpdateProductRequestDto() {
        return UpdateProductRequestDto.builder()
                .productName(" TEST PRODUCT ")
                .minVat(BigDecimal.ZERO)
                .maxVat(BigDecimal.ONE)
                .warrantyTypePublicId(UUID.randomUUID())
                .modifiedBy("test user")
                .measurementUnitPublicId(UUID.randomUUID())
                .build();
    }

    private MeasuringUnit getMeasuringUnit() {
        return MeasuringUnit.builder()
                .name("Inches")
                .description("nothing")
                .abbreviation("abc")
                .status(Status.ACTIVE.name())
                .build();
    }

    private Product createProduct() {
        Product product = Product.builder().build();
        product.setId(UUID.randomUUID());
        return product;

    }

    private ProductCategory getProductCategory() {
        return ProductCategory.builder()
                .productCategoryName("Auto")
                .description("All things auto")
                .status(Status.ACTIVE)
                .imageUrl("imageUrl")
                .build();
    }

    @Test
    void getProductsByProductCategoryId() {

        int page = 0;
        int size = 5;
        UUID productCategoryPublicId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(page, size);

        Product product = new Product();
        product.setProductName("abc");
        product.setVated(false);
        product.setVersion(BigInteger.ONE);
        product.setBrand(getBrand());
        product.setManufacturer(getManufacturer());
        product.setProductCategory(getProductCategory());

        List<Product> productResponse = List.of(product);
        final int start = (int) pageRequest.getOffset();
        final int end = Math.min((start + pageRequest.getPageSize()), productResponse.size());
        Page<Product> pageProduct = new PageImpl<>(productResponse.subList(start, end), pageRequest,
                productResponse.size());

        given(productInternalService.findByCategory(pageRequest, productCategoryPublicId)).willReturn(pageProduct);
        AppResponse response = productServiceImpl.getProductsByProductCategoryId(productCategoryPublicId, page, size);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("products.retrieved.success"));
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

    private ImageCatalogRequestDto imageCatalogRequestDto() {
        return ImageCatalogRequestDto.builder()
                .imageDescription("test")
                .imageUrl("https://d3juwdfmtun6px.cloudfront.net/8638109299140888121")
                .createdBy("system").build();

    }

    private CreateVariantRequestDto buildVariantRequestDto() {

        return CreateVariantRequestDto.builder()
                .variantName("Test variant")
                .variantDescription("This is test")
                .costPrice(BigDecimal.valueOf(5000))
                .createdBy("system").build();

    }

    private VariantType getVariantTypeResponseDto() {
        VariantType variantType = new VariantType();
        variantType.setPublicId(UUID.randomUUID());
        variantType.setVariantTypeName("Variant Name one");
        variantType.setDescription("This variant name one");
        variantType.setStatus(Status.ACTIVE.name());
        variantType.setCreatedBy("system");
        variantType.setLastModifiedBy("system");
        variantType.setCreatedDate(LocalDateTime.now());
        variantType.setLastModifiedDate(LocalDateTime.now());
        variantType.setVersion(BigInteger.ZERO);
        return variantType;
    }

    private Manufacturer getManufacturer() {
        return Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
    }

    private WarrantyType getWarrantyType() {
        return WarrantyType.builder()
                .warrantyTypeName("w")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
    }

    private Brand getBrand() {
        return Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .manufacturer(getManufacturer())
                .build();
    }

    private Product convertToResponseDto(UUID publicId) {
        Product product = Product.builder().build();
        product.setId(publicId);
        product.setPublicId(publicId);
        product.setStatus(Status.ACTIVE.name());
        return product;
    }

    public static CreateProductRequestDto buildProductRequestDto() {
        CreateProductRequestDto product = CreateProductRequestDto.builder().build();
        product.setProductName("  Product one  ".toUpperCase());
        product.setProductDescription("This is test product");
        product.setBrandPublicId(UUID.randomUUID());
        product.setManufacturerPublicId(UUID.randomUUID());
        product.setCategoryPublicId(UUID.randomUUID());
        product.setWarrantyTypePublicId(UUID.randomUUID().toString());
        product.setMeasurementUnitPublicId(UUID.randomUUID());
        product.setProductListings(Collections.singleton(ProductListing.AGENTAPP));
        product.setProductHighlights("This is test");
        product.setWarrantyAddress("Test");
        product.setWarrantyCover("All");
        product.setWarrantyDuration("6 months");
        product.setCreatedBy("system");
        product.setVated(false);
        product.setMaxVat(BigDecimal.ZERO);
        product.setMinVat(BigDecimal.ZERO);
        product.setVariants(List.of(CreateVariantAwaitingApprovalRequestDto.builder()
                .variantName("test")
                .costPrice(BigDecimal.ONE)
                .createdBy("test")
                .countryPublicId(UUID.randomUUID())
                .variantTypeId(UUID.randomUUID())
                .variantDescription("desc")
                .build()));
        return product;

    }

    @Test
    void getProductCategoriesWithFilter() {
        int page = 0;
        int size = 5;
        int count = 30;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String getProductCategoryName = "Automobiles";
        PageRequest pageRequest = PageRequest.of(page, size);

        given(productCategoryInternalService.findAllBySearchCrieteria(pageRequest,
                getProductCategoryName, startDate, endDate))
                .willReturn(getProductCategoryResponse(pageRequest, count));

        var createdResponse = productCategoryService.getAllProductCategoriesFiltered(page, size,
                getProductCategoryName, startDate, endDate);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("list.of.product.categories.retrieved.successfully"));
    }

    private Page<ProductCategory> getProductCategoryResponse(PageRequest pageRequest, int count) {
        List<ProductCategory> categoryResponse = new ArrayList<>();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), categoryResponse.size());
        Page<ProductCategory> page = new PageImpl<>(categoryResponse.subList(start, end), pageRequest,
                categoryResponse.size());
        for (int i = 0; i <= count; i++) {

            categoryResponse.add(ProductCategory.builder()
                    .productCategoryName("Automobiles")
                    .description("All things automobiles")
                    .status(Status.ACTIVE)
                    .depth(8)
                    .imageUrl("https://imageRepo.com/1243")
                    .build());

        }
        return page;
    }

    @Test
    void deleteProduct() {
        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.deleteProduct(any())).willReturn(responseDto);

        var deleteResponse = productServiceImpl.deleteProduct(publicId);
        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.deleted.successfully"));
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void archiveUnarchiveProduct() {

        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.updateProductArchiveStatus(any(), any())).willReturn(responseDto);
        given(variantInternalService.updateProductVariantsArchiveStatus(any(), any()))
                .willReturn(List.of(new ProductVariant()));

        var archivedProduct = productServiceImpl.updateProductArchiveStatus(publicId, Status.INACTIVE.name());
        assertThat(archivedProduct).isNotNull();
        assertThat(archivedProduct.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.archived.successfully"));
        assertThat(archivedProduct.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void getProductByPublicId() {

        UUID publicId = UUID.randomUUID();

        final var product = Product.builder()
                .productName("product")
                .status(Status.ACTIVE.name())
                .productDescription("product")
                .vated(false)
                .manufacturer(getManufacturer())
                .productCategory(getProductCategory())
                .warrantyType(getWarrantyType())
                .brand(getBrand())
                .build();

        given(productInternalService.findByPublicIdAndStatus(any())).willReturn(product);

        ProductResponseDto productResponseDto = ProductHelper.buildProductResponse(product);

        var result = productServiceImpl.getProductByPublicId(publicId);

        assertThat(result.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("products.retrieved.success"));

    }

    @Test
    void saveImageUploadTemplateRequestTest() {

        String createdBy = "test";

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setProductName("testProduct");
        productVariantDto.setVariantName("testVariant");
        productVariantDto.setManufacturerName("Nykaa");
        productVariantDto.setBrandName("Adidas");
        productVariantDto.setProductCategoryName("Auto");
        productVariantDto.setCostPrice(new BigDecimal("1000"));
        productVariantDto.setVariantTypeName("testVariantType");
        productVariantDto.setCreatedBy(createdBy);

        ImageUploadTemplateRequest imageTemplateRequest = getImageTemplateRequest();
        imageTemplateRequest.setImageUrl2("http://testImage.jpg");
        List<VariantAwaitingApproval> variantListMock = Arrays.asList(variantAwaitingApproval());

        given(manufacturerInternalService.findByManufacturerName(any())).willReturn(Optional.of(getManufacturer()));
        given(brandInternalService.findByBrandName(any())).willReturn(Optional.of(getBrand()));
        given(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .willReturn(Optional.of(getProductCategory()));
        given(variantTypeInternalService.findVariantTypeByNameIgnoreCase(any()))
                .willReturn(Optional.of(getVariantType()));
        given(productInternalService.findProductByName(any())).willReturn(Optional.of(getProductDto()));
        given(failedProductsInternalService.saveAllFailedProducts(any())).willReturn(List.of(getFailedProducts()));
        given(productInternalService.saveAllProducts(any())).willReturn(List.of(getProductDto()));
        given(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .willReturn(variantListMock);
        given(imageCatalogInternalService.saveImageCatalogsToDb(any())).willReturn(List.of(getImageCatalog()));
        given(productInternalService.saveProductToDb(any())).willReturn(getProductDto());
        given(locationClientInternalService.getCountryByPublicId(any())).willReturn(getCountryMock());

        given(pimVatProperties.getCountries()).willReturn(getPimVatProperties());

        var response = productServiceImpl.saveUploadProductVariants(List.of(getImageTemplateRequest()), createdBy,
                UUID.randomUUID());
        assertThat(response).isNotNull();

    }

    @Test
    void getProductCatalogueTest() {

        UUID traceId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        int page = 1;
        int size = 3;
        String searchValue = "var";
        UUID stateId = UUID.randomUUID();
        UUID lgaId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();
        UUID variantPublicId = UUID.randomUUID();
        SupplierProductDetailsResponseDto supplierProductDetailsResponseDto = SupplierProductDetailsResponseDto
                .builder()
                .sku("test")
                .productId(UUID.randomUUID())
                .variantName("varTest")
                .lgaName("lga")
                .quantity(BigInteger.valueOf(100))
                .supplierName("new")
                .variantPublicId(variantPublicId)
                .build();

        PriceModelResponseDto priceModelResponseDto1 = PriceModelResponseDto.builder()
                .productSku("test")
                .publicId(UUID.randomUUID())
                .status(Status.ACTIVE.name())
                .finalSellingPrice(BigDecimal.valueOf(400))
                .manualSellingPrice(BigDecimal.valueOf(500))
                .maximumQuantity(BigInteger.valueOf(10))
                .minimumQuantity(BigInteger.ONE)
                .build();

        PriceModelResponseDto priceModelResponseDto2 = PriceModelResponseDto.builder()
                .productSku("SKU-001")
                .publicId(UUID.randomUUID())
                .status(Status.ACTIVE.name())
                .finalSellingPrice(BigDecimal.valueOf(300))
                .manualSellingPrice(BigDecimal.valueOf(400))
                .maximumQuantity(BigInteger.valueOf(10))
                .minimumQuantity(BigInteger.ONE)
                .build();
        Product product = Product.builder()
                .productName("new")
                .status(Status.ACTIVE.name())
                .build();
        ProductVariant productVariant1 = ProductVariant.builder()
                .status(Status.ACTIVE.name())
                .approvedBy("test")
                .product(product)
                .build();
        productVariant1.setId(UUID.randomUUID());
        productVariant1.setPublicId(variantPublicId);
        ProductVariant productVariant2 = ProductVariant.builder()
                .status(Status.ACTIVE.name())
                .approvedBy("test")
                .product(product)
                .build();
        productVariant2.setId(UUID.randomUUID());
        productVariant2.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder()
                .variantDescription("test desc")
                .approvedBy("test")
                .sku("SKU-001")
                .status(Status.ACTIVE.name())
                .variantName("iphone 11")
                .productVariant(productVariant1)
                .product(product)
                .build();

        ImageCatalog imageCatalog1 = ImageCatalog.builder()
                .productVariant(productVariant1)
                .status(Status.ACTIVE.name())
                .imageDescription("desc")
                .imageUrl("test1")
                .build();

        ImageCatalog imageCatalog2 = ImageCatalog.builder()
                .productVariant(productVariant2)
                .status(Status.ACTIVE.name())
                .imageDescription("desc")
                .imageUrl("test2")
                .build();

        List<InventoryData> inventoryDataList = Arrays
                .asList(new InventoryData("SKU-001", 100.0, 50, 50, 50, 20, 10.0, null));
        LiveInventoryResponse liveInventoryResponseData = LiveInventoryResponse.builder()
                .liveInventoryProducts(inventoryDataList)
                .warehouseName("test")
                .warehouseId(warehouseId)
                .build();

        List<PriceModelResponseDto> priceModelResponseDtos = List.of(priceModelResponseDto1, priceModelResponseDto2);
        AppResponse liveInventoryResponse = new AppResponse(HttpStatus.OK.value(),
                "warehouse live inventory fetched successfully",
                "warehouse live inventory fetched successfully", liveInventoryResponseData, null);

        AppResponse virtualInventoryResponse = new AppResponse();
        virtualInventoryResponse.setStatus(HttpStatus.OK.value());
        virtualInventoryResponse.setData(List.of(supplierProductDetailsResponseDto));
        AppResponse priceModelResponse = new AppResponse();
        priceModelResponse.setStatus(HttpStatus.OK.value());
        priceModelResponse.setData(priceModelResponseDtos);
        when(inventoryClientInternalService.getStockOneLiveInventoryProducts(traceId.toString(), stateId, warehouseId))
                .thenReturn(liveInventoryResponse);
        when(inventoryClientInternalService.filterVirtualStorageProducts(stateId, cityId, lgaId))
                .thenReturn(virtualInventoryResponse);
        when(variantVersionInternalService.findVariantBySkuList(List.of("SKU-001")))
                .thenReturn(List.of(variantVersion));
        when(shoppingExperienceClientInternalService.getPriceModelBySkuList(any()))
                .thenReturn(ResponseEntity.ok(priceModelResponse));
        when(variantInternalService
                .findProductVariantsByPublicIds(List.of(productVariant1.getPublicId(), productVariant2.getPublicId())))
                .thenReturn(List.of(productVariant1, productVariant2));
        when(imageCatalogInternalService.findByProductVariants(List.of(productVariant1, productVariant2)))
                .thenReturn(List.of(imageCatalog1, imageCatalog2));

        AppResponse productResponse = new AppResponse();
        productResponse.setData(supplierProductDetailsResponseDto);
        productResponse.setMessage(messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"));
        productResponse.setSupportDescriptiveMessage(
                messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"));

        List<SupplierProductDetailsResponseDto> listMocked = Arrays.asList(supplierProductDetailsResponseDto);

        when(messageSourceService.getMessageByKey(any())).thenReturn(productResponse.getMessage());

        var appResponse = productServiceImpl.createProductCatalogue(traceId, warehouseId, stateId, cityId, lgaId,
                searchValue, page, size);
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.catalogue.retrieved.successfully"));

    }

    private VariantType getVariantType() {
        return VariantType.builder()
                .variantTypeName("testVariantType")
                .description("variant type desc")
                .status(Status.ACTIVE.name())
                .build();
    }

    private Product getProductDto() {

        return Product.builder()
                .productName("testProduct")
                .productDescription("products desc")
                .status(Status.ACTIVE.name())
                .vated(false)
                .brand(getBrand())
                .manufacturer(getManufacturer())
                .productCategory(getProductCategory())
                .build();

    }

    private FailedProducts getFailedProducts() {
        return FailedProducts.builder()
                .productName("testVariantType")
                .productDescription("variant type desc")
                .status(Status.ACTIVE.name())
                .build();
    }

    private ImageCatalog getImageCatalog() {
        return ImageCatalog.builder()
                .imageUrl("http://testImage.jpg")
                .imageName("testImage")
                .imageDescription("image desc")
                .status(Status.ACTIVE.name())
                .variantAwaitingApproval(variantAwaitingApproval())
                .build();
    }

    private ImageUploadTemplateRequest getImageTemplateRequest() {

        ImageUploadTemplateRequest imageTemplateRequest = new ImageUploadTemplateRequest();
        imageTemplateRequest.setProductName("testProduct");
        imageTemplateRequest.setVariantName("testVariant");
        imageTemplateRequest.setManufacturerName("Nykaa");
        imageTemplateRequest.setBrand("Adidas");
        imageTemplateRequest.setProductCategory("Auto");
        imageTemplateRequest.setCostPrice(new BigDecimal(1000));
        imageTemplateRequest.setMaxVat(BigDecimal.ZERO);
        imageTemplateRequest.setMinVat(BigDecimal.ZERO);
        imageTemplateRequest.setVariantType("testVariantType");
        imageTemplateRequest.setImageUrl1("http://testImage.jpg");
        return imageTemplateRequest;

    }

    private VariantAwaitingApproval variantAwaitingApproval() {

        return VariantAwaitingApproval.builder()
                .variantType(getVariantType())
                .variantName("testVariant")
                .variantDescription("variant desc")
                .status(Status.ACTIVE.name())
                .build();

    }

    @Test
    void testGetProductsByBrand() {
        UUID brandId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("AIG")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setPublicId(UUID.randomUUID());

        Brand brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();
        brand.setId(brandId);

        Product product = Product.builder()
                .brand(brand)
                .vated(false)
                .productName("Mobile")
                .build();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Electronics")
                .status(Status.ACTIVE)
                .build();
        productCategory.setPublicId(UUID.randomUUID());

        product.setManufacturer(manufacturer);
        product.setProductCategory(productCategory);

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(brandInternalService.findByPublicId(any())).thenReturn(brand);
        when(productInternalService.getProductsByBrandId(any())).thenReturn(productList);

        AppResponse response = productServiceImpl.getProductsByBrand(brandId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("product.retrieved.successfully"));
    }

    @Test
    void testGetApprovedProductsByPublicIdList() {

        Product product = Product.builder()
                .productName("Mobile")
                .build();
        List<UUID> prodPublicIdList = new ArrayList<>();
        prodPublicIdList.add(UUID.randomUUID());
        prodPublicIdList.add(UUID.randomUUID());
        prodPublicIdList.add(UUID.randomUUID());

        when(productInternalService.findByPublicId(any())).thenReturn(product);

        var response = productServiceImpl.getApprovedProductsByPublicIdList(prodPublicIdList);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("products.retrieved.success"));

    }

    @Test
    void testGetApprovedProductsPublicIdListUsingSku() {

        Product product = Product.builder().build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder()
                .productVariant(ProductVariant.builder()
                        .product(product)
                        .build())
                .build();
        List<VariantVersion> variantVersions = new ArrayList<>();
        variantVersions.add(variantVersion);
        List<Product> products = new ArrayList<>();
        products.add(product);

        List<String> skuList = Arrays.asList("SKU1", "SKU2", "SKU3");

        when(variantVersionInternalService.findAllBySkuIn(any())).thenReturn(variantVersions);
        when(productInternalService.findAllById(any())).thenReturn(products);

        var response = productServiceImpl.getApprovedProductsPublicIdListUsingSku(skuList);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("products.retrieved.success"));

    }

    @Test
    void testGetApprovedProductsPublicIdListUsingSkuError() {

        Product product = Product.builder().build();
        product.setId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder()
                .productVariant(ProductVariant.builder()
                        .product(product)
                        .build())
                .build();
        List<VariantVersion> variantVersions = new ArrayList<>();
        variantVersions.add(variantVersion);
        List<Product> products = new ArrayList<>();
        products.add(product);

        List<String> skuList = Arrays.asList("SKU1", "SKU2", "SKU3");

        when(variantVersionInternalService.findAllBySkuIn(any())).thenReturn(variantVersions);
        when(productInternalService.findAllById(any())).thenReturn(products);

        var response = productServiceImpl.getApprovedProductsPublicIdListUsingSku(skuList);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("product.not.found"));

    }

    /*
     * @Test
     * void createNewProductDuplicateNameTest() {
     * CreateProductRequestDto requestDto = buildProductRequestDto();
     * 
     * when(productInternalService.productNameIsNotUniqueToBrandAndManufacturer(any(
     * ), any(), any())).thenReturn(true);
     * 
     * assertThrows(NoResultException.class, () ->
     * productServiceImpl.createNewProduct(requestDto, Boolean.FALSE));
     * }
     */
    @Test
    void getProductsByProductCategoryIdsTest() {
        ProductCategory productCategory = EntityHelpers.buildProductCategory();
        Product product = EntityHelpers.buildProduct();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<Product> products = new PageImpl<>(List.of(product), request, 1);

        when(productCategoryInternalService.findProductCategoryByPublicIds(any()))
                .thenReturn(List.of(productCategory));
        when(productInternalService.getProductsByCategoryIds(any(), any())).thenReturn(products);

        var response = productServiceImpl.getProductsByProductCategoryIds(List.of(UUID.randomUUID()),
                1, 10);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService.getMessageByKey("products.retrieved.success"));
        assertThat(response.getData()).isEqualTo(products);
    }

    @Test
    void getAllProducts() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        Product product = EntityHelpers.buildProduct();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<Product> products = new PageImpl<>(List.of(product), request, 1);

        when(variantInternalService.getAllProductVariants()).thenReturn(List.of(productVariant));
        when(productInternalService.searchProducts(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(products);

        var result = productServiceImpl.getAllProducts(1, 10, "", null,
                null, null, null, null, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"));
    }

    @Test
    void getAllProductsTest() {
        List<UUID> uuidList = List.of(UUID.randomUUID());
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        Product product = EntityHelpers.buildProduct();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<Product> products = new PageImpl<>(List.of(product), request, 1);

        when(variantInternalService.getAllProductVariants()).thenReturn(List.of(productVariant));
        when(productInternalService.searchProducts(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(products);

        var result = productServiceImpl.getAllProducts(0, 10, "", "2023-04-08",
                "2023-04-10", uuidList, uuidList, uuidList, uuidList, uuidList);

        assertThat(result).isNotNull();
        assertThat(result.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.products.retrieved.successfully"));
    }

    @Test
    void saveImageUploadTemplateRequestMissingImageTest() {

        String createdBy = "test";

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setProductName("testProduct");
        productVariantDto.setVariantName("testVariant");
        productVariantDto.setManufacturerName("");
        productVariantDto.setBrandName("Adidas");
        productVariantDto.setProductCategoryName("Auto");
        productVariantDto.setCostPrice(new BigDecimal("1000"));
        productVariantDto.setVariantTypeName("testVariantType");
        productVariantDto.setCreatedBy(createdBy);

        ImageUploadTemplateRequest imageTemplateRequest = new ImageUploadTemplateRequest();
        imageTemplateRequest.setProductName("testProduct");
        imageTemplateRequest.setVariantName("testVariant");
        imageTemplateRequest.setManufacturerName("Nykaa");
        imageTemplateRequest.setBrand("Adidas");
        imageTemplateRequest.setProductCategory("Auto");
        imageTemplateRequest.setCostPrice(new BigDecimal(1000));
        imageTemplateRequest.setVariantType("testVariantType");
        imageTemplateRequest.setImageUrl1("");
        imageTemplateRequest.setImageUrl2("");

        given(manufacturerInternalService.findByManufacturerName(any())).willReturn(Optional.empty());
        given(brandInternalService.findByBrandName(any())).willReturn(Optional.of(getBrand()));
        given(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .willReturn(Optional.of(getProductCategory()));
        given(variantTypeInternalService.findVariantTypeByNameIgnoreCase(any()))
                .willReturn(Optional.of(getVariantType()));
        given(productInternalService.findProductByName(any())).willReturn(Optional.of(getProductDto()));
        given(failedProductsInternalService.saveAllFailedProducts(any())).willReturn(List.of(getFailedProducts()));
        given(productInternalService.saveAllProducts(any())).willReturn(List.of(getProductDto()));
        given(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .willReturn(List.of(variantAwaitingApproval()));
        given(imageCatalogInternalService.saveImageCatalogsToDb(any())).willReturn(List.of(getImageCatalog()));

        var response = productServiceImpl.saveUploadProductVariants(List.of(imageTemplateRequest), createdBy,
                UUID.randomUUID());
        assertThat(response).isNotNull();

    }

    @Test
    void saveImageUploadTemplateRequestVariantAwaitingApprovalNullTest() {

        String createdBy = "test";

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setProductName("testProduct");
        productVariantDto.setVariantName("testVariant");
        productVariantDto.setManufacturerName("");
        productVariantDto.setBrandName("Adidas");
        productVariantDto.setProductCategoryName("Auto");
        productVariantDto.setCostPrice(new BigDecimal("1000"));
        productVariantDto.setVariantTypeName("testVariantType");
        productVariantDto.setCreatedBy(createdBy);

        ImageUploadTemplateRequest imageTemplateRequest = new ImageUploadTemplateRequest();
        imageTemplateRequest.setProductName("testProduct");
        imageTemplateRequest.setVariantName("testVariant");
        imageTemplateRequest.setManufacturerName("Nykaa");
        imageTemplateRequest.setBrand("Adidas");
        imageTemplateRequest.setProductCategory("Auto");
        imageTemplateRequest.setCostPrice(new BigDecimal(1000));
        imageTemplateRequest.setVariantType("testVariantType");
        imageTemplateRequest.setImageUrl1("url");
        imageTemplateRequest.setImageUrl2("url");

        List<ProductVariant> variantListMock = Arrays.asList(ProductVariant.builder()
                .build());

        given(manufacturerInternalService.findByManufacturerName(any())).willReturn(Optional.empty());
        given(brandInternalService.findByBrandName(any())).willReturn(Optional.of(getBrand()));
        given(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .willReturn(Optional.of(getProductCategory()));
        given(variantTypeInternalService.findVariantTypeByNameIgnoreCase(any()))
                .willReturn(Optional.of(getVariantType()));
        given(productInternalService.findProductByName(any())).willReturn(Optional.of(getProductDto()));
        given(failedProductsInternalService.saveAllFailedProducts(any())).willReturn(List.of(getFailedProducts()));
        given(productInternalService.saveAllProducts(any())).willReturn(List.of(getProductDto()));
        given(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .willReturn(List.of(variantAwaitingApproval()));
        given(imageCatalogInternalService.saveImageCatalogsToDb(any())).willReturn(List.of(getImageCatalog()));
        given(productVariantInternalService.saveProductVariantToDb(anyList())).willReturn(variantListMock);
        given(locationClientInternalService.getCountryByPublicId(any())).willReturn(getCountryMock());

        given(pimVatProperties.getCountries()).willReturn(getPimVatProperties());

        var response = productServiceImpl.saveUploadProductVariants(List.of(imageTemplateRequest), createdBy,
                UUID.randomUUID());
        assertThat(response).isNotNull();

    }

    private Map<String, CountryProperties> getPimVatProperties() {
        Map<String, CountryProperties> countryPropertiesMap = Map.of("NGN", getPimVatCountryProperties());
        return countryPropertiesMap;
    }

    private CountryProperties getPimVatCountryProperties() {
        CountryProperties countryProperties = new CountryProperties();
        countryProperties.setDefaultVatMaxValue("1");
        countryProperties.setDefaultVatMinValue("0");
        countryProperties.setDefaultVatValue("1.5");
        return countryProperties;
    }

    @Test
    void saveImageUploadTemplateRequestImageURLTest() {

        String createdBy = "test";

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setProductName("testProduct");
        productVariantDto.setVariantName("testVariant");
        productVariantDto.setManufacturerName("manufacturer");
        productVariantDto.setBrandName("Adidas");
        productVariantDto.setProductCategoryName("Auto");
        productVariantDto.setCostPrice(new BigDecimal("1000"));
        productVariantDto.setVariantTypeName("testVariantType");
        productVariantDto.setCreatedBy(createdBy);

        ImageUploadTemplateRequest imageTemplateRequest = new ImageUploadTemplateRequest();
        imageTemplateRequest.setProductName("testProduct");
        imageTemplateRequest.setVariantName("testVariant");
        imageTemplateRequest.setManufacturerName("Nykaa");
        imageTemplateRequest.setBrand("Adidas");
        imageTemplateRequest.setProductCategory("Auto");
        imageTemplateRequest.setCostPrice(new BigDecimal(1000));
        imageTemplateRequest.setVariantType("testVariantType");
        imageTemplateRequest.setMinVat(BigDecimal.ZERO);
        imageTemplateRequest.setMaxVat(BigDecimal.ZERO);
        // imageTemplateRequest.setImageUrl1("http://testImage.jpg");
        imageTemplateRequest.setImageUrl2("http://testImage.jpg");

        given(manufacturerInternalService.findByManufacturerName(any())).willReturn(Optional.of(getManufacturer()));
        given(brandInternalService.findByBrandName(any())).willReturn(Optional.of(getBrand()));
        given(productCategoryInternalService.findProductCategoryByNameIgnoreCase(any()))
                .willReturn(Optional.of(getProductCategory()));
        given(variantTypeInternalService.findVariantTypeByNameIgnoreCase(any()))
                .willReturn(Optional.of(getVariantType()));
        given(productInternalService.findProductByName(any())).willReturn(Optional.of(getProductDto()));
        given(failedProductsInternalService.saveAllFailedProducts(any())).willReturn(List.of(getFailedProducts()));
        given(productInternalService.saveAllProducts(any())).willReturn(List.of(getProductDto()));
        given(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .willReturn(List.of(variantAwaitingApproval()));
        given(imageCatalogInternalService.saveImageCatalogsToDb(any())).willReturn(List.of(getImageCatalog()));
        given(productInternalService.saveProductToDb(any())).willReturn(getProductDto());
        given(locationClientInternalService.getCountryByPublicId(any())).willReturn(getCountryMock());
        given(pimVatProperties.getCountries()).willReturn(getPimVatProperties());

        var response = productServiceImpl.saveUploadProductVariants(List.of(imageTemplateRequest), createdBy,
                UUID.randomUUID());
        assertThat(response).isNotNull();
    }

    @Test
    void savePriceTemplateRequestTest() {
        PriceTemplateRequest priceTemplateRequest = PriceTemplateRequest.builder()
                .manufacturerName("manufacturer")
                .brand("brand")
                .productCategory("productCategory")
                .subCategory("subCategory")
                .productName("product")
                .variantType("vt")
                .variantName("vn")
                .moq1(10L)
                .moq1Price(BigDecimal.ONE)
                .moq2(10L)
                .moq2Price(BigDecimal.ONE)
                .listingPrice(BigDecimal.ONE)
                .costPrice(BigDecimal.ONE)
                .weight(100.1)
                .material("mt")
                .color("red")
                .dimension("dim")
                .build();
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();

        when(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(List.of(variantAwaitingApproval)))
                .thenReturn(List.of(variantAwaitingApproval));

        productServiceImpl.savePriceTemplateRequest(List.of(priceTemplateRequest), "creator");
    }

    @Test
    void saveStockUpdateTemplateRequestTest() {
        StockUpdateTemplateRequest stockUpdateTemplateRequest = StockUpdateTemplateRequest.builder()
                .businessName("bn")
                .manufacturerName("mn")
                .brand("br")
                .productCategory("pc")
                .subCategory("sc")
                .productName("pn")
                .variantType("vt")
                .variantName("vn")
                .quantityToUpload(BigInteger.ONE)
                .listingPrice(BigDecimal.ONE)
                .costPrice(BigDecimal.ONE)
                .weight(100.1)
                .build();
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();

        when(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .thenReturn(List.of(variantAwaitingApproval));

        productServiceImpl.saveStockUpdateTemplateRequest(List.of(stockUpdateTemplateRequest), "creator");
    }

    @Test
    void saveCategoryUploadTemplateRequestTest() {
        CategoryUploadTemplateRequest categoryUploadTemplateRequest = CategoryUploadTemplateRequest.builder()
                .manufacturerName("Sample Manufacturer")
                .brand("Sample Brand")
                .productCategory("Sample Category")
                .subCategory("Sample Sub-category")
                .productName("Sample Product Name")
                .variantType("Sample Variant Type")
                .variantName("Sample Variant Name")
                .listingPrice(BigDecimal.valueOf(99.99))
                .costPrice(BigDecimal.valueOf(49.99))
                .weight(1.5)
                .measurementUnit("Sample Unit")
                .packagingType("Sample Packaging Type")
                .moq1(10L)
                .moq1Price(BigDecimal.valueOf(9.99))
                .moq2(20L)
                .moq2Price(BigDecimal.valueOf(19.99))
                .packaging("Sample Packaging")
                .ageGroup("Sample Age Group")
                .gender("Sample Gender")
                .size("Sample Size")
                .colour("Sample Colour")
                .model("Sample Model")
                .powerCapacity("Sample Power Capacity")
                .fuelType("Sample Fuel Type")
                .operatingSystem("Sample OS")
                .screenSize("Sample Screen Size")
                .numberOfSims(2)
                .internalStorage("Sample Storage")
                .ram("Sample RAM")
                .batteryCapacity("Sample Battery Capacity")
                .material("Sample Material")
                .dimension("Sample Dimension")
                .power("Sample Power")
                .capacity("Sample Capacity")
                .deviceModelSize("Sample Device Model Year")
                .displaySize("Sample Display Size")
                .displayTechnology("Sample Display Technology")
                .processorType("Sample Processor Type")
                .storage("Sample Storage")
                .memory("Sample Memory")
                .battery("Sample Battery")
                .imageUrl1("Sample Image URL 1")
                .imageUrl2("Sample Image URL 2")
                .build();

        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();

        when(variantAwaitingApprovalInternalService.saveVariantAwaitingApprovalToDb(any()))
                .thenReturn(List.of(variantAwaitingApproval));

        productServiceImpl.saveCategoryUploadTemplateRequest(List.of(categoryUploadTemplateRequest), "creator");
    }

    @Test
    void deleteProductModelNotFoundExceptionTest() {
        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);
        responseDto.setStatus(Status.DELETED.name());

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.deleteProduct(any())).willReturn(responseDto);

        assertThrows(ModelNotFoundException.class, () -> productServiceImpl.deleteProduct(publicId));
    }

    @Test
    void archiveUnarchiveProductValidatorExceptionTest() {

        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);
        responseDto.setStatus(Status.INACTIVE.name());

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.updateProductArchiveStatus(any(), any())).willReturn(responseDto);
        given(variantInternalService.updateProductVariantsArchiveStatus(any(), any()))
                .willReturn(List.of(new ProductVariant()));

        assertThrows(ValidatorException.class,
                () -> productServiceImpl.updateProductArchiveStatus(publicId, Status.INACTIVE.name()));

    }

    @Test
    void archiveUnarchiveProductElseCaseTest() {

        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);
        responseDto.setStatus(Status.INACTIVE.name());

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.updateProductArchiveStatus(any(), any())).willReturn(responseDto);
        given(variantInternalService.updateProductVariantsArchiveStatus(any(), any()))
                .willReturn(List.of(new ProductVariant()));

        var archivedProduct = productServiceImpl.updateProductArchiveStatus(publicId, Status.ACTIVE.name());
        assertThat(archivedProduct).isNotNull();
        assertThat(archivedProduct.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("product.unarchived.successfully"));
        assertThat(archivedProduct.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    @Test
    void archiveUnarchiveProductElseCaseValidationErrorTest() {

        UUID publicId = UUID.randomUUID();
        Product responseDto = convertToResponseDto(publicId);

        given(productInternalService.findByPublicId(any())).willReturn(responseDto);
        given(productInternalService.updateProductArchiveStatus(any(), any())).willReturn(responseDto);
        given(variantInternalService.updateProductVariantsArchiveStatus(any(), any()))
                .willReturn(List.of(new ProductVariant()));

        assertThrows(ValidatorException.class,
                () -> productServiceImpl.updateProductArchiveStatus(publicId, Status.ACTIVE.name()));

    }

    @Test
    void testGetApprovedProductsPublicIdListUsingSkuProductNotFound() {

        Product product = Product.builder().build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        VariantVersion variantVersion = VariantVersion.builder()
                .productVariant(ProductVariant.builder()
                        .product(product)
                        .build())
                .build();
        List<VariantVersion> variantVersions = new ArrayList<>();
        variantVersions.add(variantVersion);
        List<Product> products = new ArrayList<>();
        products.add(product);

        List<String> skuList = Arrays.asList("SKU1", "SKU2", "SKU3");

        when(variantVersionInternalService.findAllBySkuIn(any())).thenReturn(variantVersions);
        when(productInternalService.findAllById(any())).thenReturn(List.of());

        var response = productServiceImpl.getApprovedProductsPublicIdListUsingSku(skuList);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(messageSourceService
                .getMessageByKey("product.not.found"));

    }

    @Test
    void uploadProductUsingExcelPriceUploadTemplateTest() throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("price-upload-template.csv");
        MockMultipartFile file = new MockMultipartFile("price-upload-template.csv", "price-upload-template.csv",
                "multipart/form-data", inputStream);

        var response = productServiceImpl.uploadProductUsingExcel(file, "creator", UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"));
    }

    @Test
    void uploadProductUsingExcelStockUploadTemplateTest() throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("stock-upload-template.csv");
        MockMultipartFile file = new MockMultipartFile("stock-upload-template.csv", "stock-upload-template.csv",
                "multipart/form-data", inputStream);

        var response = productServiceImpl.uploadProductUsingExcel(file, "creator", UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"));
    }

    @Test
    void uploadProductUsingExcelImageUploadTemplateTest() throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("image-upload-template.csv");
        MockMultipartFile file = new MockMultipartFile("image-upload-template.csv", "image-upload-template.csv",
                "multipart/form-data", inputStream);

        var response = productServiceImpl.uploadProductUsingExcel(file, "creator", UUID.randomUUID());

        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("bulk.upload.of.product.in.process"));
    }

    @Test
    void uploadProductUsingExcelFileNotFoundTest() throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("upload-template.csv");
        MockMultipartFile file = new MockMultipartFile("upload-template.csv", "upload-template.csv",
                "multipart/form-data", inputStream);

        assertThrows(FileNotFoundException.class,
                () -> productServiceImpl.uploadProductUsingExcel(file, "creator", UUID.randomUUID()));
    }

    @Test
    void createNewProductWithWhiteSpaceName() {
        List<CreateVariantRequestDto> createVariantRequestDto = new ArrayList<>();
        CreateVariantRequestDto variantRequestDto = buildVariantRequestDto();
        createVariantRequestDto.add(variantRequestDto);

        CreateProductRequestDto requestDto = buildProductRequestDto();

        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setPublicId(requestDto.getManufacturerPublicId());
        manufacturer.setId(UUID.randomUUID());

        Brand brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();

        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("Adidas")
                .description("We are sports industry")
                .depth(2)
                .imageUrl("")
                .status(Status.ACTIVE)
                .build();

        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName("w1")
                .description("new")
                .status(Status.ACTIVE)
                .build();

        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Inches")
                .description("nothing")
                .abbreviation("abb")
                .status(Status.ACTIVE.name())
                .build();
        Product product = ProductHelper.buildProduct(requestDto, brand, manufacturer, productCategory, measuringUnit,
                warrantyType);
        UUID publicId = UUID.randomUUID();
        requestDto.getVariants().get(0).setCountryPublicId(publicId);

        given(productInternalService.checkIfNameExist(requestDto.getProductName())).willReturn(false);
        when(productInternalService.productNameIsNotUniqueToBrandAndManufacturer(requestDto.getProductName(), brand,
                manufacturer)).thenReturn(false);
        given(manufacturerInternalService.findByPublicId(requestDto.getManufacturerPublicId()))
                .willReturn(Optional.of(manufacturer));
        given(brandInternalService.findByPublicId(requestDto.getBrandPublicId())).willReturn(brand);
        given(warrantyTypeInternalService.findByPublicId(UUID.fromString(requestDto.getWarrantyTypePublicId())))
                .willReturn(warrantyType);
        given(productCategoryInternalService.findProductCategoryByPublicId(requestDto.getCategoryPublicId()))
                .willReturn(productCategory);
        given(measuringUnitInternalService.findByPublicId(requestDto.getMeasurementUnitPublicId()))
                .willReturn(measuringUnit);
        given(locationClientInternalService.getCountryByPublicId(any())).willReturn(getCountryMock());
        given(locationClientInternalService.findCountryByPublicIds(any())).willReturn(getCountryListMock(publicId));
        given(pimVatProperties.getCountries()).willReturn(getPimVatProperties());
        var createdResponse = productServiceImpl.createNewProduct(requestDto, Boolean.FALSE, "NGN");
        CreateProductResponseDto responseDto = (CreateProductResponseDto) createdResponse.getData();
        assertThat(responseDto.productCategoryName().equals(requestDto.getProductName().trim()));

    }

    @Test
    void updateProductWhiteSpaceName() {
        UUID publicId = UUID.randomUUID();
        ProductCategory productCategory = getProductCategory();
        Brand brand = getBrand();
        Manufacturer manufacturer = getManufacturer();
        WarrantyType warrantyType = getWarrantyType();
        MeasuringUnit measuringUnit = getMeasuringUnit();

        Product product = new Product();
        product.setProductName("abc");
        product.setVersion(BigInteger.ONE);

        Product productResponse = Product.builder()
                .productName("TEST PRODUCT")
                .productCategory(productCategory)
                .brand(brand)
                .vated(false)
                .manufacturer(manufacturer)
                .warrantyType(warrantyType)
                .measurementUnit(measuringUnit)
                .status(Status.ACTIVE.name())
                .productListing("MERCHBUY")
                .build();

        given(productInternalService.findByPublicId(publicId)).willReturn(product);
        given(productInternalService.saveProductToDb(any())).willReturn(productResponse);

        AppResponse response = productServiceImpl.updateProductByPublicId(publicId, getUpdateProductRequestDto());
        CreateProductResponseDto responseDto = (CreateProductResponseDto) response.getData();
        assertThat(responseDto.productCategoryName().equals(getUpdateProductRequestDto().getProductName().trim()));
    }

}
