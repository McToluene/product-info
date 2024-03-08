package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Table(name = "variants_awaiting_approval")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Where(clause = "status != 'DELETED' ")
public class VariantAwaitingApproval extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "variant_type_id")
    private VariantType variantType;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String variantName;

    private String variantDescription;

    private String sku;

    private BigDecimal costPrice;

    private String defaultImageUrl;

    private Integer leadTime;

    private Integer threshold;

    private String status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    private String approvalStatus;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    private String completedBy;

    private String rejectedReason;

    private String productVariantDetails;

    private UUID countryId;

    private Double weight;

    @Column(name = "vated")
    private Boolean isVated;

    private BigDecimal vatValue;

}
