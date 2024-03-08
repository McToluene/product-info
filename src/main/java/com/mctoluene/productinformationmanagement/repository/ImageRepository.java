package com.mctoluene.productinformationmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.model.Image;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    @Modifying
    @Query("update Image img set img.status = ?1 where img.imageName = ?2")
    void deleteByImageName(Status status, String imageName);

    Optional<Image> findByImageName(String name);

    Optional<Image> findByUrl(String url);

    @Query("SELECT i FROM Image i WHERE " +
            "((:imageName IS NULL OR :imageName = '') OR LOWER(i.imageName) LIKE LOWER(CONCAT('%', :imageName, '%')))" +
            "AND (i.createdDate BETWEEN :fromDate AND :toDate) " +
            "ORDER BY i.createdDate ASC")
    Page<Image> findByImageNameIgnoreCaseAndCreatedByAndCreatedDate(
            @Param("imageName") String imageName,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

}
