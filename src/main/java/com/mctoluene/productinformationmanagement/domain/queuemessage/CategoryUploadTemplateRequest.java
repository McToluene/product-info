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
public class CategoryUploadTemplateRequest implements Serializable {

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

    @PoiCell(name = "Listing Price")
    private BigDecimal listingPrice;

    @PoiCell(name = "Cost Price")
    private BigDecimal costPrice;

    @PoiCell(name = "Weight")
    private Double weight;

    @PoiCell(name = "Measurement Unit")
    private String measurementUnit;

    @PoiCell(name = "Packaging Type")
    private String packagingType;

    @PoiCell(name = "MOQ 1")
    private Long moq1;

    @PoiCell(name = "MOQ 1 Price")
    private BigDecimal moq1Price;

    @PoiCell(name = "MOQ 2")
    private Long moq2;

    @PoiCell(name = "MOQ 2 Price")
    private BigDecimal moq2Price;

    @PoiCell(name = "Packaging")
    private String packaging;

    @PoiCell(name = "Age Group")
    private String ageGroup;

    @PoiCell(name = "Gender")
    private String gender;

    @PoiCell(name = "Size")
    private String size;

    @PoiCell(name = "Colour")
    private String colour;

    @PoiCell(name = "Model")
    private String model;

    @PoiCell(name = "Power Capacity")
    private String powerCapacity;

    @PoiCell(name = "Fuel Type")
    private String fuelType;

    @PoiCell(name = "Operating System")
    private String operatingSystem;

    @PoiCell(name = "Screen Size")
    private String screenSize;

    @PoiCell(name = "Number of SIMs")
    private Integer numberOfSims;

    @PoiCell(name = "Internal Storage")
    private String internalStorage;

    @PoiCell(name = "RAM")
    private String ram;

    @PoiCell(name = "Battery Capacity")
    private String batteryCapacity;

    @PoiCell(name = "Material")
    private String material;

    @PoiCell(name = "Dimension")
    private String dimension;

    @PoiCell(name = "Power")
    private String power;

    @PoiCell(name = "Capacity")
    private String capacity;

    @PoiCell(name = "Device Model Year")
    private String deviceModelSize;

    @PoiCell(name = "Display Size")
    private String displaySize;

    @PoiCell(name = "Display Technology")
    private String displayTechnology;

    @PoiCell(name = "Processor Type")
    private String processorType;

    @PoiCell(name = "Storage")
    private String storage;

    @PoiCell(name = "Memory")
    private String memory;

    @PoiCell(name = "Battery")
    private String battery;

    @PoiCell(name = "Image URL 1")
    private String imageUrl1;

    @PoiCell(name = "Image URL 2")
    private String imageUrl2;

}
