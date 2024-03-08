package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

import com.mctoluene.productinformationmanagement.util.PoiCell;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceTemplateRequest implements Serializable {

    @PoiCell(name = "Manufacturer Name")
    private String manufacturerName;

    @PoiCell(name = "Brand")
    private String brand;

    @PoiCell(name = "Product Category")
    private String productCategory;

    @PoiCell(name = "Sub-category")
    private String subCategory;

    @PoiCell(name = "Product Name")
    private String productName;

    @PoiCell(name = "Variant Type")
    private String variantType;

    @PoiCell(name = "Variant Name")
    private String variantName;

    @PoiCell(name = "MOQ 1")
    private Long moq1;

    @PoiCell(name = "MOQ 1 Price")
    private BigDecimal moq1Price;

    @PoiCell(name = "MOQ 2")
    private Long moq2;

    @PoiCell(name = "MOQ 2 Price")
    private BigDecimal moq2Price;

    @PoiCell(name = "Listing Price")
    private BigDecimal listingPrice;

    @PoiCell(name = "Cost Price")
    private BigDecimal costPrice;

    @PoiCell(name = "Weight")
    private Double weight;

    @PoiCell(name = "Material")
    private String material;

    @PoiCell(name = "Colour")
    private String color;

    @PoiCell(name = "Dimension")
    private String dimension;
}
