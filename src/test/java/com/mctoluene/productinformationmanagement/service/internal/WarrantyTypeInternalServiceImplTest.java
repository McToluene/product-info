package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.repository.WarrantyTypeRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.WarrantyTypeInternalServiceImpl;

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
public class WarrantyTypeInternalServiceImplTest {

    @Mock
    private WarrantyTypeRepository warrantyTypeRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private WarrantyTypeInternalServiceImpl warrantyTypeInternalService;

    @Test
    void saveWarrantyTypeToDbTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.findByWarrantyTypeNameIgnoreCase(any()))
                .thenReturn(Optional.empty());
        when(warrantyTypeRepository.save(any())).thenReturn(warrantyType);

        var result = warrantyTypeInternalService.saveWarrantyTypeToDb(warrantyType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void saveWarrantyTypeToDbExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.findByWarrantyTypeNameIgnoreCase(any()))
                .thenReturn(Optional.of(warrantyType));
        when(warrantyTypeRepository.save(any())).thenReturn(warrantyType);

        Assertions.assertThrows(ValidatorException.class,
                () -> warrantyTypeInternalService.saveWarrantyTypeToDb(warrantyType));
    }

    @Test
    void saveNewWarrantyTypeToDbExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> warrantyTypeInternalService.saveNewWarrantyTypeToDb(warrantyType));
    }

    @Test
    void saveNewWarrantyTypeToDbTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(any())).thenReturn(warrantyType);

        var result = warrantyTypeInternalService.saveNewWarrantyTypeToDb(warrantyType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void deleteWarrantyTypeTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(any())).thenReturn(warrantyType);

        var result = warrantyTypeInternalService.deleteWarrantyType(warrantyType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void deleteWarrantyTypeExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> warrantyTypeInternalService.deleteWarrantyType(warrantyType));
    }

    @Test
    void findByPublicIdTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.of(warrantyType));

        var result = warrantyTypeInternalService.findByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void findByPublicIdExceptionTest() {
        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> warrantyTypeInternalService.findByPublicId(UUID.randomUUID()));
    }

    @Test
    void findWarrantyTypeByPublicIdTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.of(warrantyType));

        var result = warrantyTypeInternalService.findWarrantyTypeByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void findWarrantyTypeByPublicIdExceptionTest() {
        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> warrantyTypeInternalService.findWarrantyTypeByPublicId(UUID.randomUUID()));
    }

    @Test
    void updateWarrantyTypeTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(warrantyType)).thenReturn(warrantyType);

        var result = warrantyTypeInternalService.updateWarrantyType(warrantyType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void updateWarrantyTypeExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();

        when(warrantyTypeRepository.save(warrantyType)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> warrantyTypeInternalService.updateWarrantyType(warrantyType));
    }

    @Test
    void findByPublicIdAndWarrantyTypeTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();
        UpdateWarrantyTypeRequestDto requestDto = UpdateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("type")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(warrantyTypeRepository.findByWarrantyTypeNameIgnoreCaseAndPublicIdNot(any(), any()))
                .thenReturn(Optional.empty());
        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.of(warrantyType));

        var result = warrantyTypeInternalService.findByPublicIdAndWarrantyType(UUID.randomUUID(),
                requestDto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyType);
    }

    @Test
    void findByPublicIdAndWarrantyTypeExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();
        UpdateWarrantyTypeRequestDto requestDto = UpdateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("type")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(warrantyTypeRepository.findByWarrantyTypeNameIgnoreCaseAndPublicIdNot(any(), any()))
                .thenReturn(Optional.empty());
        when(warrantyTypeRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> warrantyTypeInternalService.findByPublicIdAndWarrantyType(UUID.randomUUID(), requestDto));
    }

    @Test
    void findByPublicIdAndWarrantyTypeValidatorExceptionTest() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();
        UpdateWarrantyTypeRequestDto requestDto = UpdateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("type")
                .description("desc")
                .lastModifiedBy("anwar")
                .build();

        when(warrantyTypeRepository.findByWarrantyTypeNameIgnoreCaseAndPublicIdNot(any(), any()))
                .thenReturn(Optional.of(warrantyType));

        Assertions.assertThrows(ValidatorException.class,
                () -> warrantyTypeInternalService.findByPublicIdAndWarrantyType(UUID.randomUUID(), requestDto));
    }

    @Test
    void getAllWarrantyType() {
        WarrantyType warrantyType = EntityHelpers.buildWarrantyType();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<WarrantyType> warrantyTypes = new PageImpl<>(List.of(warrantyType), request, 1);

        when(warrantyTypeRepository.findAll(request)).thenReturn(warrantyTypes);

        var result = warrantyTypeInternalService.getAllWarrantyType(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(warrantyTypes);

    }

}
