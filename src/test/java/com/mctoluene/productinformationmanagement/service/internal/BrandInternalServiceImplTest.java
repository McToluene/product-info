package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.request.brand.EditBrandRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.repository.BrandRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.BrandInternalServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BrandInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandInternalServiceImpl brandInternalService;

    @Test
    void saveNewBrandTest() {
        Brand brand = EntityHelpers.buildBrand();
        when(brandRepository.findByBrandNameIgnoreCase(any()))
                .thenReturn(Optional.empty());
        when(brandRepository.save(brand))
                .thenReturn(brand);

        var result = brandInternalService.saveNewBrand(brand);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void saveNewBrandValidatorExceptionTest() {
        Brand brand = EntityHelpers.buildBrand();
        when(brandRepository.findByBrandNameIgnoreCase(any()))
                .thenReturn(Optional.of(brand));
        when(brandRepository.save(brand))
                .thenReturn(brand);

        Assertions.assertThrows(ValidatorException.class, () -> brandInternalService.saveNewBrand(brand));
    }

    @Test
    void saveNewBrandUnProcessableEntityExceptionTest() {
        Brand brand = EntityHelpers.buildBrand();
        when(brandRepository.findByBrandNameIgnoreCase(any()))
                .thenReturn(Optional.empty());
        when(brandRepository.save(brand))
                .thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class, () -> brandInternalService.saveNewBrand(brand));
    }

    @Test
    void findByPublicIdTest() {
        Brand brand = EntityHelpers.buildBrand();
        when(brandRepository.findByPublicId(any()))
                .thenReturn(Optional.of(brand));

        var result = brandInternalService.findByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void findByPublicIdNotFoundTest() {

        when(brandRepository.findByPublicId(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> brandInternalService.findByPublicId(UUID.randomUUID()));
    }

    @Test
    void findAllByTest() {
        Brand brand = EntityHelpers.buildBrand();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<Brand> brands = new PageImpl<>(List.of(brand), request, 1);

        when(brandRepository.findAllBy(request))
                .thenReturn(brands);

        var result = brandInternalService.findAllBy(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brands);
    }

    @Test
    void updateBrandTest() {
        Brand brand = EntityHelpers.buildBrand();

        when(brandRepository.save(any())).thenReturn(brand);

        var result = brandInternalService.updateBrand(brand);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void updateBrandExceptionTest() {
        Brand brand = EntityHelpers.buildBrand();

        when(brandRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class, () -> brandInternalService.updateBrand(brand));
    }

    @Test
    void findByPublicIdAndBrandNameTest() {
        Brand brand = EntityHelpers.buildBrand();
        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("new Name")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(brandRepository.findByBrandNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(brandRepository.findByPublicId(any())).thenReturn(Optional.of(brand));

        var result = brandInternalService.findByPublicIdAndBrandName(UUID.randomUUID(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void findByPublicIdAndBrandNameValidatorExceptionTest() {
        Brand brand = EntityHelpers.buildBrand();
        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("new Name")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(brandRepository.findByBrandNameIgnoreCase(any())).thenReturn(Optional.of(brand));
        when(brandRepository.findByPublicId(any())).thenReturn(Optional.of(brand));

        Assertions.assertThrows(ValidatorException.class,
                () -> brandInternalService.findByPublicIdAndBrandName(UUID.randomUUID(), requestDto));
    }

    @Test
    void findByPublicIdAndBrandNameNotFoundExceptionTest() {
        Brand brand = EntityHelpers.buildBrand();
        EditBrandRequestDto requestDto = EditBrandRequestDto.builder()
                .brandName("new Name")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(brandRepository.findByBrandNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(brandRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> brandInternalService.findByPublicIdAndBrandName(UUID.randomUUID(), requestDto));
    }

    @Test
    void findByBrandNameTest() {
        Brand brand = EntityHelpers.buildBrand();

        when(brandRepository.findByBrandNameIgnoreCase(any())).thenReturn(Optional.of(brand));

        var result = brandInternalService.findByBrandName("name");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(brand));
    }

    @Test
    void deleteBrandTest() {
        Brand brand = EntityHelpers.buildBrand();

        when(brandRepository.save(any())).thenReturn(brand);

        var result = brandInternalService.deleteBrand(brand);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void createNewBrandDuplicateNameTest() {

        Brand brand = EntityHelpers.buildBrand();

        when(brandRepository.findByBrandNameIgnoreCase(any())).thenReturn(Optional.of(brand));

        Assertions.assertThrows(ValidatorException.class, () -> brandInternalService.saveNewBrand(brand));
    }

    @Test
    void findByManufacturerPublicIdTest() {
        Brand brand = EntityHelpers.buildBrand();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<Brand> brands = new PageImpl<>(List.of(brand), request, 1);

        when(brandRepository.findByManufacturer(any(), any()))
                .thenReturn(brands);

        var result = brandInternalService.findByManufacturer(Manufacturer.builder().build(), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(brands);
    }

}
