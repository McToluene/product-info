package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.repository.VariantTypeRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantTypeInternalServiceImpl;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantTypeInternalServiceImplTest {

    @Mock
    private VariantTypeRepository variantTyeRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private VariantTypeInternalServiceImpl variantTypeInternalService;

    @Test
    void saveVariantTypeToDb() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.save(variantType)).thenReturn(variantType);

        var result = variantTypeInternalService.saveVariantTypeToDb(variantType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

    @Test
    void saveVariantTypeToDbException() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.save(variantType)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantTypeInternalService.saveVariantTypeToDb(variantType));
    }

    @Test
    void saveNewVariantType() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.save(variantType)).thenReturn(variantType);

        var result = variantTypeInternalService.saveNewVariantType(variantType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);

    }

    @Test
    void findVariantTypeByPublicIdTest() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.findByPublicId(any())).thenReturn(Optional.of(variantType));

        var result = variantTypeInternalService.findVariantTypeByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

    @Test
    void findVariantTypeByPublicIdExceptionTest() {

        when(variantTyeRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantTypeInternalService.findVariantTypeByPublicId(UUID.randomUUID()));
    }

    @Test
    void findVariantTypeByNameTest() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.findByVariantTypeName(any())).thenReturn(Optional.of(variantType));

        var result = variantTypeInternalService.findVariantTypeByName("name");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

    @Test
    void findVariantTypeByNameExceptionTest() {

        when(variantTyeRepository.findByVariantTypeName(any())).thenReturn(Optional.empty());

        var result = variantTypeInternalService.findVariantTypeByName("name");

        assertThat(result).isNull();
    }

    @Test
    void checkIfNameExistTest() {
        VariantType variantType = EntityHelpers.buildVariantType();

        when(variantTyeRepository.findByVariantTypeName(any())).thenReturn(Optional.of(variantType));

        var result = variantTypeInternalService.checkIfNameExist("name");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(true);
    }

    @Test
    void checkIfNameExistFalseTest() {
        VariantType variantType = EntityHelpers.buildVariantType();

        when(variantTyeRepository.findByVariantTypeName(any())).thenReturn(Optional.empty());

        var result = variantTypeInternalService.checkIfNameExist("name");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(false);
    }

    @Test
    void getAllVariantType() {
        VariantType variantType = EntityHelpers.buildVariantType();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantType> variantTypes = new PageImpl<>(List.of(variantType), request, 1);

        when(variantTyeRepository.findAll(request)).thenReturn(variantTypes);

        var result = variantTypeInternalService.getAllVariantTypes(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantTypes);

    }

    @Test
    void deleteVariantType() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.save(variantType)).thenReturn(variantType);

        var result = variantTypeInternalService.deleteVariantType(variantType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

    @Test
    void findVariantTypeById() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.findById(any())).thenReturn(Optional.of(variantType));

        var result = variantTypeInternalService.findVariantTypeById(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

    @Test
    void findVariantTypeByIdException() {

        when(variantTyeRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantTypeInternalService.findVariantTypeById(UUID.randomUUID()));

    }

    @Test
    void findVariantTypeByNameIgnoreCase() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.findByVariantTypeNameIgnoreCase(anyString())).thenReturn(Optional.of(variantType));

        var result = variantTypeInternalService.findVariantTypeByNameIgnoreCase("test");

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(variantType);
    }

    @Test
    void updateExistingVariantType() {
        VariantType variantType = EntityHelpers.buildVariantType();
        when(variantTyeRepository.save(variantType)).thenReturn(variantType);

        var result = variantTypeInternalService.updateExistingVariantType(variantType);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantType);
    }

}
