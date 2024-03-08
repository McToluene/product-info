package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mctoluene.productinformationmanagement.model.FailedProducts;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FailedProductsRepository extends JpaRepository<FailedProducts, UUID> {

    @Query(value = """
                select * from failed_products where (product_name like :searchParam or product_description  like :searchParam)
                and (created_date between :from and :to)
            """, nativeQuery = true)
    Page<FailedProducts> searchFailedProducts(String searchParam, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

}
