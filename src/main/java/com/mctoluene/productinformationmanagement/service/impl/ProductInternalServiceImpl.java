package com.mctoluene.productinformationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;
import com.mctoluene.productinformationmanagement.repository.ProductRepository;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.internal.ProductInternalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductInternalServiceImpl implements ProductInternalService {
    private final MessageSourceService messageSourceService;
    private final ProductRepository productRepository;

    @Override
    public Product saveProductToDb(Product product) {
        try {

            return productRepository.save(product);
        } catch (Exception e) {
            throw new UnProcessableEntityException("Could not process request");
        }
    }

    @Override
    public Page<Product> getProductsByCategoryIds(List<UUID> productCategoryIds, Pageable pageable) {
        return productRepository.findByProductCategoryIdIn(productCategoryIds, pageable);
    }

    @Override
    public Product deleteProduct(Product product) {
        log.info("inside deleteProduct {}", product);
        return saveProductToDb(product);
    }

    @Override
    public Product findByPublicId(UUID publicId) {
        log.info("inside findByPublicId {}", publicId);
        return productRepository.findByPublicId(publicId)
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("product.not.found")));
    }

    @Override
    public Page<Product> findAllBy(Pageable pageable) {
        log.info("Time start to DB <=========== {} ===============>", LocalDateTime.now());
        var result = productRepository.findAllByStatus(Status.ACTIVE.name(), pageable);
        log.info("Response Time from DB <=========== {} ===============> ", LocalDateTime.now());
        return result;
    }

    @Override
    public List<Product> findByCategory(UUID categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    @Override
    public Page<Product> findByCategory(Pageable pageable, UUID categoryId) {
        ProductCategory productCategory = ProductCategory.builder().build();
        productCategory.setId(categoryId);
        return productRepository.findByProductCategory(productCategory, pageable);
    }

    @Override
    public Product findByPublicIdAndStatus(UUID publicId) {
        return productRepository.findByPublicIdAndStatus(publicId, Status.ACTIVE.name())
                .orElseThrow(
                        () -> new ModelNotFoundException(messageSourceService.getMessageByKey("product.not.found")));
    }

    @Override
    public List<Product> archiveAllByproductCategoryIn(List<ProductCategory> productCategories) {
        return productRepository.archiveAllByproductCategoryIn(productCategories);
    }

    @Override
    public Optional<Product> findProductByName(String productName) {
        return productRepository.findByProductNameIgnoreCase(productName);
    }

    @Override
    public List<Product> saveAllProducts(List<Product> products) {

        try {
            return productRepository.saveAll(products);
        } catch (Exception e) {
            throw new ModelNotFoundException(messageSourceService.getMessageByKey("product.not.found"));
        }
    }

    @Override
    public Product updateProductArchiveStatus(UUID productPublicId, String status) {
        return productRepository.updateProductArchiveStatus(productPublicId, status)
                .orElseThrow(() -> new UnProcessableEntityException(
                        messageSourceService.getMessageByKey("product.not.archived")));
    }

    @Override
    public Page<Product> searchProducts(String searchValue, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable, List<UUID> categoryPublicIds,
            List<UUID> brandPublicIds, List<UUID> manufacturerPublicIds, List<UUID> warrantyTypePublicIds,
            List<UUID> measuringUnitPublicIds) {
        return productRepository.searchProducts("%" + searchValue.toLowerCase() + "%", startDate, endDate, pageable,
                categoryPublicIds,
                brandPublicIds, manufacturerPublicIds, warrantyTypePublicIds, measuringUnitPublicIds);
    }

    @Override
    public Boolean checkIfManufacturerIsInUse(UUID manufacturerId) {
        return !getManufacturerInUse(manufacturerId).isEmpty();
    }

    private List<Product> getManufacturerInUse(UUID manufacturerId) {
        return productRepository.findByManufacturerId(manufacturerId);
    }

    @Override
    public List<Product> findAllById(List<UUID> productIdList) {
        return productRepository.findAllById(productIdList);
    }

    public boolean checkIfNameExist(String productName) {
        var response = productRepository.findByProductNameIgnoreCase(productName);
        return response.isPresent();
    }

    public boolean productNameIsNotUniqueToBrandAndManufacturer(String productName, Brand brand,
            Manufacturer manufacturer) {
        var response = getProductByNameAndBrandAndManufacturer(productName, brand, manufacturer);
        return response.isPresent();
    }

    @Override
    public Optional<Product> getProductByNameAndBrandAndManufacturer(String productName, Brand brand,
            Manufacturer manufacturer) {
        return productRepository.findByProductNameIgnoreCaseAndBrandIdAndManufacturerId(productName.trim(),
                brand.getId(), manufacturer.getId());
    }

    @Override
    public List<Product> getProductsByBrandId(UUID brandId) {
        return productRepository.findByBrandId(brandId);
    }

    @Override
    public Boolean checkIfBrandIsInUse(UUID brandId) {
        return !getProductsByBrandId(brandId).isEmpty();
    }

    @Override
    public Boolean checkIfCategoryIsInUse(UUID categoryId) {
        return !findByCategory(categoryId).isEmpty();
    }

}
