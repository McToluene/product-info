package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantVersion;
import com.mctoluene.productinformationmanagement.repository.VariantVersionRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantVersionInternalServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantVersionInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private VariantVersionRepository variantVersionRepository;

    @InjectMocks
    private VariantVersionInternalServiceImpl variantVersionInternalService;

    @Test
    void saveVariantVersionToDb() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.save(any())).thenReturn(variantVersion);

        var result = variantVersionInternalService.saveVariantVersionToDb(variantVersion);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void saveVariantVersionToDbException() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantVersionInternalService.saveVariantVersionToDb(variantVersion));

    }

    @Test
    void saveVariantVersionsToDb() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.saveAll(any())).thenReturn(List.of(variantVersion));

        var result = variantVersionInternalService.saveVariantVersionsToDb(List.of(variantVersion));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void saveVariantVersionsToDbException() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantVersionInternalService.saveVariantVersionsToDb(List.of(variantVersion)));

    }

    @Test
    void getProductInUse() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findVariantVersionById(any())).thenReturn(List.of(variantVersion));

        var result = variantVersionInternalService.getProductInUse(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void findByProductVariantId() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findByProductVariantId(any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService.findByProductVariantId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void findByProductVariantIdException() {

        when(variantVersionRepository.findByProductVariantId(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantVersionInternalService.findByProductVariantId(UUID.randomUUID()));
    }

    @Test
    void findMostRecentVariantVersion() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findActiveByProductVariantId(any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService.findMostRecentVariantVersion(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void findMostRecentVariantVersionException() {

        when(variantVersionRepository.findActiveByProductVariantId(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantVersionInternalService.findMostRecentVariantVersion(UUID.randomUUID()));
    }

    @Test
    void setVersionToStatusByProductVariantId() {

        doNothing().when(variantVersionRepository).updateVersionAndStatusByProductVariantId(any(), any());

        variantVersionInternalService.setVersionToStatusByProductVariantId(UUID.randomUUID(), Status.ACTIVE.name());
    }

    @Test
    void findBySku() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findBySkuAndStatus(any(), any())).thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService.findBySku("sku");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void findBySkuException() {

        when(variantVersionRepository.findBySkuAndStatus(any(), any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantVersionInternalService.findBySku("sku"));
    }

    @Test
    void findAllBySkuIn() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findAllBySku(any())).thenReturn(List.of(variantVersion));

        var result = variantVersionInternalService.findAllBySkuIn(List.of("sku1", "sku2"));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));

    }

    @Test
    void searchVariantVersionByProductVariantsIn() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.searchVariantVersionByProductVariantsIn(any(), any()))
                .thenReturn(List.of(variantVersion));

        var result = variantVersionInternalService
                .searchVariantVersionByProductVariantsIn("search", List.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void findByVariantTypeId() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findByVariantTypeIdAndStatus(any(), any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService.findByVariantTypeId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(variantVersion));
    }

    @Test
    void findByProductId() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findByProductIdAndStatus(any(), any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService.findByProductId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(variantVersion));
    }

    @Test
    void searchVariantBySkuListAndProductName() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        Pageable request = PageRequest.of(1, 10);
        Page<VariantVersion> variantVersionPage = new PageImpl<>(List.of(variantVersion), request, 1);
        when(variantVersionRepository.findBySkuListAndProductName(any(), any(), any()))
                .thenReturn(variantVersionPage);

        var result = variantVersionInternalService
                .searchVariantBySkuListAndProductName(List.of("sku"), "search", request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersionPage);
    }

    @Test
    void findVariantsByProductCategoryId() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        Pageable request = PageRequest.of(1, 10);
        Page<VariantVersion> variantVersionPage = new PageImpl<>(List.of(variantVersion), request, 1);
        when(variantVersionRepository.findByCategoryId(any(), any()))
                .thenReturn(variantVersionPage);

        var result = variantVersionInternalService
                .findVariantsByProductCategoryId(UUID.randomUUID(), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersionPage);
    }

    @Test
    void findByProductVariantIdAndStatus() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findByProductVariantIdAndStatus(any(), any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService
                .findByProductVariantIdAndStatus(UUID.randomUUID(), Status.ACTIVE.name());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void findByProductVariantIdAndStatusException() {

        when(variantVersionRepository.findByProductVariantIdAndStatus(any(), any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantVersionInternalService
                .findByProductVariantIdAndStatus(UUID.randomUUID(), Status.ACTIVE.name()));
    }

    @Test
    void findVariantBySkuList() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findBySkuList(any())).thenReturn(List.of(variantVersion));

        var result = variantVersionInternalService
                .findVariantBySkuList(List.of("sku"));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void findVariantBySku() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        when(variantVersionRepository.findBySku(any())).thenReturn(Optional.of(variantVersion));

        var result = variantVersionInternalService
                .findVariantBySku("sku");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersion);
    }

    @Test
    void findVariantBySkuException() {

        when(variantVersionRepository.findBySku(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantVersionInternalService.findVariantBySku("sku"));
    }
}
