package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.brand.CreateBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.brand.BrandResponseDto;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.BrandInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.BrandServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ManufacturerInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class BrandServiceImplTest {

    @Mock
    private BrandInternalServiceImpl brandInternalService;

    @Mock
    private ManufacturerInternalServiceImpl manufacturerInternalService;

    @Mock
    private ProductInternalService productInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void createBrand() {
        CreateBrandRequestDto requestDto = CreateBrandRequestDto.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .createdBy("Dilip")
                .manufacturerId(UUID.randomUUID())
                .build();

        given(brandInternalService.saveNewBrand(any(Brand.class))).willReturn(brand());
        given(manufacturerInternalService.findByPublicId(any())).willReturn(Optional.of(manufacturer()));
        var createdResponse = brandService.createBrand(requestDto);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brand.created.successfully"));
    }

    private Manufacturer manufacturer() {
        var manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve Beauty products")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setPublicId((UUID.randomUUID()));
        return manufacturer;
    }

    private Brand brand() {
        var brand = Brand.builder()
                .brandName("Adidas")
                .description("We are sports industry")
                .status(Status.ACTIVE)
                .build();

        brand.setVersion(BigInteger.ZERO);
        brand.setLastModifiedBy("Dilip");
        brand.setCreatedBy("Dilip");
        brand.setCreatedBy("Dilip");
        brand.setLastModifiedDate(LocalDateTime.now());
        brand.setCreatedDate(LocalDateTime.now());
        brand.setManufacturer(manufacturer());
        return brand;
    }

    @Test
    void getBrand() {

        UUID publicId = UUID.randomUUID();
        given(brandInternalService.findByPublicId(publicId)).willReturn(brand());
        given(brandInternalService.findByPublicId(publicId)).willReturn(brand());
        var createdResponse = brandService.getBrandByPublicId(publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brand.fetched.successfully"));
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brand.fetched.successfully"));
    }

    @Test
    void getBrands() {
        int page = 0;
        int size = 5;
        int count = 30;
        PageRequest pageRequest = PageRequest.of(page, size);

        given(brandInternalService.findAllBy(any(Pageable.class)))
                .willReturn((PageImpl<Brand>) getBrandResponse(pageRequest, count));
        given(brandInternalService.findAllBy(any(Pageable.class)))
                .willReturn((PageImpl<Brand>) getBrandResponse(pageRequest, count));
        var createdResponse = brandService.getBrands(page, size);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brands.retrieved.successfully"));
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brands.retrieved.successfully"));
    }

    private Page<Brand> getBrandResponse(PageRequest pageRequest, int count) {
        List<Brand> brandResponse = new ArrayList<>();
        final int start = (int) pageRequest.getOffset();
        final int end = Math.min((start + pageRequest.getPageSize()), brandResponse.size());
        Page<Brand> page = new PageImpl<>(brandResponse.subList(start, end), pageRequest, brandResponse.size());
        for (int i = 0; i <= count; i++) {

            Brand brand = Brand.builder().brandName("Brand-" + i)
                    .status(Status.ACTIVE)
                    .description("No description needed")
                    .build();

            brand.setPublicId(UUID.randomUUID());
            brand.setVersion(BigInteger.ZERO);
            brand.setLastModifiedBy("Dilip");
            brand.setLastModifiedDate(LocalDateTime.now());
            brand.setCreatedBy("Dilip");
            brand.setCreatedDate(LocalDateTime.now());

            brandResponse.add(brand);

        }
        return page;
    }

    @Test
    void editBrand() {
        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("PUMA")
                .description("We are sports items manufacturer")
                .lastModifiedBy("Dilip")
                .build();

        UUID publicId = UUID.randomUUID();
        Brand responseDto = brand();
        given(brandInternalService.findByPublicIdAndBrandName(any(), any())).willReturn(responseDto);
        given(brandInternalService.updateBrand(any())).willReturn(responseDto);
        given(manufacturerInternalService.findByPublicId(any())).willReturn(Optional.of(manufacturer()));
        given(manufacturerInternalService.findByPublicId(any())).willReturn(Optional.of(manufacturer()));
        var editResponse = brandService.editBrand(publicId, requestDto);
        assertThat(editResponse).isNotNull();
    }

    @Test
    void deleteBrand() {
        UUID publicId = UUID.randomUUID();
        Brand responseDto = brand();
        given(brandInternalService.findByPublicId(publicId)).willReturn(responseDto);
        given(productInternalService.checkIfBrandIsInUse(publicId)).willReturn(true);
        given(brandInternalService.deleteBrand(any(Brand.class))).willReturn(responseDto);
        var deleteResponse = brandService.deleteBrand(publicId);
        assertThat(deleteResponse).isNotNull();
    }

    @Test
    void createBrandWithWhiteSpaceName() {
        CreateBrandRequestDto requestDto = CreateBrandRequestDto.builder()
                .brandName("  Adidas  ")
                .description("We are sports industry")
                .createdBy("TEST")
                .manufacturerId(UUID.randomUUID())
                .build();

        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("test")
                .build();
        manufacturer.setPublicId(requestDto.getManufacturerId());
        Brand responseDto = Brand.builder()
                .brandName("ADIDAS")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();
        responseDto.setPublicId(UUID.randomUUID());
        given(brandInternalService.saveNewBrand(any(Brand.class))).willReturn(responseDto);
        given(manufacturerInternalService.findByPublicId(any())).willReturn(Optional.of(manufacturer()));
        var createdResponse = brandService.createBrand(requestDto);
        BrandResponseDto brand = (BrandResponseDto) createdResponse.getData();
        assertThat(brand.brandName().equals(requestDto.getBrandName().trim()));

    }

    @Test
    void editBrandWithWhiteSpaceName() {
        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("  PUMA  ")
                .description("We are sports items manufacturer")
                .lastModifiedBy("TEST")
                .manufacturerId(UUID.randomUUID())
                .build();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("test")
                .build();
        manufacturer.setPublicId(requestDto.getManufacturerId());
        UUID publicId = UUID.randomUUID();
        Brand responseDto = Brand.builder()
                .brandName("ADIDAS")
                .status(Status.ACTIVE)
                .manufacturer(manufacturer)
                .build();
        responseDto.setPublicId(publicId);

        Brand brand = new Brand();
        brand.setBrandName("PUMA");
        brand.setPublicId(publicId);
        brand.setStatus(Status.ACTIVE);
        brand.setManufacturer(manufacturer);
        given(brandInternalService.findByPublicIdAndBrandName(any(), any())).willReturn(responseDto);
        given(brandInternalService.updateBrand(any())).willReturn(brand);
        given(manufacturerInternalService.findByPublicId(any())).willReturn(Optional.of(manufacturer()));
        var editResponse = brandService.editBrand(publicId, requestDto);
        BrandResponseDto brandResponseDto = (BrandResponseDto) editResponse.getData();
        assertThat(brandResponseDto.brandName().equals(requestDto.getBrandName().trim()));
    }

    @Test
    void getBrandsByManufacture() {
        int page = 0;
        int size = 5;
        int count = 30;
        PageRequest pageRequest = PageRequest.of(page, size);
        Manufacturer manufacturer = manufacturer();
        manufacturer.setBrands(List.of(brand()));
        given(manufacturerInternalService.findByPublicId(any()))
                .willReturn(Optional.of(manufacturer));
        given(brandInternalService.findByManufacturer(any(), any(Pageable.class)))
                .willReturn(getBrandResponse(pageRequest, count));

        var createdResponse = brandService.getBrandsByManufacturer(UUID.randomUUID(), page, size);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brands.retrieved.successfully"));
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brands.retrieved.successfully"));
    }

}