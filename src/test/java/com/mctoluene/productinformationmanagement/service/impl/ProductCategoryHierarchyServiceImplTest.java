package com.mctoluene.productinformationmanagement.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductCategoryHierarchy;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryHierarchyServiceImpl;
import com.mctoluene.productinformationmanagement.service.internal.ProductCategoryHierarchyInternalService;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductCategoryHierarchyServiceImplTest {

    @Mock
    private ProductCategoryHierarchyInternalService productCategoryHierarchyInternalService;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private ProductCategoryHierarchyServiceImpl productCategoryHierarchyService;

    @Test
    void createProductCategoryHierarchyWithParentCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();
        when(productCategoryHierarchyInternalService.findByCategoryPublicIdAndParentCategoryPublicId(any(), any()))
                .thenReturn(Optional.empty());
        when(productCategoryHierarchyInternalService.createNewProductCategoryHierarchy(productCategoryHierarchy))
                .thenReturn(productCategoryHierarchy);

        productCategoryHierarchyService.createProductCategoryHierarchyWithParentCategory(productCategory,
                UUID.randomUUID());
    }

    @Test
    void createProductCategoryHierarchyWithParentCategoryExceptionTest() {
        ProductCategory productCategory = buildProductCategory();
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();
        when(productCategoryHierarchyInternalService.findByCategoryPublicIdAndParentCategoryPublicId(any(), any()))
                .thenReturn(Optional.of(productCategoryHierarchy));
        when(productCategoryHierarchyInternalService.createNewProductCategoryHierarchy(productCategoryHierarchy))
                .thenReturn(productCategoryHierarchy);
        Assertions.assertThrows(UnProcessableEntityException.class, () -> productCategoryHierarchyService
                .createProductCategoryHierarchyWithParentCategory(productCategory, UUID.randomUUID()));
    }

    @Test
    void createProductParentCategoryHierarchyWithoutParentCategoryTest() {
        ProductCategoryHierarchy productCategoryHierarchy = buildProductCategoryHierarchy();
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryHierarchyInternalService.createNewProductCategoryHierarchy(productCategoryHierarchy))
                .thenReturn(productCategoryHierarchy);

        productCategoryHierarchyService.createProductParentCategoryHierarchyWithoutParentCategory(productCategory);
    }

    private ProductCategory buildProductCategory() {
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .status(Status.ACTIVE)
                .depth(10)
                .imageUrl("url")
                .description("desc")
                .build();

        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(UUID.randomUUID());
        productCategory.setVersion(BigInteger.ZERO);
        productCategory.setCreatedBy("test");
        productCategory.setCreatedDate(LocalDateTime.now());
        productCategory.setLastModifiedBy("test");
        productCategory.setLastModifiedDate(LocalDateTime.now());

        return productCategory;
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
