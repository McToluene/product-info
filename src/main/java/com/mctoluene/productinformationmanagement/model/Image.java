package com.mctoluene.productinformationmanagement.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.mctoluene.productinformationmanagement.domain.enums.Status;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image extends BaseEntity {

    private String url;

    private String imageName;

    @Enumerated(EnumType.STRING)
    private Status status;

}
