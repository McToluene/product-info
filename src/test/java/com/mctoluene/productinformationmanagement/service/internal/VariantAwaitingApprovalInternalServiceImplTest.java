package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.repository.VariantAwaitingApprovalRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantAwaitingApprovalInternalServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantAwaitingApprovalInternalServiceImplTest {

    @Mock
    private VariantAwaitingApprovalRepository variantAwaitingApprovalRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private VariantAwaitingApprovalInternalServiceImpl variantAwaitingApprovalInternalService;

    @Test
    void saveVariantAwaitingApprovalToDbTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.saveAll(anyList())).thenReturn(List.of(variantAwaitingApproval));

        var result = variantAwaitingApprovalInternalService
                .saveVariantAwaitingApprovalToDb(List.of(variantAwaitingApproval));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantAwaitingApproval));
    }

    @Test
    void saveVariantAwaitingApprovalToDbExceptionTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.saveAll(anyList())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class, () -> variantAwaitingApprovalInternalService
                .saveVariantAwaitingApprovalToDb(List.of(variantAwaitingApproval)));

    }

    @Test
    void findByPublicIdTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.findByPublicId(any())).thenReturn(Optional.of(variantAwaitingApproval));

        var result = variantAwaitingApprovalInternalService.findByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantAwaitingApproval);
    }

    @Test
    void findByPublicIdModelNotFoundTest() {

        when(variantAwaitingApprovalRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> variantAwaitingApprovalInternalService.findByPublicId(UUID.randomUUID()));

    }

    @Test
    void saveVariantAwaitApprovalToDbTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.save(any())).thenReturn(variantAwaitingApproval);

        var result = variantAwaitingApprovalInternalService.saveVariantAwaitApprovalToDb(variantAwaitingApproval);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantAwaitingApproval);
    }

    @Test
    void findByVariantAwaitingApprovalByPublicIdTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.findByVariantAwatingApprovalPublicId(any()))
                .thenReturn(Optional.of(variantAwaitingApproval));

        var result = variantAwaitingApprovalInternalService.findByVariantAwaitingApprovalByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantAwaitingApproval);
    }

    @Test
    void findByVariantAwaitingApprovalByPublicIdExceptionTest() {

        when(variantAwaitingApprovalRepository.findByVariantAwatingApprovalPublicId(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> variantAwaitingApprovalInternalService
                .findByVariantAwaitingApprovalByPublicId(UUID.randomUUID()));

    }

    @Test
    void searchVariantsAwaitingApprovalTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantAwaitingApproval> variantAwaitingApprovals = new PageImpl<>(List.of(variantAwaitingApproval),
                request, 1);

        when(variantAwaitingApprovalRepository.searchVariantAwaitingApproval(any(), any(), any(), any(), anyString(),
                anyString(), any()))
                .thenReturn(variantAwaitingApprovals);

        var result = variantAwaitingApprovalInternalService
                .searchVariantsAwaitingApproval("search", UUID.randomUUID(), LocalDateTime.parse("2015-08-04T00:00:00"),
                        LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.APPROVED.name(),
                        Status.ACTIVE.name(), request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(variantAwaitingApproval));
    }

    @Test
    void getVariantsWithMissingImages() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantAwaitingApproval> variantAwaitingApprovals = new PageImpl<>(List.of(variantAwaitingApproval),
                request, 1);

        when(variantAwaitingApprovalRepository.findVariantsWithMissingImages(any(), any(), any(), any()))
                .thenReturn(variantAwaitingApprovals);

        var result = variantAwaitingApprovalInternalService
                .getVariantsWithMissingImages("search", LocalDateTime.parse("2015-08-04T00:00:00"),
                        LocalDateTime.parse("2023-08-04T23:59:59.999999999"), request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(variantAwaitingApproval));
    }

    @Test
    void findVariantBySkuTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.findVariantAwaitingApprovalBySku(any()))
                .thenReturn(Optional.of(variantAwaitingApproval));

        var result = variantAwaitingApprovalInternalService.findVariantBySku("sku");

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(variantAwaitingApproval);
    }

    @Test
    void setApprovalStatusByProductVariantIdTest() {
        doNothing().when(variantAwaitingApprovalRepository).updateApprovalStatusByProductVariantId(any(), any());

        variantAwaitingApprovalInternalService.setApprovalStatusByProductVariantId(UUID.randomUUID(), "test");
    }

    @Test
    void searchVariantsAwaitingApproval() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantAwaitingApproval> variantAwaitingApprovals = new PageImpl<>(List.of(variantAwaitingApproval),
                request, 1);

        when(variantAwaitingApprovalRepository.searchVariantAwaitingApproval(any(), any(), any(), any(), any(),
                anyList(),
                any()))
                .thenReturn(variantAwaitingApprovals);

        var result = variantAwaitingApprovalInternalService
                .searchVariantsAwaitingApproval("search", UUID.randomUUID(), LocalDateTime.parse("2015-08-04T00:00:00"),
                        LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.APPROVED.name(),
                        List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantAwaitingApprovals);

    }

    @Test
    void getRejectedVariantsAwaitingApprovalTest() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantAwaitingApproval> variantAwaitingApprovals = new PageImpl<>(List.of(variantAwaitingApproval),
                request, 1);

        when(variantAwaitingApprovalRepository.getRejectedVariantsAwaitingApproval(any(), any(), any(), any(), any()))
                .thenReturn(variantAwaitingApprovals);

        var result = variantAwaitingApprovalInternalService
                .getRejectedVariantsAwaitingApproval("search", UUID.randomUUID(),
                        LocalDateTime.parse("2015-08-04T00:00:00"),
                        LocalDateTime.parse("2023-08-04T23:59:59.999999999"), request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(variantAwaitingApproval));

    }

    @Test
    void findBySkuAndApprovalStatus() {
        VariantAwaitingApproval variantAwaitingApproval = EntityHelpers.buildVariantAwaitingApproval();
        when(variantAwaitingApprovalRepository.findBySkuAndApprovalStatus(any(), any()))
                .thenReturn(Optional.of(variantAwaitingApproval));
        var result = variantAwaitingApprovalInternalService.findBySkuAndApprovalStatus("sku",
                ApprovalStatus.REJECTED.name());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantAwaitingApproval);
    }

    @Test
    void findBySkuAndApprovalStatusNullTest() {

        when(variantAwaitingApprovalRepository.findBySkuAndApprovalStatus(any(), any()))
                .thenReturn(Optional.empty());
        var result = variantAwaitingApprovalInternalService.findBySkuAndApprovalStatus("sku",
                ApprovalStatus.REJECTED.name());

        assertThat(result).isNull();
    }
}
