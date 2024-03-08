package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.VariantAwaitingApproval;
import com.mctoluene.productinformationmanagement.repository.VariantAwaitingApprovalRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.VariantAwaitingApprovalService;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantAwaitingApprovalServiceImpl implements VariantAwaitingApprovalService {
    private final MessageSourceService messageSourceService;
    private final VariantAwaitingApprovalRepository variantAwaitingApprovalRepository;

    @Override
    public VariantAwaitingApproval saveVariantAwaitingApprovalToDb(VariantAwaitingApproval variantAwaitingApproval) {
        try {
            return variantAwaitingApprovalRepository.save(variantAwaitingApproval);
        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("an.error.occurred.creating.variant"));
        }
    }
}
