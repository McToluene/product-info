package com.mctoluene.productinformationmanagement.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Builder
@Data
@Table(name = "measuring_unit")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Where(clause = "status != 'DELETED' ")
public class MeasuringUnit extends BaseEntity implements Serializable {

    private String name;

    private String description;

    private String abbreviation;

    private String status;

}
