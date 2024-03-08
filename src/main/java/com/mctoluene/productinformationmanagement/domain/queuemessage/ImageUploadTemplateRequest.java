package com.mctoluene.productinformationmanagement.domain.queuemessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

import com.mctoluene.productinformationmanagement.util.PoiCell;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadTemplateRequest implements Serializable {

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

    @PoiCell(name = "Weight")
    private Double weight;

    @PoiCell(name = "Cost Price")
    private BigDecimal costPrice;

    @PoiCell(name = "Listing Price")
    private BigDecimal listingPrice;

    @PoiCell(name = "Image URL 1")
    private String imageUrl1;

    @PoiCell(name = "Image URL 2")
    private String imageUrl2;

    @PoiCell(name = "Measurement Unit")
    private String measurementUnit;

    @PoiCell(name = "vated")
    private boolean vated;

    @PoiCell(name = "Vat Value")
    private BigDecimal vatValue;

    @PoiCell(name = "Product Min Vat")
    private BigDecimal minVat;

    @PoiCell(name = "Product Max Vat")
    private BigDecimal maxVat;

}
