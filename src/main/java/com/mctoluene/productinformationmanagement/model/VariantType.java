package com.mctoluene.productinformationmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Data
@Table(name = "variant_types")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Where(clause = "status != 'DELETED' ")
public class VariantType extends BaseEntity implements Serializable {

    private String variantTypeName;

    private String description;

    private String status;

}
