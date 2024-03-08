package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import com.mctoluene.productinformationmanagement.domain.enums.Status;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "warranty_type")
@Where(clause = "status != 'DELETED' ")
public class WarrantyType extends BaseEntity implements Serializable {

    private String warrantyTypeName;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

}
