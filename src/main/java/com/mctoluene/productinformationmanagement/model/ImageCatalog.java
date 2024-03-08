package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Table(name = "image_catalog")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Where(clause = "status != 'DELETED' ")
public class ImageCatalog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @ManyToOne
    @JoinColumn(name = "variant_await_approval_id")
    private VariantAwaitingApproval variantAwaitingApproval;

    @Column(name = "image_catalog_image_name")
    private String imageName;

    private String imageUrl;

    private String imageDescription;

    private String status;
}
