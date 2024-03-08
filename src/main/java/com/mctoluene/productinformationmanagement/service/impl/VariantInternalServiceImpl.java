package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.response.productVariant.ProductVariantVatResponseDto;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.helper.VariantHelper;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantVersion;
import com.mctoluene.productinformationmanagement.repository.ProductRepository;
import com.mctoluene.productinformationmanagement.repository.ProductVariantRepository;
import com.mctoluene.productinformationmanagement.repository.VariantVersionRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.VariantInternalService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VariantInternalServiceImpl implements VariantInternalService {
    private final MessageSourceService messageSourceService;

    private final ProductVariantRepository productVariantRepository;

    private final ProductRepository productRepository;

    private final VariantVersionRepository variantVersionRepository;

    @Override
    public Page<VariantVersion> searchVariants(String searchValue, Pageable pageable) {
        return variantVersionRepository.searchVariant(searchValue, pageable);
    }

    @Override
    public Page<VariantVersion> findAllVariants(String searchValue, LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfStatus, Pageable pageable) {
        List<ProductVariant> productVariants = productVariantRepository.findAllByStatusIn(listOfStatus);
        if (productVariants.isEmpty())
            return Page.empty();

        return variantVersionRepository.findAllByStatusAndProductVariantIn("%" + searchValue.toLowerCase() + "%",
                startDate, endDate,
                approvalStatus, Status.ACTIVE.name(), productVariants, pageable);
    }

    @Override
    public Page<VariantVersion> findAllVariantsPageable(String searchValue, LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, List<String> listOfVariantStatus, Pageable pageable) {

        return variantVersionRepository.findVariantsEdited("%" + searchValue.toLowerCase() + "%", approvalStatus,
                listOfVariantStatus, Status.ACTIVE.name(),
                startDate, endDate, pageable);
    }

    @Override
    public List<VariantVersion> findAllByStatusAndProductVariantIn(UUID productId) {

        List<ProductVariant> variantList = productVariantRepository.findByProductIdAndStatus(productId,
                Status.ACTIVE.name());
        return variantVersionRepository.findAllByStatusAndProductVariantIn(Status.ACTIVE.name(), variantList);
    }

    @Override
    public List<ProductVariant> findProductVariantsByPublicIds(List<UUID> publicIds) {
        return productVariantRepository.findByStatusAndPublicIdIn(Status.ACTIVE.name(), publicIds);

    }

    @Override
    public List<ProductVariant> findAllByProductId(UUID productId) {
        return productVariantRepository.findByProductIdAndStatus(productId,
                Status.ACTIVE.name());
    }

    @Override
    public ProductVariant saveProductVariantToDb(ProductVariant productVariant) {
        try {
            return productVariantRepository.save(productVariant);
        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("variant.not.saved"));
        }
    }

    @Override
    public ProductVariant deleteProductVariant(ProductVariant productVariant) {
        return saveProductVariantToDb(productVariant);
    }

    @Override
    public Optional<ProductVariant> findProductVariantByPublicId(UUID publicId) {
        return productVariantRepository.findByPublicId(publicId);
    }

    @Override
    public Optional<ProductVariant> findByPublicIdAndStatusNot(UUID publicId, String status) {
        return productVariantRepository.findByPublicIdAndStatusNot(publicId, status);
    }

    @Override
    public Optional<VariantVersion> findVariantByProductVariant(ProductVariant productVariant) {
        return variantVersionRepository.findByStatusAndProductVariant(Status.ACTIVE.name(), productVariant);
    }

    @Override
    public Page<VariantVersion> findAllVariantsByCategoryPublicIds(String searchValue,
            List<ProductCategory> productCategories, LocalDateTime startDate, LocalDateTime endDate,
            String approvalStatus, List<String> listOfStatus, Pageable pageable) {

        List<Product> products = productRepository.findAllByProductCategoryInAndStatusIn(productCategories,
                listOfStatus);

        List<ProductVariant> productVariants = productVariantRepository.findAllByProductInAndStatusIn(products,
                listOfStatus);

        if (productVariants.isEmpty())
            return Page.empty();

        return variantVersionRepository.findAllByStatusAndProductVariantIn("%" + searchValue.toLowerCase() + "%",
                startDate, endDate,
                approvalStatus, Status.ACTIVE.name(), productVariants, pageable);
    }

    @Override
    public List<VariantVersion> findProductVariantsByPublicIdsAndStatusAndFilter(List<UUID> variantPublicIds,
            List<String> status, String searchValue, List<UUID> categoryPublicIds) {
        return variantVersionRepository.findByPublicIdInAndStatusAndFilter(variantPublicIds, status, searchValue,
                categoryPublicIds);
    }

    @Override
    public List<VariantVersion> findByCategoryPublicIds(Set<UUID> categoryPublicIds) {
        return variantVersionRepository.findByCategoryPublicIds(categoryPublicIds);
    }

    @Override
    public List<ProductVariant> updateProductVariantsArchiveStatus(Product product, String status) {
        return productVariantRepository.updateProductVariantsArchiveStatusByProduct(product, status);
    }

    @Override
    public List<ProductVariant> getAllProductVariants() {
        return productVariantRepository.findAll();
    }

    @Override
    @Cacheable(value = "productsVatRatio")
    public Map<String, ProductVariantVatResponseDto> getProductVariantVatValueRatio(List<UUID> productVariantPublicIds,
            List<String> productVariantSKUs) {
        log.info("Cached getProductVariantVatValueRatio");
        return VariantHelper.convertToMap(productVariantRepository
                .getProductVariantsByPublicIdInOrSkuIn(productVariantPublicIds, productVariantSKUs));
    }

    @Override
    public List<ProductVariant> findVariantPublicIdBySkus(List<String> skus) {
        return productVariantRepository.findProductVariantBySkuIn(skus);
    }

}
