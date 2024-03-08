package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.repository.VariantAwaitingApprovalRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantAwaitingApprovalServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class VariantAwaitingApprovalServiceTest {

    @Mock
    private VariantAwaitingApprovalRepository variantAwaitingApprovalRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private VariantAwaitingApprovalServiceImpl variantAwaitingApprovalService;

    @Test
    void saveVariantAwaitingApprovalToDbTest() {
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus("ACTIVE")
                .build();

        when(variantAwaitingApprovalRepository.save(variantAwaitingApproval))
                .thenReturn(variantAwaitingApproval);

        var response = variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(variantAwaitingApproval);

        assertThat(response).isNotNull();
        assertThat(response.getApprovalStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void saveVariantAwaitingApprovalToDbExceptionTest() {
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus("ACTIVE")
                .build();
        when(variantAwaitingApprovalRepository.save(variantAwaitingApproval))
                .thenThrow(new DataIntegrityViolationException("test"));

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(variantAwaitingApproval));
    }

    @Test
    void saveVariantAwaitingApprovalWithWhiteSpace() {
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .approvalStatus("ACTIVE")
                .variantName("  TEST  ")
                .build();

        VariantAwaitingApproval savedVariant = VariantAwaitingApproval.builder()
                .approvalStatus("ACTIVE")
                .variantName("TEST")
                .build();

        when(variantAwaitingApprovalRepository.save(variantAwaitingApproval))
                .thenReturn(savedVariant);

        var response = variantAwaitingApprovalService.saveVariantAwaitingApprovalToDb(variantAwaitingApproval);
        assertThat(response.getVariantName().equals(variantAwaitingApproval.getVariantName().trim()));
    }
}
