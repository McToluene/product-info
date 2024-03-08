package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import com.mctoluene.productinformationmanagement.domain.dto.ProductSkuDto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@Table(name = "product_variants")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Where(clause = "status != 'DELETED' ")
public class ProductVariant extends BaseEntity implements Serializable {

    private UUID originalPublicId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    private String approvedBy;

    private UUID countryId;

    @Column(name = "variant_name")
    private String variantName;
    @Column(name = "variant_description")
    private String variantDescription;
    @Column(name = "status")
    private String status;
    @Column(name = "sku")
    private String sku;

    @ManyToOne
    @JoinColumn(name = "variant_type_id")
    private VariantType variantType;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "vated")
    private Boolean isVated;

    @Column(name = "vat_value")
    private BigDecimal vatValue;

}