package com.mctoluene.productinformationmanagement.domain.request.imagecatalog;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Builder
@Data
public class UpdateImageCatalogRequestDto {

    private UUID imageCatalogPublicId;

    @NotEmpty(message = "image url cannot be empty")
    private String imageUrl;

    @NotEmpty(message = "image name cannot be empty")
    private String imageName;

    private String imageDescription;

    @NotEmpty(message = "{empty.modified.by}")
    private String modifiedBy;
}
