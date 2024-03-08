package com.mctoluene.productinformationmanagement.domain.response.productVariant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProductVariantResponseDto {
    private UUID publicId;
    private String name;
    private String sku;
    private String categoryName;
    private Double price;
    private Integer moq;
    private UUID statePublicId;
    private List<String> imageUrls;
    private Integer maxOrderQuantity;
    private Integer quantity;
}
