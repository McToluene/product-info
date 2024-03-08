package com.mctoluene.productinformationmanagement.service.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.repository.ProductVariantRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.ProductVariantInternalServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductVariantInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private ProductVariantInternalServiceImpl productVariantInternalService;

    @Test
    void saveProductVariantToDbTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);
        List<ProductVariant> productVariants = List.of(productVariant);

        when(productVariantRepository.saveAll(productVariants)).thenReturn(productVariants);

        var result = productVariantInternalService.saveProductVariantToDb(productVariants);

        assertThat(result).isNotNull();
        assertThat(result.get(0).getOriginalPublicId()).isEqualTo(publicId);
    }

    @Test
    void saveProductVariantToDbExceptionTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);
        List<ProductVariant> productVariants = List.of(productVariant);

        when(productVariantRepository.saveAll(productVariants)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(ModelNotFoundException.class,
                () -> productVariantInternalService.saveProductVariantToDb(productVariants));
    }

    @Test
    void saveSingleProductVariantToDbTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);

        when(productVariantRepository.save(productVariant)).thenReturn(productVariant);

        var result = productVariantInternalService.saveProductVariantToDb(productVariant);

        assertThat(result).isNotNull();
        assertThat(result.getOriginalPublicId()).isEqualTo(publicId);
    }

    @Test
    void saveSingleProductVariantToDbExceptionTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);

        when(productVariantRepository.save(productVariant)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> productVariantInternalService.saveProductVariantToDb(productVariant));
    }

    @Test
    void findByPublicIdTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);

        when(productVariantRepository.findByPublicId(publicId)).thenReturn(Optional.of(productVariant));

        var result = productVariantInternalService.findByPublicId(publicId);

        assertThat(result).isNotNull();
        assertThat(result.getOriginalPublicId()).isEqualTo(publicId);
    }

    @Test
    void findByPublicIdExceptionTest() {
        UUID publicId = UUID.randomUUID();

        when(productVariantRepository.findByPublicId(publicId)).thenThrow(UnProcessableEntityException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> productVariantInternalService.findByPublicId(publicId));
    }

    @Test
    void archiveAllByProductIdInTest() {
        UUID publicId = UUID.randomUUID();
        ProductVariant productVariant = buildProductVariant(publicId);
        List<ProductVariant> productVariants = List.of(productVariant);
        List<Product> products = List.of(Product.builder().build());

        when(productVariantRepository.archiveAllByProductIdIn(products)).thenReturn(productVariants);

        var result = productVariantInternalService.archiveAllByProductIdIn(products);

        assertThat(result).isNotNull();
        assertThat(result.get(0).getOriginalPublicId()).isEqualTo(publicId);
    }

    private ProductVariant buildProductVariant(UUID originalPublicId) {
        ProductVariant productVariant = ProductVariant.builder()
                .originalPublicId(originalPublicId)
                .status(Status.ACTIVE.name())
                .product(Product.builder()
                        .productName("test")
                        .build())
                .build();
        productVariant.setPublicId(originalPublicId);
        productVariant.setId(originalPublicId);
        return productVariant;
    }

}
