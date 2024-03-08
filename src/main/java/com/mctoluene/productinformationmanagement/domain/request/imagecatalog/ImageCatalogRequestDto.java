package com.mctoluene.productinformationmanagement.domain.request.imagecatalog;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Builder
@Data
public class ImageCatalogRequestDto {

    @NotEmpty(message = "image url cannot be empty")
    private String imageUrl;

    @NotEmpty(message = "image name cannot be empty")
    private String imageName;

    private String imageDescription;

    @NotEmpty(message = "created by cannot be empty")
    private String createdBy;
}
