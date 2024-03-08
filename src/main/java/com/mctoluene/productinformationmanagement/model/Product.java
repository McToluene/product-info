package com.mctoluene.productinformationmanagement.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
// @Where(clause = "status != 'DELETED' ")
public class Product extends BaseEntity implements Serializable {

    private String productName;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "warranty_type_id")
    private WarrantyType warrantyType;

    @ManyToOne
    @JoinColumn(name = "measuring_unit_id")
    private MeasuringUnit measurementUnit;

    private String productListing;

    private String productDescription;

    private String productHighlights;

    private String warrantyDuration;

    private String warrantyCover;

    private String warrantyAddress;

    private String status;

    private String productNotes;

    private Boolean vated;

    private BigDecimal minVat;

    private BigDecimal maxVat;
}
