package com.mctoluene.productinformationmanagement.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.queuemessage.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

@Slf4j
public class TemplateHelper {

    private TemplateHelper() {
    }

    public static ProductVariantDto buildProductVariantDtoUsingImageTemplateRequest(
            ImageUploadTemplateRequest imageTemplateRequest, String createdBy, ObjectMapper mapper) {

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto
                .setManufacturerName(WordUtils.capitalizeFully(imageTemplateRequest.getManufacturerName().trim()));
        productVariantDto.setBrandName(WordUtils.capitalizeFully(imageTemplateRequest.getBrand().trim()));
        productVariantDto.setProductName(WordUtils.capitalizeFully(imageTemplateRequest.getProductName().trim()));
        productVariantDto.setCreatedBy(createdBy.trim());
        productVariantDto.setWeight(imageTemplateRequest.getWeight());
        productVariantDto.setCostPrice(imageTemplateRequest.getCostPrice());
        productVariantDto
                .setProductCategoryName(WordUtils.capitalizeFully(imageTemplateRequest.getProductCategory().trim()));
        productVariantDto.setVariantTypeName(WordUtils.capitalizeFully(imageTemplateRequest.getVariantType().trim()));
        productVariantDto.setVariantName(WordUtils.capitalizeFully(imageTemplateRequest.getVariantName().trim()));

        try {
            String productVariantDetails = mapper.writeValueAsString(imageTemplateRequest);
            productVariantDto.setProductVariantDetails(productVariantDetails);
        } catch (Exception ex) {
            log.error("Exception while serializing ImageTemplateRequest Object: ", ex);
        }
        return productVariantDto;
    }

    public static ProductVariantDto buildProductVariantDtoUsingPriceTemplateRequest(
            PriceTemplateRequest priceTemplateRequest, String createdBy, ObjectMapper mapper) {

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto
                .setManufacturerName(WordUtils.capitalizeFully(priceTemplateRequest.getManufacturerName().trim()));
        productVariantDto.setBrandName(WordUtils.capitalizeFully(priceTemplateRequest.getBrand().trim()));
        productVariantDto.setProductName(WordUtils.capitalizeFully(priceTemplateRequest.getProductName().trim()));
        productVariantDto.setCreatedBy(createdBy);
        productVariantDto.setCostPrice(priceTemplateRequest.getCostPrice());
        productVariantDto
                .setProductCategoryName(WordUtils.capitalizeFully(priceTemplateRequest.getProductCategory().trim()));
        productVariantDto.setVariantTypeName(priceTemplateRequest.getVariantType());
        productVariantDto.setVariantName(WordUtils.capitalizeFully(priceTemplateRequest.getVariantName()));

        try {
            String productVariantDetails = mapper.writeValueAsString(priceTemplateRequest);
            productVariantDto.setProductVariantDetails(productVariantDetails);
        } catch (Exception ex) {
            log.error("Exception while serializing priceTemplateRequest Object: ", ex);
        }
        return productVariantDto;
    }

    public static ProductVariantDto buildProductVariantDtoUsingStockUpdateTemplateRequest(
            StockUpdateTemplateRequest stockUpdateTemplateRequest, String createdBy, ObjectMapper mapper) {

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setManufacturerName(
                WordUtils.capitalizeFully(stockUpdateTemplateRequest.getManufacturerName().trim()));
        productVariantDto.setBrandName(WordUtils.capitalizeFully(stockUpdateTemplateRequest.getBrand().trim()));
        productVariantDto.setProductName(WordUtils.capitalizeFully(stockUpdateTemplateRequest.getProductName().trim()));
        productVariantDto.setCreatedBy(createdBy);
        productVariantDto.setCostPrice(stockUpdateTemplateRequest.getCostPrice());
        productVariantDto.setProductCategoryName(
                WordUtils.capitalizeFully(stockUpdateTemplateRequest.getProductCategory().trim()));
        productVariantDto.setVariantTypeName(stockUpdateTemplateRequest.getVariantType().trim());
        productVariantDto.setVariantName(WordUtils.capitalizeFully(stockUpdateTemplateRequest.getVariantName().trim()));

        try {
            String productVariantDetails = mapper.writeValueAsString(stockUpdateTemplateRequest);
            productVariantDto.setProductVariantDetails(productVariantDetails);
        } catch (Exception ex) {
            log.error("Exception while serializing stockUpdateTemplateRequest Object: ", ex);
        }
        return productVariantDto;
    }

    public static ProductVariantDto buildProductVariantDtoUsingCategoryUploadTemplateRequest(
            CategoryUploadTemplateRequest categoryUploadTemplateRequest, String createdBy, ObjectMapper mapper) {

        ProductVariantDto productVariantDto = new ProductVariantDto();
        productVariantDto.setManufacturerName(
                WordUtils.capitalizeFully(categoryUploadTemplateRequest.getManufacturerName().trim()));
        productVariantDto.setBrandName(WordUtils.capitalizeFully(categoryUploadTemplateRequest.getBrand().trim()));
        productVariantDto
                .setProductName(WordUtils.capitalizeFully(categoryUploadTemplateRequest.getProductName().trim()));
        productVariantDto.setCreatedBy(createdBy);
        productVariantDto.setCostPrice(categoryUploadTemplateRequest.getCostPrice());
        productVariantDto.setProductCategoryName(
                WordUtils.capitalizeFully(categoryUploadTemplateRequest.getProductCategory().trim()));
        productVariantDto.setVariantTypeName(categoryUploadTemplateRequest.getVariantType().trim());
        productVariantDto
                .setVariantName(WordUtils.capitalizeFully(categoryUploadTemplateRequest.getVariantName().trim()));

        try {
            String productVariantDetails = mapper.writeValueAsString(categoryUploadTemplateRequest);
            productVariantDto.setProductVariantDetails(productVariantDetails);
        } catch (Exception ex) {
            log.error("Exception while serializing categoryUploadTemplateRequest Object: ", ex);
        }
        return productVariantDto;
    }
}
