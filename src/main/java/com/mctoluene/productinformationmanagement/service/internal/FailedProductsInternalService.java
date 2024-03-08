package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.model.FailedProducts;

import java.time.LocalDateTime;
import java.util.List;

public interface FailedProductsInternalService {

    Page<FailedProducts> searchFailedProducts(String searchParam, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

    List<FailedProducts> saveAllFailedProducts(List<FailedProducts> failedProducts);
}
