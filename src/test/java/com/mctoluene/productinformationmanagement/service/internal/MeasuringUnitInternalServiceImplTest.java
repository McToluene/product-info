package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.request.measuringunit.UpdateMeasuringUnitRequestDto;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.MeasuringUnit;
import com.mctoluene.productinformationmanagement.repository.MeasuringUnitRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.MeasuringUnitInternalServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
public class MeasuringUnitInternalServiceImplTest {

    AutoCloseable closeable;

    MeasuringUnit measuringUnit;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private MeasuringUnitRepository measuringUnitRepository;

    @InjectMocks
    private MeasuringUnitInternalServiceImpl measuringUnitInternalService;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        measuringUnit = EntityHelpers.buildMeasuringUnit();
        measuringUnitInternalService = new MeasuringUnitInternalServiceImpl(messageSourceService,
                measuringUnitRepository);
    }

    @Test
    void saveNewMeasuringUnit() {

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.save(any())).thenReturn(measuringUnit);

        var result = measuringUnitInternalService.saveNewMeasuringUnit(measuringUnit);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnit);
    }

    @Test
    void saveNewMeasuringUnitException() {

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(measuringUnit));
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.save(any())).thenReturn(measuringUnit);

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.saveNewMeasuringUnit(measuringUnit));
    }

    @Test
    void saveNewMeasuringUnitAbbreviationException() {

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.of(measuringUnit));
        when(measuringUnitRepository.save(any())).thenReturn(measuringUnit);

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.saveNewMeasuringUnit(measuringUnit));
    }

    @Test
    void saveNewMeasuringUnitToDbTest() {

        when(measuringUnitRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.saveNewMeasuringUnitToDb(measuringUnit));
    }

    @Test
    void findByPublicIdTest() {
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.of(measuringUnit));

        var result = measuringUnitInternalService.findByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnit);
    }

    @Test
    void findByPublicIdExceptionTest() {
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> measuringUnitInternalService.findByPublicId(UUID.randomUUID()));
    }

    @Test
    void findAllBy() {
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<MeasuringUnit> measuringUnits = new PageImpl<>(List.of(measuringUnit), request, 1);

        when(measuringUnitRepository.findAll(request)).thenReturn(measuringUnits);

        var result = measuringUnitInternalService.findAllBy(request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnits);
    }

    @Test
    void updateMeasuringUnit() {
        when(measuringUnitRepository.save(any())).thenReturn(measuringUnit);

        var result = measuringUnitInternalService.updateMeasuringUnit(measuringUnit);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnit);
    }

    @Test
    void updateMeasuringUnitException() {
        when(measuringUnitRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.updateMeasuringUnit(measuringUnit));
    }

    @Test
    void deleteMeasuringUnit() {
        when(measuringUnitRepository.save(any())).thenReturn(measuringUnit);

        var result = measuringUnitInternalService.deleteMeasuringUnit(measuringUnit);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnit);
    }

    @Test
    void findByPublicIdAndMeasuringUnitName() {
        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("test")
                .description("test")
                .abbreviation("test")
                .build();

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.of(measuringUnit));

        var result = measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(UUID.randomUUID(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(measuringUnit);
    }

    @Test
    void findByPublicIdAndMeasuringUnitNameDuplicateNameTest() {
        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("test")
                .description("test")
                .abbreviation("test")
                .build();

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(measuringUnit));
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.of(measuringUnit));

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(UUID.randomUUID(), requestDto));
    }

    @Test
    void findByPublicIdAndMeasuringUnitNameDuplicateAbbreviationTest() {
        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("test")
                .description("test")
                .abbreviation("test")
                .build();

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.of(measuringUnit));
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.of(measuringUnit));

        Assertions.assertThrows(ValidatorException.class,
                () -> measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(UUID.randomUUID(), requestDto));
    }

    @Test
    void findByPublicIdAndMeasuringUnitNameNotFoundTest() {
        UpdateMeasuringUnitRequestDto requestDto = UpdateMeasuringUnitRequestDto.builder()
                .name("test")
                .description("test")
                .abbreviation("test")
                .build();

        when(measuringUnitRepository.findByNameIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByAbbreviationIgnoreCase(any())).thenReturn(Optional.empty());
        when(measuringUnitRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> measuringUnitInternalService.findByPublicIdAndMeasuringUnitName(UUID.randomUUID(), requestDto));
    }
}
