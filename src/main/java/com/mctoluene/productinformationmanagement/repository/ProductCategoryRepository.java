package com.mctoluene.productinformationmanagement.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.model.ProductCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, UUID>, JpaSpecificationExecutor<ProductCategory> {

    @Query(value = "select pc.* from product_categories pc where pc.public_id = :publicId AND pc.status LIKE 'ACTIVE' and "
            +
            " ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR} or country_id is null)", nativeQuery = true)
    Optional<ProductCategory> findByPublicId(UUID publicId);

    @Query(value = "select pc.* from product_categories pc where pc.public_id IN :publicIdList AND pc.status LIKE 'ACTIVE' "
            +
            "( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR})", nativeQuery = true)
    List<ProductCategory> findByPublicIdIn(List<UUID> publicIdList);

    Optional<ProductCategory> findByProductCategoryName(String name);

    Optional<ProductCategory> findByProductCategoryNameIgnoreCase(String name);

    @Query(value = "SELECT pc.* from product_categories pc INNER JOIN "
            + " product_category_hierarchy pch ON pc.public_id = pch.product_category_public_id "
            + " WHERE pch.product_category_parent_public_id IS NULL AND pc.status LIKE 'ACTIVE' " +
            " and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR}) ", nativeQuery = true)
    Page<ProductCategory> findAllParentProductCategory(Pageable pageable);

    @Query(value = "select pc.* from product_categories pc inner join "
            + " product_category_hierarchy pch on pc.public_id = pch.product_category_public_id "
            + " where pch.product_category_parent_public_id = :publicId AND pc.status LIKE 'ACTIVE' " +
            "and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR}) ", nativeQuery = true)
    Page<ProductCategory> findAllDirectChildrenOfProductCategory(UUID publicId, Pageable pageable);

    @Query(value = "select pc.* from product_categories pc inner join "
            + " product_category_hierarchy pch on pc.public_id = pch.product_category_public_id "
            + " where pch.product_category_parent_public_id = :publicId AND pc.status LIKE 'ACTIVE' " +
            " and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR})", nativeQuery = true)
    List<ProductCategory> findAllDirectChildrenOfProductCategoryUnpaged(UUID publicId);

    @Query(value = "SELECT * FROM product_categories pc WHERE lower(pc.product_category_name) ILIKE %:name% " +
            "AND pc.status LIKE 'ACTIVE' " +
            "and (pc.created_date between :from and :to)" +
            "and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR}) ", nativeQuery = true)
    Page<ProductCategory> findAllByProductCategoryNameIgnoreCaseContainingLike(Pageable pageable,
            @Param("name") String productCategoryName, LocalDateTime from, LocalDateTime to);

    @Query(nativeQuery = true, value = "UPDATE product_categories SET status = 'INACTIVE' WHERE public_id IN(:listOfPublicIds) RETURNING *")
    List<ProductCategory> archiveCategoryAndSubCategoriesByPublicIds(List<UUID> listOfPublicIds);

    @Query(value = "select pc.* from product_categories pc where pc.public_id = :publicId AND pc.status = :status.name "
            +
            " and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR})", nativeQuery = true)
    Optional<ProductCategory> findByPublicIdAndStatus(UUID publicId, Status status);

    @Query(value = "SELECT pc.* from product_categories pc INNER JOIN "
            + " product_category_hierarchy pch ON pc.public_id = pch.product_category_parent_public_id "
            + " WHERE pch.product_category_public_id = :productCategoryPublicId AND pc.status LIKE 'ACTIVE' " +
            " and ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR})", nativeQuery = true)
    Optional<ProductCategory> findParentProductCategory(UUID productCategoryPublicId);

    @Query(value = "select pc.* from product_categories pc where ( country_id = :#{#locale.country} or country_id = :#{#locale.countryZAR})", nativeQuery = true)
    List<ProductCategory> findByCountryId();
}
