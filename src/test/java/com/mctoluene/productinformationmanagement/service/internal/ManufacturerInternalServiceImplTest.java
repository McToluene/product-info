package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.repository.ManufacturerRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ManufacturerInternalServiceImpl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ManufacturerInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @InjectMocks
    private ManufacturerInternalServiceImpl manufacturerInternalService;

    @Test
    void saveNewManufacturerTest() {
        Manufacturer manufacturer = buildManufacturer();
        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(manufacturerRepository.save(any())).thenReturn(manufacturer);

        var result = manufacturerInternalService.saveNewManufacturer(manufacturer);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(manufacturer);
    }

    @Test
    void saveNewManufacturerValidatorExceptionTest() {
        Manufacturer manufacturer = buildManufacturer();
        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(manufacturerRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> manufacturerInternalService.saveNewManufacturer(manufacturer));
    }

    @Test
    void saveNewManufacturerValidatorExceptionWhenManufacturerExistsTest() {
        Manufacturer manufacturer = buildManufacturer();
        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.of(manufacturer));
        when(manufacturerRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> manufacturerInternalService.saveNewManufacturer(manufacturer));
    }

    @Test
    void findByPublicIdTest() {
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var result = manufacturerInternalService.findByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.get());
    }

    @Test
    void findByPublicIdModelNotFoundTest() {

        when(manufacturerRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> manufacturerInternalService.findByPublicId(UUID.randomUUID()));

    }

    @Test
    void findAllByTest() {
        PageRequest request = PageRequest.of(1, 10);
        Manufacturer manufacturer = buildManufacturer();
        Page<Manufacturer> page = new PageImpl<>(List.of(manufacturer), request, 1);

        when(manufacturerRepository.findAllBy(request)).thenReturn(page);

        var result = manufacturerInternalService.findAllBy(request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(manufacturer));
    }

    @Test
    void updateManufacturerTest() {
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.save(any())).thenReturn(manufacturer);

        var result = manufacturerInternalService.updateManufacturer(manufacturer);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(manufacturer);
    }

    @Test
    void updateManufacturerValidatorExceptionTest() {
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> manufacturerInternalService.updateManufacturer(manufacturer));
    }

    @Test
    void deleteManufacturerTest() {
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.save(any())).thenReturn(manufacturer);

        var result = manufacturerInternalService.deleteManufacturer(manufacturer);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(manufacturer);
    }

    @Test
    void findByPublicIdAndManufacturerTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(manufacturerRepository.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var result = manufacturerInternalService.findByPublicIdAndManufacturer(publicId,
                buildUpdateManufacturerRequestDto());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(manufacturer);
    }

    @Test
    void findByPublicIdAndManufacturerManufacturerAlreadyExistsTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.of(manufacturer));
        when(manufacturerRepository.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        Assertions.assertThrows(ValidatorException.class, () -> manufacturerInternalService
                .findByPublicIdAndManufacturer(publicId, buildUpdateManufacturerRequestDto()));
    }

    @Test
    void findByPublicIdAndManufacturerModelNotFoundTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = buildManufacturer();

        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(manufacturerRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> manufacturerInternalService
                .findByPublicIdAndManufacturer(publicId, buildUpdateManufacturerRequestDto()));
    }

    @Test
    void findByManufacturerNameTest() {
        Manufacturer manufacturer = buildManufacturer();
        when(manufacturerRepository.findByManufacturerNameIgnoreCase(any())).thenReturn(Optional.of(manufacturer));

        var result = manufacturerInternalService.findByManufacturerName("test");

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(manufacturer);
    }

    private UpdateManufacturerRequestDto buildUpdateManufacturerRequestDto() {
        return UpdateManufacturerRequestDto.builder()
                .manufacturerName("new name")
                .description("desc")
                .lastModifiedBy("test")
                .build();
    }

    private Manufacturer buildManufacturer() {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .description("desc")
                .status(Status.ACTIVE)
                .build();

        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setVersion(BigInteger.ZERO);
        manufacturer.setCreatedBy("test");
        manufacturer.setCreatedDate(LocalDateTime.now());
        manufacturer.setLastModifiedBy("test");
        manufacturer.setLastModifiedDate(LocalDateTime.now());

        return manufacturer;
    }

}
