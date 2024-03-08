package com.mctoluene.productinformationmanagement.model;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class VariantLocation extends BaseEntity implements Serializable {
    private UUID variantPublicId;
    private UUID locationPublicId;
    private String status;
}