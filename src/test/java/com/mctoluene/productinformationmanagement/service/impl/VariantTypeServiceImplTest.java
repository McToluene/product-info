package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.CreateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.varianttype.UpdateVariantTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.variantType.VariantTypeResponseDto;
import com.mctoluene.productinformationmanagement.model.VariantType;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantTypeInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.VariantTypeServiceImpl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class VariantTypeServiceImplTest {

    @Mock
    private VariantTypeInternalServiceImpl variantTypeInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private VariantTypeServiceImpl variantTypeService;

    @Test
    void createVariantType() {
        CreateVariantTypeRequestDto requestDto = CreateVariantTypeRequestDto.builder()
                .variantTypeName("Variant type")
                .description("This is Test")
                .createdBy("Ugo")
                .build();

        UUID publicId = UUID.randomUUID();
        VariantType responseDto = convertToResponseDto(publicId);
        given(variantTypeInternalService.checkIfNameExist(requestDto.getVariantTypeName().toUpperCase().trim()))
                .willReturn(false);
        given(variantTypeInternalService.saveNewVariantType(any(VariantType.class))).willReturn(responseDto);
        var createdResponse = variantTypeService.createVariantType(requestDto);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.created.successfully"));
    }

    @Test
    void updateVariantType() {
        UpdateVariantTypeRequestDto requestDto = UpdateVariantTypeRequestDto.builder()
                .variantTypeName("Variant type".trim().toUpperCase())
                .description("This is Test")
                .lastModifiedBy("Ugo")
                .build();
        UUID publicId = UUID.randomUUID();
        VariantType responseDto = convertToResponseDto();
        given(variantTypeInternalService.findVariantTypeByPublicId(publicId)).willReturn(responseDto);
        given(variantTypeInternalService.updateExistingVariantType(any(VariantType.class))).willReturn(responseDto);
        var createdResponse = variantTypeService.updateVariantType(requestDto, publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.updated.successfully"));
    }

    @Test
    void deleteVariantType() {
        VariantType responseDto = getResponseDto();
        UUID publicId = UUID.randomUUID();
        given(variantTypeInternalService.findVariantTypeByPublicId(publicId)).willReturn(responseDto);
        given(variantTypeInternalService.deleteVariantType(any(VariantType.class))).willReturn(responseDto);
        var response = variantTypeService.deleteVariantType(publicId);
        assertThat(response).isNotNull();
    }

    @Test
    void getVariantByPublicId() {
        UUID publicId = UUID.randomUUID();
        given(variantTypeInternalService.findVariantTypeByPublicId(publicId)).willReturn(getResponseDto());
        var response = variantTypeService.getVariantTypeById(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.fetched.successfully"));
    }

    @Test
    void getVariantTypesTest() {

        VariantType responseDto = getResponseDto();
        List<VariantType> variantTypes = new ArrayList<>();
        variantTypes.add(responseDto);
        var variantTypesPage = new PageImpl<>(variantTypes);
        Pageable pageable = PageRequest.of(1, 2);
        given(variantTypeInternalService.getAllVariantTypesFiltered("", pageable)).willReturn(variantTypesPage);
        var response = variantTypeService.getAllVariantTypes("", pageable.getPageNumber(), pageable.getPageSize());
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.fetched.successfully"));
    }

    private VariantType convertToResponseDto() {
        return VariantType.builder()
                .variantTypeName("Testing variant".toUpperCase().trim())
                .status(Status.ACTIVE.name())
                .build();
    }

    private VariantType getResponseDto() {
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

    private VariantType convertToResponseDto(UUID publicId) {
        VariantType variantType = VariantType.builder().build();
        variantType.setPublicId(publicId);
        variantType.setVariantTypeName("TEST");
        return variantType;
    }

    @Test
    void createVariantTypeWithWhiteSpaceName() {
        CreateVariantTypeRequestDto requestDto = CreateVariantTypeRequestDto.builder()
                .variantTypeName("  Variant type  ")
                .description("This is Test")
                .createdBy("Ugo")
                .build();

        UUID publicId = UUID.randomUUID();
        VariantType responseDto = convertToResponseDto(publicId);
        given(variantTypeInternalService.checkIfNameExist(requestDto.getVariantTypeName().toUpperCase().trim()))
                .willReturn(false);
        given(variantTypeInternalService.saveNewVariantType(any(VariantType.class))).willReturn(responseDto);
        var createdResponse = variantTypeService.createVariantType(requestDto);
        VariantTypeResponseDto variantTypeResponseDto = (VariantTypeResponseDto) createdResponse.getData();
        assertThat(variantTypeResponseDto.variantTypeName().equals(requestDto.getVariantTypeName().trim()));

    }

    @Test
    void updateVariantTypeWithWhiteSpaceName() {

        UpdateVariantTypeRequestDto requestDto = UpdateVariantTypeRequestDto.builder()
                .variantTypeName(" Variant type ")
                .description("This is Test")
                .lastModifiedBy("TEST")
                .build();
        UUID publicId = UUID.randomUUID();
        VariantType responseDto = convertToResponseDto();
        VariantType variantType = new VariantType();
        variantType.setVariantTypeName("Variant type");
        given(variantTypeInternalService.findVariantTypeByPublicId(publicId)).willReturn(responseDto);
        given(variantTypeInternalService.updateExistingVariantType(any(VariantType.class))).willReturn(variantType);
        var editResponse = variantTypeService.updateVariantType(requestDto, publicId);
        VariantTypeResponseDto variantTypeResponseDto = (VariantTypeResponseDto) editResponse.getData();
        assertThat(variantTypeResponseDto.variantTypeName().equals(requestDto.getVariantTypeName().trim()));
    }

    @Test
    void getVariantByName() {
        given(variantTypeInternalService.findVariantTypeByName(anyString()))
                .willReturn(getResponseDto());
        var createdResponse = variantTypeService.getVariantTypeByName(anyString());
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.fetched.successfully"));
    }

    @Test
    void getVariantByNameNotFound() {
        given(variantTypeInternalService.findVariantTypeByName(anyString()))
                .willReturn(null);
        var createdResponse = variantTypeService.getVariantTypeByName(anyString());
        assertThat(createdResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.not.found"));
    }

}
