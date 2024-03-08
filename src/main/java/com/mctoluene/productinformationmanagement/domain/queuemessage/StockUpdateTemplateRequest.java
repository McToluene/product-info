package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.mctoluene.productinformationmanagement.util.PoiCell;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateTemplateRequest implements Serializable {

    @PoiCell(name = "Business Name")
    private String businessName;

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

    @PoiCell(name = "quantity to upload")
    private BigInteger quantityToUpload;

    @PoiCell(name = "Listing Price")
    private BigDecimal listingPrice;

    @PoiCell(name = "Cost Price")
    private BigDecimal costPrice;

    @PoiCell(name = "Weight")
    private Double weight;

}
