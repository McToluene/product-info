package com.mctoluene.productinformationmanagement.model;

import lombok.*;

import javax.persistence.*;

import com.mctoluene.productinformationmanagement.domain.enums.Status;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "manufacturers")
public class Manufacturer extends BaseEntity implements Serializable {

    private String description;

    @Column(unique = true)
    private String manufacturerName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "manufacturer")
    private List<Brand> brands;

}
