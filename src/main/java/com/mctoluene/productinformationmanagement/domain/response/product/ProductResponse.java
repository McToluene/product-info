package com.mctoluene.productinformationmanagement.domain.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.model.Product;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProductResponse {

    private UUID publicId;
    private String productName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String status;
    private BigInteger version;
    private UUID manufacturerPublicId;
    private UUID brandPublicId;

    public ProductResponse(Product product) {
        this(product.getPublicId(), product.getProductName(), product.getProductDescription(), product.getCreatedDate(),
                product.getLastModifiedDate(),
                product.getCreatedBy(), product.getLastModifiedBy(), product.getStatus(), product.getVersion(),
                product.getManufacturer().getPublicId(), product.getBrand().getPublicId());
    }
}
