package com.mctoluene.productinformationmanagement.domain.response.productVariant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.response.product.ProductResponseDto;
import com.mctoluene.productinformationmanagement.helper.ProductHelper;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductVariantResponse {
    private UUID publicId;
    private String sku;
    private String variantName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;
    private String variantType;
    private Double weight;
    private ProductResponseDto product;

    public ProductVariantResponse(ProductVariant productVariant) {
        this(productVariant.getPublicId(), productVariant.getSku(), productVariant.getVariantName(),
                productVariant.getVariantDescription(),
                productVariant.getCreatedDate(), productVariant.getLastModifiedDate(),
                productVariant.getCreatedBy(), productVariant.getLastModifiedBy(), productVariant.getStatus(),
                productVariant.getVersion(),
                productVariant.getVariantType().getVariantTypeName(), productVariant.getWeight(),
                ProductHelper.buildProductResponse(productVariant.getProduct()));
    }

}
