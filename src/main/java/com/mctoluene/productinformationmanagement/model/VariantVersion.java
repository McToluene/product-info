package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table(name = "variants_version")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Where(clause = "status != 'DELETED' ")
public class VariantVersion implements Serializable {

    @Id
    private UUID id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;
    private String sku;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "variant_type_id")
    private VariantType variantType;

    private String variantName;

    private String variantDescription;

    private BigDecimal costPrice;

    private String defaultImageUrl;

    private Integer threshold;

    private Integer leadTime;

    private String status;

    private String approvalStatus;

    private BigInteger version;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    private String approvedBy;

}