package com.mctoluene.productinformationmanagement.service;

import com.mctoluene.productinformationmanagement.model.FailedProducts;
import com.mctoluene.productinformationmanagement.service.internal.FailedProductsInternalService;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailedProductService {

    private final FailedProductsInternalService failedProductsInternalService;

    public AppResponse getFailedProducts(String searchParam, LocalDate from, LocalDate to, Integer page, Integer size) {
        if (from == null || from.toString().isBlank())
            from = LocalDate.now().minusYears(50);
        if (to == null || to.toString().isBlank())
            to = LocalDate.now();

        page = page > 0 ? page : 1;
        size = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<FailedProducts> failedProductsPage = failedProductsInternalService.searchFailedProducts(searchParam,
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX), pageable);
        return new AppResponse(HttpStatus.OK.value(), "failed products retrieved",
                "failed products retrieved", failedProductsPage, null);
    }

}
