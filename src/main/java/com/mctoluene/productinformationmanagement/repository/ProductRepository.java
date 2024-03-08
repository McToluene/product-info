package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.model.Manufacturer;
import com.mctoluene.productinformationmanagement.model.Product;
import com.mctoluene.productinformationmanagement.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByPublicId(UUID publicId);

    List<Product> findByPublicIdIn(List<UUID> prodPublicIdList);

    Optional<Product> findByProductNameIgnoreCase(String productName);

    Optional<Product> findByProductNameIgnoreCaseAndBrandIdAndManufacturerId(String productName, UUID brand,
            UUID manufacturer);

    Page<Product> findByProductCategoryIdIn(List<UUID> productCategoryIds, Pageable pageable);

    @Query("select p from Product p where p.status=?1")
    Page<Product> findAllByStatus(String status, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from products where brand_id = :brandId and status='ACTIVE'")
    List<Product> findByBrandId(UUID brandId);

    @Query(nativeQuery = true, value = "select * from products where category_Id = :categoryId and status='ACTIVE'")
    List<Product> findByCategory(UUID categoryId);

    Page<Product> findByProductCategory(ProductCategory categoryId, Pageable pageable);

    Optional<Product> findByPublicIdAndStatus(UUID publicId, String status);

    @Query(nativeQuery = true, value = "UPDATE products SET status = 'INACTIVE' where category_id IN(:productCategories) RETURNING *")
    List<Product> archiveAllByproductCategoryIn(List<ProductCategory> productCategories);

    List<Product> findAllByProductCategoryInAndStatusIn(List<ProductCategory> productCategories, List<String> status);

    @Query(nativeQuery = true, value = "UPDATE products SET status = :status where public_id = :productPublicId RETURNING *")
    Optional<Product> updateProductArchiveStatus(UUID productPublicId, String status);

    @Query(value = """
                select p.* from products p
                LEFT JOIN brands b ON b.id = p.brand_id
                LEFT JOIN product_categories pc ON pc.id = p.category_id
                LEFT JOIN manufacturers mc ON mc.id = p.manufacturer_id
                LEFT JOIN measuring_unit mu ON mu.id = p.measuring_unit_id
                LEFT JOIN warranty_type wt ON wt.id = p.warranty_type_id
                where (lower(p.product_name)  like :searchParam) and (p.created_date between :from and :to)
                AND ( COALESCE(:categoryPublicIds) IS NULL OR pc.public_id IN (:categoryPublicIds) )
                AND ( COALESCE(:brandPublicIds) IS NULL OR b.public_id IN (:brandPublicIds) )
                AND ( COALESCE(:manufacturerPublicIds) IS NULL OR mc.public_id IN (:manufacturerPublicIds) )
                AND ( COALESCE(:warrantyTypePublicIds) IS NULL OR wt.public_id IN (:warrantyTypePublicIds) )
                AND ( COALESCE(:measuringUnitPublicIds) IS NULL OR mu.public_id IN (:measuringUnitPublicIds) )
                ORDER BY p.created_date DESC
            """, nativeQuery = true)
    Page<Product> searchProducts(String searchParam, LocalDateTime from, LocalDateTime to, Pageable pageable,
            List<UUID> categoryPublicIds, List<UUID> brandPublicIds,
            List<UUID> manufacturerPublicIds, List<UUID> warrantyTypePublicIds, List<UUID> measuringUnitPublicIds);

    @Query(nativeQuery = true, value = "select * from products where manufacturer_id = :manufacturerId and status='ACTIVE'")
    List<Product> findByManufacturerId(UUID manufacturerId);
}
