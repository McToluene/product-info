package com.mctoluene.productinformationmanagement.service.internal;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantVersion;
import com.mctoluene.productinformationmanagement.repository.ProductRepository;
import com.mctoluene.productinformationmanagement.repository.ProductVariantRepository;
import com.mctoluene.productinformationmanagement.repository.VariantVersionRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.impl.VariantInternalServiceImpl;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VariantInternalServiceImplTest {

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private VariantVersionRepository variantVersionRepository;

    @InjectMocks
    private VariantInternalServiceImpl variantInternalService;

    @Test
    void searchVariantsTest() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(variantVersionRepository.searchVariant(any(), any())).thenReturn(variantVersions);

        var result = variantInternalService.searchVariants("search", request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersions);
    }

    @Test
    void findAllVariantsTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(productVariantRepository.findAllByStatusIn(any())).thenReturn(List.of(productVariant));
        when(variantVersionRepository.findAllByStatusAndProductVariantIn(any(), any(), any(), any(), any(), any(),
                any())).thenReturn(variantVersions);

        var result = variantInternalService.findAllVariants("", LocalDateTime.parse("2015-08-04T00:00:00"),
                LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.PENDING.name(),
                List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersions);
    }

    @Test
    void findAllVariantsEmptyPageTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(productVariantRepository.findAllByStatusIn(any())).thenReturn(List.of());
        when(variantVersionRepository.findAllByStatusAndProductVariantIn(any(), any(), any(), any(), any(), any(),
                any())).thenReturn(variantVersions);

        var result = variantInternalService.findAllVariants("", LocalDateTime.parse("2015-08-04T00:00:00"),
                LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.PENDING.name(),
                List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Page.empty());
    }

    @Test
    void findAllVariantsPageableTest() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(variantVersionRepository.findVariantsEdited(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(variantVersions);

        var result = variantInternalService.findAllVariantsPageable("", LocalDateTime.parse("2015-08-04T00:00:00"),
                LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.PENDING.name(),
                List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersions);
    }

    @Test
    void findAllByStatusAndProductVariantIn() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findByProductIdAndStatus(any(), any()))
                .thenReturn(List.of(productVariant));
        when(variantVersionRepository.findAllByStatusAndProductVariantIn(any(), any()))
                .thenReturn(List.of(variantVersion));

        var result = variantInternalService.findAllByStatusAndProductVariantIn(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void findProductVariantsByPublicIdsTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findByStatusAndPublicIdIn(any(), any())).thenReturn(List.of(productVariant));

        var result = variantInternalService.findProductVariantsByPublicIds(List.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productVariant));
    }

    @Test
    void findAllByProductIdTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findByProductIdAndStatus(any(), any())).thenReturn(List.of(productVariant));

        var result = variantInternalService.findAllByProductId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productVariant));
    }

    @Test
    void saveProductVariantToDbTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.save(any())).thenReturn(productVariant);

        var result = variantInternalService.saveProductVariantToDb(productVariant);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productVariant);
    }

    @Test
    void saveProductVariantToDbExceptionTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(UnProcessableEntityException.class,
                () -> variantInternalService.saveProductVariantToDb(productVariant));
    }

    @Test
    void deleteProductVariantTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.save(any())).thenReturn(productVariant);

        var result = variantInternalService.deleteProductVariant(productVariant);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productVariant);
    }

    @Test
    void findProductVariantByPublicIdTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findByPublicId(any())).thenReturn(Optional.of(productVariant));

        var result = variantInternalService.findProductVariantByPublicId(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(productVariant));
    }

    @Test
    void findByPublicIdAndStatusNotTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findByPublicIdAndStatusNot(any(), any()))
                .thenReturn(Optional.of(productVariant));

        var result = variantInternalService.findByPublicIdAndStatusNot(UUID.randomUUID(), Status.ACTIVE.name());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(productVariant));
    }

    @Test
    void findVariantByProductVariantTest() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(variantVersionRepository.findByStatusAndProductVariant(any(), any()))
                .thenReturn(Optional.of(variantVersion));

        var result = variantInternalService.findVariantByProductVariant(productVariant);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(variantVersion));
    }

    @Test
    void findAllVariantsByCategoryPublicIdsTest() {
        Product product = EntityHelpers.buildProduct();
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        ProductCategory productCategory = EntityHelpers.buildProductCategory();

        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(productRepository.findAllByProductCategoryInAndStatusIn(any(), any())).thenReturn(List.of(product));
        when(productVariantRepository.findAllByProductInAndStatusIn(any(), any())).thenReturn(List.of(productVariant));
        when(variantVersionRepository.findAllByStatusAndProductVariantIn(any(), any(), any(), any(), any(), any(),
                any())).thenReturn(variantVersions);

        var result = variantInternalService.findAllVariantsByCategoryPublicIds("", List.of(productCategory),
                LocalDateTime.parse("2015-08-04T00:00:00"),
                LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.PENDING.name(),
                List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(variantVersions);
    }

    @Test
    void findAllVariantsByCategoryPublicIdsEmptyPageTest() {
        Product product = EntityHelpers.buildProduct();
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();
        ProductCategory productCategory = EntityHelpers.buildProductCategory();

        PageRequest request = PageRequest.of(1, 10);
        PageImpl<VariantVersion> variantVersions = new PageImpl<>(List.of(variantVersion), request, 1);

        when(productRepository.findAllByProductCategoryInAndStatusIn(any(), any())).thenReturn(List.of(product));
        when(productVariantRepository.findAllByProductInAndStatusIn(any(), any())).thenReturn(List.of());
        when(variantVersionRepository.findAllByStatusAndProductVariantIn(any(), any(), any(), any(), any(), any(),
                any())).thenReturn(variantVersions);

        var result = variantInternalService.findAllVariantsByCategoryPublicIds("", List.of(productCategory),
                LocalDateTime.parse("2015-08-04T00:00:00"),
                LocalDateTime.parse("2023-08-04T23:59:59.999999999"), ApprovalStatus.PENDING.name(),
                List.of(Status.ACTIVE.name()), request);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Page.empty());
    }

    @Test
    void findProductVariantsByPublicIdsAndStatusAndFilterTest() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();

        when(variantVersionRepository.findByPublicIdInAndStatusAndFilter(any(), any(), any(), any()))
                .thenReturn(List.of(variantVersion));

        var result = variantInternalService.findProductVariantsByPublicIdsAndStatusAndFilter(List.of(UUID.randomUUID()),
                List.of(Status.ACTIVE.name()),
                "", List.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));
    }

    @Test
    void findByCategoryPublicIds() {
        VariantVersion variantVersion = EntityHelpers.buildVariantVersion();

        when(variantVersionRepository.findByCategoryPublicIds(any())).thenReturn(List.of(variantVersion));

        var result = variantInternalService.findByCategoryPublicIds(Set.of(UUID.randomUUID()));

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(variantVersion));

    }

    @Test
    void updateProductVariantsArchiveStatus() {
        Product product = EntityHelpers.buildProduct();
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.updateProductVariantsArchiveStatusByProduct(any(), any()))
                .thenReturn(List.of(productVariant));

        var result = variantInternalService.updateProductVariantsArchiveStatus(product, "");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productVariant));
    }

    @Test
    void getAllProductVariants() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();

        when(productVariantRepository.findAll()).thenReturn(List.of(productVariant));

        var result = variantInternalService.getAllProductVariants();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(productVariant));
    }
}
