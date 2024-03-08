package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import com.mctoluene.productinformationmanagement.domain.enums.Status;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_categories")
@Where(clause = "status != 'DELETED' ")
@EqualsAndHashCode
public class ProductCategory extends BaseEntity implements Serializable {

    private String productCategoryName;

    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int depth;

    @Column(name = "country_id")
    private UUID countryId;

}
