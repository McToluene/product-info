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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.BaseEntity;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.repository.ProductRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ProductInternalServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductInternalServiceImpl productInternalService;

    @Test
    void saveProductToDbTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.save(product)).thenReturn(product);

        var result = productInternalService.saveProductToDb(product);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);

    }

    @Test
    void saveProductToDbExceptionTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> productInternalService.saveProductToDb(product));
    }

    @Test
    void getProductsByCategoryIdsTest() {
        int page = 1, size = 10;
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        List<UUID> productCategoryIds = List.of(UUID.randomUUID());

        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);
        Page<Product> productPage = new PageImpl<>(List.of(product), request, 1);

        when(productRepository.findByProductCategoryIdIn(productCategoryIds, request)).thenReturn(productPage);

        var result = productInternalService.getProductsByCategoryIds(productCategoryIds, request);

        assertThat(result).isNotNull();
        assertThat(result.map(BaseEntity::getPublicId).toList()).isEqualTo(List.of(publicId));
    }

    @Test
    void deleteProductTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.save(product)).thenReturn(product);

        var result = productInternalService.deleteProduct(product);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);

    }

    @Test
    void findByPublicIdTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByPublicId(publicId)).thenReturn(Optional.of(product));

        var result = productInternalService.findByPublicId(publicId);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);

    }

    @Test
    void findByPublicIdExceptionTest() {
        UUID publicId = UUID.randomUUID();

        when(productRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ModelNotFoundException.class, () -> productInternalService.findByPublicId(publicId));
    }

    @Test
    void findAllByTest() {

        int page = 1, size = 10;
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);
        Page<Product> productPage = new PageImpl<>(List.of(product), request, 1);

        when(productRepository.findAllByStatus(Status.ACTIVE.name(), request)).thenReturn(productPage);

        var result = productInternalService.findAllBy(request);

        assertThat(result).isNotNull();
        assertThat(result.map(BaseEntity::getPublicId).toList()).isEqualTo(List.of(publicId));
    }

    @Test
    void findByCategoryTest() {
        UUID categoryId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByCategory(categoryId)).thenReturn(List.of(product));

        var result = productInternalService.findByCategory(categoryId);

        assertThat(result).isNotNull();
        assertThat(result.get(0).getPublicId()).isEqualTo(publicId);
    }

    @Test
    void findByCategoryPageTest() {
        int page = 1, size = 10;
        UUID categoryId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        ProductCategory productCategory = ProductCategory.builder().build();
        productCategory.setId(categoryId);

        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);
        Page<Product> productPage = new PageImpl<>(List.of(product), request, 1);

        when(productRepository.findByProductCategory(productCategory, request)).thenReturn(productPage);

        var result = productInternalService.findByCategory(request, categoryId);

        assertThat(result).isNotNull();
        assertThat(result.map(BaseEntity::getPublicId).toList()).isEqualTo(List.of(publicId));
    }

    @Test
    void findByPublicIdAndStatusTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByPublicIdAndStatus(publicId, Status.ACTIVE.name()))
                .thenReturn(Optional.of(product));

        var result = productInternalService.findByPublicIdAndStatus(publicId);

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);
    }

    @Test
    void archiveAllByproductCategoryInTest() {
        UUID publicId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        ProductCategory productCategory = ProductCategory.builder().build();
        productCategory.setId(categoryId);

        List<Product> products = List.of(product);
        List<ProductCategory> productCategories = List.of(productCategory);

        when(productRepository.archiveAllByproductCategoryIn(productCategories)).thenReturn(products);

        var result = productInternalService.archiveAllByproductCategoryIn(productCategories);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(products);

    }

    @Test
    void findProductByNameTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByProductNameIgnoreCase(product.getProductName()))
                .thenReturn(Optional.of(product));

        var result = productInternalService.findProductByName(product.getProductName());

        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(product);

    }

    @Test
    void saveAllProductsTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        List<Product> products = List.of(product);

        when(productRepository.saveAll(products)).thenReturn(products);

        var result = productInternalService.saveAllProducts(products);

        assertThat(result).isNotNull();
        assertThat(result.get(0)).isEqualTo(product);
    }

    @Test
    void saveAllProductsExceptionTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        List<Product> products = List.of(product);

        when(productRepository.saveAll(products)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ModelNotFoundException.class, () -> productInternalService.saveAllProducts(products));

    }

    @Test
    void updateProductArchiveStatusTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.updateProductArchiveStatus(publicId, Status.ACTIVE.name()))
                .thenReturn(Optional.of(product));

        var result = productInternalService.updateProductArchiveStatus(publicId, Status.ACTIVE.name());

        assertThat(result).isNotNull();
        assertThat(result.getPublicId()).isEqualTo(publicId);

    }

    @Test
    void updateProductArchiveStatusExceptionTest() {
        UUID publicId = UUID.randomUUID();

        when(productRepository.updateProductArchiveStatus(publicId, Status.ACTIVE.name()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> productInternalService.updateProductArchiveStatus(publicId, Status.ACTIVE.name()));

    }

    @Test
    void searchProductsTest() {
        int page = 1, size = 10;
        UUID categoryId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        ProductCategory productCategory = ProductCategory.builder().build();
        productCategory.setId(categoryId);

        List<UUID> uuids = List.of(UUID.randomUUID());

        Pageable request = PageRequest.of(page - 1, size <= 1 ? 10 : size);
        Page<Product> productPage = new PageImpl<>(List.of(product), request, 1);

        when(productRepository.searchProducts(any(), any(), any(), any(), any(),
                any(), any(), any(), any())).thenReturn(productPage);

        var result = productInternalService.searchProducts("test", LocalDateTime.now(),
                LocalDateTime.now().plus(90, ChronoUnit.DAYS), request, uuids, uuids,
                uuids, uuids, uuids);

        assertThat(result).isNotNull();
        assertThat(result.map(BaseEntity::getPublicId).toList()).isEqualTo(List.of(publicId));
    }

    @Test
    void checkIfManufacturerIsInUseTest() {
        UUID manufacturerId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        List<Product> products = List.of(product);

        when(productRepository.findByManufacturerId(manufacturerId)).thenReturn(products);

        var result = productInternalService.checkIfManufacturerIsInUse(manufacturerId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(true);
    }

    @Test
    void checkIfManufacturerIsInUseEmptyListTest() {
        UUID manufacturerId = UUID.randomUUID();
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByManufacturerId(manufacturerId)).thenReturn(List.of());

        var result = productInternalService.checkIfManufacturerIsInUse(manufacturerId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(false);
    }

    @Test
    void findAllByIdTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        List<Product> products = List.of(product);
        List<UUID> uuids = List.of(UUID.randomUUID());

        when(productRepository.findAllById(uuids)).thenReturn(products);

        var result = productInternalService.findAllById(uuids);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(products);

    }

    @Test
    void checkIfNameExistTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByProductNameIgnoreCase(any()))
                .thenReturn(Optional.of(product));

        var result = productInternalService.checkIfNameExist("product");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(true);
    }

    @Test
    void getProductsByBrandIdTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);
        List<Product> products = List.of(product);

        when(productRepository.findByBrandId(any())).thenReturn(products);

        var result = productInternalService.getProductsByBrandId(publicId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(products);
    }

    @Test
    void checkIfBrandIsInUseTest() {

        when(productRepository.findByBrandId(any())).thenReturn(List.of());

        var result = productInternalService.checkIfBrandIsInUse(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(false);
    }

    @Test
    void checkIfBrandIsInUseTrueTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByBrandId(any())).thenReturn(List.of(product));

        var result = productInternalService.checkIfBrandIsInUse(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(true);
    }

    @Test
    void checkIfCategoryIsInUseTest() {
        when(productRepository.findByCategory(any())).thenReturn(List.of());

        var result = productInternalService.checkIfCategoryIsInUse(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(false);
    }

    @Test
    void checkIfCategoryIsInUseTrueTest() {
        UUID publicId = UUID.randomUUID();
        Product product = buildProduct(publicId);

        when(productRepository.findByCategory(any())).thenReturn(List.of(product));

        var result = productInternalService.checkIfCategoryIsInUse(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(true);
    }

    private Product buildProduct(UUID publicId) {
        Product product = Product.builder()
                .productName("smartphones")
                .status("ACTIVE")
                .build();
        product.setPublicId(publicId);
        product.setId(publicId);
        product.setStatus(Status.ACTIVE.name());
        return product;
    }

}
