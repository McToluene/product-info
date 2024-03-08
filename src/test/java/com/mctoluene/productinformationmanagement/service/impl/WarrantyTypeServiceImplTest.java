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
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.CreateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.warrantytype.UpdateWarrantyTypeRequestDto;
import com.mctoluene.productinformationmanagement.model.WarrantyType;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.WarrantyTypeServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.WarrantyTypeInternalService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class WarrantyTypeServiceImplTest {

    @Mock
    private WarrantyTypeInternalService warrantyTypeInternalService;
    @Mock
    private MessageSourceService messageSourceService;
    @InjectMocks
    private WarrantyTypeServiceImpl warrantyTypeServiceImpl;

    @Test
    void createWarrantyType() {
        CreateWarrantyTypeRequestDto requestDto = CreateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("Warranty type")
                .description("Warranty type description")
                .createdBy("System")
                .build();

        WarrantyType responseDto = convertToResponseDto();
        given(warrantyTypeInternalService.saveWarrantyTypeToDb(any(WarrantyType.class))).willReturn(responseDto);
        var createdResponse = warrantyTypeServiceImpl.createWarrantyType(requestDto);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("warranty.type.created.successfully"));
    }

    @Test
    void getWarrantyTypeById() {
        UUID publicId = UUID.randomUUID();

        when(warrantyTypeInternalService.findWarrantyTypeByPublicId(publicId)).thenReturn(getResponseDto());
        var response = warrantyTypeServiceImpl.getWarrantyTypeById(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("warranty.type.fetched.successfully"));
    }

    private WarrantyType convertToResponseDto() {
        return WarrantyType.builder()
                .warrantyTypeName("Warranty type")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void deleteWarrantyType() {
        UUID publicId = UUID.randomUUID();
        WarrantyType responseDto = convertToResponseDto();
        given(warrantyTypeInternalService.findByPublicId(publicId)).willReturn(responseDto);
        given(warrantyTypeInternalService.deleteWarrantyType(any(WarrantyType.class))).willReturn(responseDto);
        var deleteResponse = warrantyTypeServiceImpl.deleteWarrantyType(publicId);
        assertThat(deleteResponse).isNotNull();
    }

    private WarrantyType getResponseDto() {

        WarrantyType warrantyType = new WarrantyType();

        warrantyType.setDescription("new description");
        warrantyType.setId(UUID.randomUUID());
        warrantyType.setPublicId(UUID.randomUUID());
        warrantyType.setWarrantyTypeName("new warranty");
        warrantyType.setStatus(Status.ACTIVE);
        warrantyType.setCreatedDate(LocalDateTime.now());
        warrantyType.setLastModifiedBy("now");
        warrantyType.setCreatedBy("kunal");
        warrantyType.setVersion(BigInteger.ZERO);
        warrantyType.setLastModifiedBy("kunal");

        return warrantyType;
    }

    @Test
    void editUpdateWarrantyType() {
        UpdateWarrantyTypeRequestDto requestDto = UpdateWarrantyTypeRequestDto.builder()
                .warrantyTypeName("Nykaa")
                .description("WE serve fashion products")
                .lastModifiedBy("Khushboo")
                .build();

        UUID publicId = UUID.randomUUID();
        WarrantyType responseDto = convertToResponseDto();
        given(warrantyTypeInternalService.findByPublicIdAndWarrantyType(publicId, requestDto)).willReturn(responseDto);
        given(warrantyTypeInternalService.updateWarrantyType(any(WarrantyType.class))).willReturn(responseDto);
        var editResponse = warrantyTypeServiceImpl.updateWarrantyType(publicId, requestDto);
        assertThat(editResponse).isNotNull();
    }

    @Test
    void gelAllWarrantyTypes() {

        List<WarrantyType> warrantyTypes = List.of(getResponseDto());
        PageImpl<WarrantyType> warrantyTypePage = new PageImpl<>(warrantyTypes);
        Pageable pageable = PageRequest.of(1, 2);

        given(warrantyTypeInternalService.getAllWarrantyType(any(Pageable.class))).willReturn(warrantyTypePage);

        var response = warrantyTypeServiceImpl.getAllWarrantyTypes(pageable.getPageNumber(), pageable.getPageSize());

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("variant.type.fetched.successfully"));

    }
}