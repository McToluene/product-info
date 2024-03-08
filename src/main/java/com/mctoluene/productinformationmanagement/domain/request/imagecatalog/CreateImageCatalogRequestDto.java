package com.mctoluene.productinformationmanagement.domain.request.imagecatalog;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class CreateImageCatalogRequestDto {

    @NotNull(message = "product id cannot be empty")
    private UUID productVariantPublicId;

    @NotNull(message = "add at least one image")
    private List<ImageCatalogRequestDto> imageCatalogs;
}
