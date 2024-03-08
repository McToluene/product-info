package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductInternalService {
    Product saveProductToDb(Product product);

    Product deleteProduct(Product product);

    Product findByPublicId(UUID publicId);

    Page<Product> getProductsByCategoryIds(List<UUID> productCategoryIds, Pageable pageable);

    Page<Product> findAllBy(Pageable pageable);

    List<Product> findAllById(List<UUID> productIdList);

    boolean checkIfNameExist(String name);

    boolean productNameIsNotUniqueToBrandAndManufacturer(String productName, Brand brand, Manufacturer manufacturer);

    Optional<Product> getProductByNameAndBrandAndManufacturer(String productName, Brand brand,
            Manufacturer manufacturer);

    List<Product> getProductsByBrandId(UUID id);

    List<Product> findByCategory(UUID categoryId);

    Page<Product> findByCategory(Pageable pageable, UUID categoryId);

    Product findByPublicIdAndStatus(UUID publicId);

    List<Product> archiveAllByproductCategoryIn(List<ProductCategory> productCategories);

    Optional<Product> findProductByName(String productName);

    List<Product> saveAllProducts(List<Product> products);

    Product updateProductArchiveStatus(UUID productPublicId, String status);

    Page<Product> searchProducts(String searchValue, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable,
            List<UUID> categoryPublicIds, List<UUID> brandPublicIds, List<UUID> manufacturerPublicIds,
            List<UUID> warrantyTypePublicIds, List<UUID> measuringUnitPublicIds);

    Boolean checkIfManufacturerIsInUse(UUID manufacturerId);

    Boolean checkIfBrandIsInUse(UUID brandId);

    Boolean checkIfCategoryIsInUse(UUID categoryId);
}
