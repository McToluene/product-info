package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.repository.ProductVariantRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ProductVariantInternalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductVariantInternalServiceImpl implements ProductVariantInternalService {
    private final MessageSourceService messageSourceService;

    private final ProductVariantRepository productVariantRepository;

    @Override
    public List<ProductVariant> saveProductVariantToDb(List<ProductVariant> productVariantList) {
        try {

            return productVariantRepository.saveAll(productVariantList);
        } catch (Exception e) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("productVariant.not.found"));
        }
    }

    @Override
    public Optional<ProductVariant> findProductVariantByNameAndProduct(String variantName, Product product) {
        return productVariantRepository.findByVariantNameIgnoreCaseAndProduct(variantName, product);
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
    public ProductVariant findByPublicId(UUID productVariantPublicId) {
        try {
            return productVariantRepository.findByPublicId(productVariantPublicId).get();
        } catch (Exception e) {
            throw new UnProcessableEntityException(
                    messageSourceService.getMessageByKey("product.variant.not.found"));
        }
    }

    @Override
    public List<ProductVariant> archiveAllByProductIdIn(List<Product> listOfProducts) {
        return productVariantRepository.archiveAllByProductIdIn(listOfProducts);
    }

    @Override
    public Page<ProductVariant> findAllProductVariantsPageable(String searchValue, LocalDateTime startDate,
            LocalDateTime endDate,
            String approvalStatus, List<String> listOfVariantStatus,
            Boolean isVated, UUID countryId, Pageable pageable) {

        return productVariantRepository.findVariantsEdited("%" + searchValue.toLowerCase() + "%", approvalStatus,
                listOfVariantStatus, Status.ACTIVE.name(),
                startDate, endDate, isVated, countryId, pageable);

    }

}
