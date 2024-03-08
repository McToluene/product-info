package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.model.FailedProducts;
import com.mctoluene.productinformationmanagement.repository.FailedProductsRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.FailedProductsInternalServiceImpl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class FailedProductsInternalServiceImplTest {

    @Mock
    private FailedProductsRepository failedProductsRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private FailedProductsInternalServiceImpl failedProductsInternalService;

    @Test
    void searchFailedProductsTest() {
        FailedProducts failedProducts = buildFailedProducts();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<FailedProducts> failedProductsPage = new PageImpl<>(List.of(failedProducts), request, 1);
        when(failedProductsRepository.searchFailedProducts(any(), any(), any(), any())).thenReturn(failedProductsPage);

        var result = failedProductsInternalService.searchFailedProducts("",
                LocalDateTime.parse("2015-08-04T00:00:00"), LocalDateTime.parse("2024-08-04T23:59:59.999999999"),
                request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(failedProducts));
    }

    @Test
    void saveAllFailedProductsTest() {
        FailedProducts failedProducts = buildFailedProducts();
        when(failedProductsRepository.saveAll(any())).thenReturn(List.of(failedProducts));

        var result = failedProductsInternalService.saveAllFailedProducts(List.of(failedProducts));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(failedProducts));

    }

    @Test
    void saveAllFailedProductsModelNotFoundTest() {
        FailedProducts failedProducts = buildFailedProducts();
        when(failedProductsRepository.saveAll(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> failedProductsInternalService.saveAllFailedProducts(List.of(failedProducts)));

    }

    private FailedProducts buildFailedProducts() {
        FailedProducts failedProducts = FailedProducts.builder()
                .productName("product")
                .brandName("brand")
                .manufacturerName("manufacturer")
                .productCategoryName("productCategoryName")
                .measurementUnit("measurementUnit")
                .productListing("productListing")
                .defaultImageUrl("url")
                .productDescription("desc")
                .productHighlights("ph")
                .warrantyDuration("duration")
                .warrantyCover("warrantyCover")
                .warrantyType("warrantyType")
                .warrantyAddress("warrantyAddress")
                .productCountry("country")
                .status(Status.ACTIVE.name())
                .productDetails("prd")
                .build();

        failedProducts.setId(UUID.randomUUID());
        failedProducts.setPublicId(UUID.randomUUID());
        failedProducts.setVersion(BigInteger.ZERO);
        failedProducts.setCreatedBy("test");
        failedProducts.setCreatedDate(LocalDateTime.now());
        failedProducts.setLastModifiedBy("test");
        failedProducts.setLastModifiedDate(LocalDateTime.now());

        return failedProducts;
    }
}
