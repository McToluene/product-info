package com.mctoluene.productinformationmanagement.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record ProductSkuDto(UUID publicId, String sku, boolean exists) {

}
