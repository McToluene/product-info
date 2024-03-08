package com.mctoluene.productinformationmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mctoluene.productinformationmanagement.domain.enums.Status;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "brands", uniqueConstraints = {
        @UniqueConstraint(name = "unique_brand_manufacturer", columnNames = { "brandName", "manufacturer_id" })
})
public class Brand extends BaseEntity implements Serializable {

    private String brandName;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;
}
