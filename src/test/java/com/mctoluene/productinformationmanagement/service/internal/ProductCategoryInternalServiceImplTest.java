package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.repository.ProductCategoryRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ProductCategoryInternalServiceImpl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductCategoryInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductCategoryInternalServiceImpl productCategoryInternalService;

    @Test
    void saveNewProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByProductCategoryName(any())).thenReturn(Optional.empty());
        when(productCategoryRepository.save(any())).thenReturn(productCategory);

        var result = productCategoryInternalService.saveNewProductCategory(productCategory);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategory);

    }

    @Test
    void saveNewProductCategoryDuplicateCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByProductCategoryName(any())).thenReturn(Optional.of(productCategory));
        when(productCategoryRepository.save(any())).thenReturn(productCategory);

        Assertions.assertThrows(ValidatorException.class,
                () -> productCategoryInternalService.saveNewProductCategory(productCategory));
    }

    @Test
    void saveNewProductCategoryUnProcessableEntityTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByProductCategoryName(any())).thenReturn(Optional.empty());
        when(productCategoryRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> productCategoryInternalService.saveNewProductCategory(productCategory));
    }

    @Test
    void findProductCategoryByNameTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByProductCategoryName(any())).thenReturn(Optional.of(productCategory));

        var result = productCategoryInternalService.findProductCategoryByName("productCategoryName");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategory);

    }

    @Test
    void findProductCategoryByNameModelNotFoundTest() {

        when(productCategoryRepository.findByProductCategoryName(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryInternalService.findProductCategoryByName("productCategoryName"));

    }

    @Test
    void findProductCategoryByNameIgnoreCaseTest() {
        ProductCategory productCategory = buildProductCategory();

        when(productCategoryRepository.findByProductCategoryNameIgnoreCase(any()))
                .thenReturn(Optional.of(productCategory));

        var result = productCategoryInternalService.findProductCategoryByNameIgnoreCase("productCategoryName");

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(productCategory);
    }

    @Test
    void findProductCategoryByPublicIdTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByPublicId(any())).thenReturn(Optional.of(productCategory));

        var result = productCategoryInternalService.findProductCategoryByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategory);

    }

    @Test
    void findProductCategoryByPublicIdModelNotFoundTest() {

        when(productCategoryRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryInternalService.findProductCategoryByPublicId(UUID.randomUUID()));

    }

    @Test
    void findProductCategoryByPublicIdsTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByPublicIdIn(any())).thenReturn(List.of(productCategory));

        var result = productCategoryInternalService.findProductCategoryByPublicIds(List.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllProductCategoriesTest() {
        ProductCategory productCategory = buildProductCategory();
        PageRequest request = PageRequest.of(1, 10);
        when(productCategoryRepository.findAll(request))
                .thenReturn(new PageImpl<>(List.of(productCategory), request, 1));

        var result = productCategoryInternalService.getAllProductCategories(request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllParentProductCategoriesTest() {
        ProductCategory productCategory = buildProductCategory();
        PageRequest request = PageRequest.of(1, 10);
        when(productCategoryRepository.findAllParentProductCategory(request))
                .thenReturn(new PageImpl<>(List.of(productCategory), request, 1));

        var result = productCategoryInternalService.getAllParentProductCategories(request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllDirectChildrenOfProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        PageRequest request = PageRequest.of(1, 10);
        when(productCategoryRepository.findAllDirectChildrenOfProductCategory(any(), any()))
                .thenReturn(new PageImpl<>(List.of(productCategory), request, 1));

        var result = productCategoryInternalService.getAllDirectChildrenOfProductCategory(UUID.randomUUID(), request);

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllDirectChildrenOfProductCategoryUnpagedTest() {
        ProductCategory productCategory = buildProductCategory();

        when(productCategoryRepository.findAllDirectChildrenOfProductCategoryUnpaged(any()))
                .thenReturn(List.of(productCategory));

        var result = productCategoryInternalService.getAllDirectChildrenOfProductCategoryUnpaged(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllChildrenOfProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByPublicId(any())).thenReturn(Optional.of(productCategory));

        var result = productCategoryInternalService.getAllChildrenOfProductCategory(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productCategory));
    }

    @Test
    void getAllChildrenOfProductCategoryProductCategoryNotFoundTest() {

        when(productCategoryRepository.findByPublicId(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryInternalService.getAllChildrenOfProductCategory(UUID.randomUUID()));
    }

    @Test
    void findAllBySearchCrieteriaTest() {
        ProductCategory productCategory = buildProductCategory();
        productCategory.setCreatedDate(LocalDateTime.now());
        PageRequest request = PageRequest.of(1, 10);
        Page<ProductCategory> productCategoryPage = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryRepository.findAllByProductCategoryNameIgnoreCaseContainingLike(any(), any(), any(), any()))
                .thenReturn(productCategoryPage);

        var result = productCategoryInternalService.findAllBySearchCrieteria(request, "productCategoryName",
                LocalDateTime.parse("2015-08-04T00:00:00"), LocalDateTime.parse("2024-08-04T23:59:59.999999999"));

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(productCategory));
    }

    @Test
    void findAllBySearchCrieteriaEmptyProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        productCategory.setCreatedDate(LocalDateTime.now());
        PageRequest request = PageRequest.of(1, 10);
        Page<ProductCategory> productCategoryPage = new PageImpl<>(List.of(productCategory), request, 1);

        when(productCategoryRepository.findAllByProductCategoryNameIgnoreCaseContainingLike(any(), any(), any(), any()))
                .thenReturn(productCategoryPage);
        when(productCategoryRepository.findAll(request))
                .thenReturn(new PageImpl<>(List.of(productCategory), request, 1));

        var result = productCategoryInternalService.findAllBySearchCrieteria(request, "productCategoryName",
                LocalDateTime.parse("2015-08-04T00:00:00"), LocalDateTime.parse("2024-08-04T23:59:59.999999999"));

        assertThat(result).isNotNull();
        assertThat(result.toList()).isEqualTo(List.of(productCategory));
    }

    @Test
    void archiveCategoryAndSubCategoriesByPublicIdsTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.archiveCategoryAndSubCategoriesByPublicIds(any()))
                .thenReturn(List.of(productCategory));

        var result = productCategoryInternalService
                .archiveCategoryAndSubCategoriesByPublicIds(List.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productCategory));
    }

    @Test
    void findByPublicIdAndStatusTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.findByPublicIdAndStatus(any(), any())).thenReturn(Optional.of(productCategory));

        var result = productCategoryInternalService.findByPublicIdAndStatus(UUID.randomUUID(), Status.ACTIVE);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategory);
    }

    @Test
    void findByPublicIdAndStatusModelNotFoundTest() {
        when(productCategoryRepository.findByPublicIdAndStatus(any(), any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productCategoryInternalService.findByPublicIdAndStatus(UUID.randomUUID(), Status.ACTIVE));
    }

    @Test
    void updateExistingProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.save(productCategory)).thenReturn(productCategory);

        var result = productCategoryInternalService.updateExistingProductCategory(productCategory);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productCategory);
    }

    @Test
    void deleteProductCategoryTest() {
        ProductCategory productCategory = buildProductCategory();
        when(productCategoryRepository.save(productCategory)).thenReturn(productCategory);
        productCategoryInternalService.deleteProductCategory(productCategory);

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

}
