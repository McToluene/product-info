package com.mctoluene.productinformationmanagement.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceModelResponseDto {

    private UUID publicId;

    private UUID statePublicId;

    private String productSku;

    private BigDecimal markup;

    private UUID configurationPublicId;

    private BigDecimal manualSellingPrice;

    private BigDecimal finalSellingPrice;

    private BigInteger minimumQuantity;

    private BigInteger maximumQuantity;

    private String status;

    private List<VolumePricing> volumePricing;

    private BigInteger version;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class VolumePricing {

    private BigInteger quantity;

    private BigDecimal amount;

}
