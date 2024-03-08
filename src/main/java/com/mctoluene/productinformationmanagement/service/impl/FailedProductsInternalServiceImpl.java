package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.model.FailedProducts;
import com.mctoluene.productinformationmanagement.repository.FailedProductsRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.FailedProductsInternalService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailedProductsInternalServiceImpl implements FailedProductsInternalService {

    private final FailedProductsRepository failedProductsRepository;

    private final MessageSourceService messageSourceService;

    @Override
    public Page<FailedProducts> searchFailedProducts(String searchParam, LocalDateTime from, LocalDateTime to,
            Pageable pageable) {
        return failedProductsRepository.searchFailedProducts("%" + searchParam + "%", from, to, pageable);
    }

    @Override
    public List<FailedProducts> saveAllFailedProducts(List<FailedProducts> failedProducts) {
        try {
            return failedProductsRepository.saveAll(failedProducts);
        } catch (Exception e) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("failed.products.not.found"));
        }
    }

}
