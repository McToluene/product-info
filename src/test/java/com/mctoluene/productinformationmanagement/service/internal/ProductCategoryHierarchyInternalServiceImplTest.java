package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;
import com.mctoluene.productinformationmanagement.repository.ProductCategoryHierarchyRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryHierarchyInternalServiceImpl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductCategoryHierarchyInternalServiceImplTest {

    @Mock
    private ProductCategoryHierarchyRepository productCategoryHierarchyRepository;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private ProductCategoryHierarchyInternalServiceImpl productCategoryHierarchyInternalService;

    @Test
    void createNewProductCategoryHierarchy() {
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();

        when(productCategoryHierarchyRepository.save(any())).thenReturn(productCategoryHierarchy);

        var result = productCategoryHierarchyInternalService
                .createNewProductCategoryHierarchy(productCategoryHierarchy);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategoryHierarchy);
    }

    @Test
    void createNewProductCategoryHierarchyUnProcessableEntity() {
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();

        when(productCategoryHierarchyRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class, () -> productCategoryHierarchyInternalService
                .createNewProductCategoryHierarchy(productCategoryHierarchy));
    }

    @Test
    void findByCategoryPublicIdAndParentCategoryPublicIdTest() {
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();
        when(productCategoryHierarchyRepository.findByProductCategoryPublicIdAndProductCategoryParentPublicId(any(),
                any()))
                .thenReturn(Optional.of(productCategoryHierarchy));

        var result = productCategoryHierarchyInternalService
                .findByCategoryPublicIdAndParentCategoryPublicId(UUID.randomUUID(), UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(productCategoryHierarchy);
    }

    @Test
    void getAllProductCategoryTest() {
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();
        when(productCategoryHierarchyRepository.findAll()).thenReturn(List.of(productCategoryHierarchy));

        var result = productCategoryHierarchyInternalService.getAllProductCategory();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productCategoryHierarchy));
    }

    private ProductCategoryHierarchy buildProductCategoryHierarchy() {
        ProductCategoryHierarchy productCategoryHierarchy = ProductCategoryHierarchy.builder()
                .productCategoryPublicId(UUID.randomUUID())
                .productCategoryParentPublicId(UUID.randomUUID())
                .build();

        productCategoryHierarchy.setId(UUID.randomUUID());
        productCategoryHierarchy.setPublicId(UUID.randomUUID());
        productCategoryHierarchy.setVersion(BigInteger.ZERO);
        productCategoryHierarchy.setCreatedBy("test");
        productCategoryHierarchy.setCreatedDate(LocalDateTime.now());
        productCategoryHierarchy.setLastModifiedBy("test");
        productCategoryHierarchy.setLastModifiedDate(LocalDateTime.now());

        return productCategoryHierarchy;
    }

}
