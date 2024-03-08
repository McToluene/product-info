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
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.CreateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.manufacturer.UpdateManufacturerRequestDto;
import com.mctoluene.productinformationmanagement.domain.response.manufacturer.ManufacturerResponseDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ManufacturerInternalServiceImpl;
import com.mctoluene.productinformationmanagement.service.impl.ManufacturerServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerInternalServiceImpl manufacturerInternalService;

    @Mock
    private ProductInternalService productInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private ManufacturerServiceImpl manufacturerService;

    @Test
    void createManufacturer() {
        CreateManufacturerRequestDto requestDto = CreateManufacturerRequestDto.builder()
                .manufacturerName("mctoluene")
                .description("We are service providers")
                .createdBy("Pranali")
                .build();

        Manufacturer responseDto = convertToResponseDto();
        when(manufacturerInternalService.saveNewManufacturer(any(Manufacturer.class))).thenReturn(responseDto);
        var createdResponse = manufacturerService.createManufacturer(requestDto);
        createdResponse.getMessage();
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("manufacturer.created.successfully"));
    }

    private Manufacturer convertToResponseDto() {
        return Manufacturer.builder()
                .status(Status.ACTIVE)
                .manufacturerName("mctoluene")
                .build();
    }

    @Test
    void getManufacturer() {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("Nykaa")
                .description("We serve beauty products")
                .status(Status.ACTIVE)
                .build();

        UUID publicId = UUID.randomUUID();
        given(manufacturerInternalService.findByPublicId(publicId)).willReturn(Optional.of(manufacturer));
        var createdResponse = manufacturerService.getManufacturer(publicId);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("brand.fetched.successfully"));
    }

    @Test
    void getAllManufacturers() {
        int page = 0;
        int size = 5;
        int count = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        given(manufacturerInternalService.findAllBy(any(Pageable.class)))
                .willReturn((PageImpl<Manufacturer>) getManufacturerResponse(pageRequest, count));
        var createdResponse = manufacturerService.getAllManufacturers(page, size);
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("list.of.manufacturers.retrieved.successfully"));
    }

    private Page<Manufacturer> getManufacturerResponse(PageRequest pageRequest, int count) {
        List<Manufacturer> manufacturerResponse = new ArrayList<>();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), manufacturerResponse.size());
        Page<Manufacturer> page = new PageImpl<>(manufacturerResponse.subList(start, end), pageRequest,
                manufacturerResponse.size());
        for (int i = 0; i <= count; i++) {

            manufacturerResponse.add(Manufacturer.builder()
                    .manufacturerName("Manufacturer-" + i)
                    .status(Status.ACTIVE)
                    .description("We have all fashion products")
                    .build());
        }
        return page;
    }

    @Test
    void editManufacturer() {
        UpdateManufacturerRequestDto requestDto = UpdateManufacturerRequestDto.builder()
                .manufacturerName("test")
                .description("WE serve fashion products")
                .lastModifiedBy("Khushboo")
                .build();

        UUID publicId = UUID.randomUUID();
        Manufacturer responseDto = convertToResponseDto();
        given(manufacturerInternalService.findByPublicIdAndManufacturer(publicId, requestDto)).willReturn(responseDto);
        given(manufacturerInternalService.updateManufacturer(any(Manufacturer.class))).willReturn(responseDto);
        var editResponse = manufacturerService.updateManufacturer(publicId, requestDto);
        assertThat(editResponse).isNotNull();
    }

    @Test
    void testUpdateManufacturer_NullName() {
        UUID publicId = UUID.randomUUID();
        UpdateManufacturerRequestDto requestDto = UpdateManufacturerRequestDto.builder()
                .manufacturerName(null)
                .description("Test null")
                .lastModifiedBy("a@a.com")
                .build();

        when(messageSourceService.getMessageByKey("manufacturer.not.null.or.empty"))
                .thenReturn("manufacturer name cannot be null or empty");

        Exception exception = assertThrows(ValidatorException.class, () -> {
            manufacturerService.updateManufacturer(publicId, requestDto);
        });

        String expectedMessage = "manufacturer name cannot be null or empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateManufacturer_EmptyName() {
        UUID publicId = UUID.randomUUID();
        UpdateManufacturerRequestDto requestDto = UpdateManufacturerRequestDto.builder()
                .manufacturerName("")
                .description("Test null")
                .lastModifiedBy("a@a.com")
                .build();

        when(messageSourceService.getMessageByKey("manufacturer.not.null.or.empty"))
                .thenReturn("manufacturer name cannot be null or empty");

        Exception exception = assertThrows(ValidatorException.class, () -> {
            manufacturerService.updateManufacturer(publicId, requestDto);
        });

        String expectedMessage = "manufacturer name cannot be null or empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deleteManufacturer() {
        UUID publicId = UUID.randomUUID();
        Manufacturer responseDto = convertToResponseDto();
        given(manufacturerInternalService.findByPublicId(publicId)).willReturn(Optional.of(responseDto));
        given(productInternalService.checkIfBrandIsInUse(publicId)).willReturn(true);
        given(manufacturerInternalService.deleteManufacturer(any(Manufacturer.class))).willReturn(responseDto);
        var deleteResponse = manufacturerService.deleteManufacturer(publicId);
        assertThat(deleteResponse).isNotNull();
    }

    @Test
    void deleteManufacturerValidatorExceptionTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));
        when(productInternalService.checkIfManufacturerIsInUse(any()))
                .thenReturn(true);

        assertThrows(ValidatorException.class, () -> manufacturerService.deleteManufacturer(publicId));
    }

    @Test
    void deleteManufacturerModelNotFoundExceptionTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.DELETED)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));
        when(productInternalService.checkIfManufacturerIsInUse(any()))
                .thenReturn(false);

        assertThrows(ModelNotFoundException.class, () -> manufacturerService.deleteManufacturer(publicId));
    }

    @Test
    void enableManufacturerStatusTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.INACTIVE)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var response = manufacturerService.enableManufacturerStatus(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("manufacturer.status.enabled.successfully"));
    }

    @Test
    void enableManufacturerStatusAlreadyEnabledTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var response = manufacturerService.enableManufacturerStatus(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("manufacturer.status.already.enabled"));
    }

    @Test
    void disableManufacturerStatusTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.ACTIVE)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var response = manufacturerService.disableManufacturerStatus(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("manufacturer.status.disabled.successfully"));
    }

    @Test
    void disableManufacturerStatusAlreadyDisabledTest() {
        UUID publicId = UUID.randomUUID();
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .status(Status.INACTIVE)
                .build();
        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(publicId);

        when(manufacturerInternalService.findByPublicId(any())).thenReturn(Optional.of(manufacturer));

        var response = manufacturerService.disableManufacturerStatus(publicId);
        assertThat(response).isNotNull();
        assertThat(response.getMessage())
                .isEqualTo(messageSourceService.getMessageByKey("manufacturer.status.already.disabled"));
    }

    @Test
    void createManufacturerWithWhiteSpaceName() {

        CreateManufacturerRequestDto requestDto = CreateManufacturerRequestDto.builder()
                .manufacturerName("  mctoluene  ")
                .description("We are service providers")
                .createdBy("TEST")
                .build();

        Manufacturer responseDto = convertToResponseDto();
        when(manufacturerInternalService.saveNewManufacturer(any(Manufacturer.class))).thenReturn(responseDto);
        var createdResponse = manufacturerService.createManufacturer(requestDto);
        ManufacturerResponseDto manufacturerResponseDto = (ManufacturerResponseDto) createdResponse.getData();
        assertThat(manufacturerResponseDto.manufacturerName().equals(requestDto.getManufacturerName().trim()));

    }

    @Test
    void editManufacturerWithWhiteSpaceName() {

        UpdateManufacturerRequestDto requestDto = UpdateManufacturerRequestDto.builder()
                .manufacturerName("  Nykaa  ")
                .description("WE serve fashion products")
                .lastModifiedBy("TEST")
                .build();

        UUID publicId = UUID.randomUUID();
        Manufacturer responseDto = convertToResponseDto();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setPublicId(publicId);
        manufacturer.setManufacturerName("Nykaa");
        manufacturer.setStatus(Status.ACTIVE);
        given(manufacturerInternalService.findByPublicIdAndManufacturer(publicId, requestDto)).willReturn(responseDto);
        given(manufacturerInternalService.updateManufacturer(any(Manufacturer.class))).willReturn(manufacturer);
        var editResponse = manufacturerService.updateManufacturer(publicId, requestDto);
        ManufacturerResponseDto manufacturerResponseDto = (ManufacturerResponseDto) editResponse.getData();
        assertThat(manufacturerResponseDto.manufacturerName().equals(requestDto.getManufacturerName().trim()));

    }
}
