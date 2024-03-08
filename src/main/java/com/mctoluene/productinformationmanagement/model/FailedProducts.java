package com.mctoluene.productinformationmanagement.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Builder
@Data
@Table(name = "failed_products")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class FailedProducts extends BaseEntity {

    private String productName;

    private String brandName;

    private String manufacturerName;

    private String productCategoryName;

    private String measurementUnit;

    private String productListing;

    private String defaultImageUrl;

    private String productDescription;

    private String productHighlights;

    private String warrantyDuration;

    private String warrantyCover;

    private String warrantyType;

    private String warrantyAddress;

    private String productCountry;

    private String status;

    private String productDetails;

    private String reason;

}
