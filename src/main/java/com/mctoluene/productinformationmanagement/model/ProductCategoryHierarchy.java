package com.mctoluene.productinformationmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@Table(name = "product_category_hierarchy")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductCategoryHierarchy extends BaseEntity implements Serializable {

    private UUID productCategoryPublicId;

    private UUID productCategoryParentPublicId;

}
