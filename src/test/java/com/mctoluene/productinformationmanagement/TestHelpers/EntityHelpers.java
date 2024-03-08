package com.mctoluene.productinformationmanagement.TestHelpers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mctoluene.productinformationmanagement.domain.enums.ApprovalStatus;
import com.mctoluene.productinformationmanagement.domain.enums.Status;
import com.mctoluene.productinformationmanagement.domain.request.imagecatalog.UpdateImageCatalogRequestDto;
import com.mctoluene.productinformationmanagement.domain.request.variant.UpdateVariantRequestDto;
import com.mctoluene.productinformationmanagement.model.*;

public class EntityHelpers {

    public static UpdateImageCatalogRequestDto buildUpdateImageCatalogRequestDto() {
        return UpdateImageCatalogRequestDto.builder()
                .imageName("test")
                .imageDescription("desc")
                .imageUrl("url")
                .imageCatalogPublicId(UUID.randomUUID())
                .modifiedBy("mod")
                .build();
    }

    public static ProductVariant buildProductVariant(UUID originalPublicId, Status status) {
        ProductVariant productVariant = ProductVariant.builder()
                .originalPublicId(originalPublicId)
                .status(status.name())
                .product(Product.builder()
                        .productName("test")
                        .build())
                .build();
        productVariant.setPublicId(originalPublicId);
        productVariant.setId(originalPublicId);
        return productVariant;
    }

    public static VariantType buildVariantType() {
        VariantType variantType = VariantType.builder()
                .variantTypeName("variantType")
                .status(Status.ACTIVE.name())
                .build();
        variantType.setPublicId(UUID.randomUUID());
        variantType.setId(UUID.randomUUID());
        return variantType;
    }

    public static UpdateVariantRequestDto buildUpdateVariantRequestDto() {
        UpdateVariantRequestDto requestDto = new UpdateVariantRequestDto();
        requestDto.setVariantTypePublicId(UUID.randomUUID());
        requestDto.setVariantName("Variant");
        requestDto.setVariantDescription("Desc");
        requestDto.setImageCatalogs(List.of(UpdateImageCatalogRequestDto.builder()
                .imageName("test")
                .imageDescription("desc")
                .imageUrl("url")
                .imageCatalogPublicId(UUID.randomUUID())
                .modifiedBy("mod")
                .build()));
        requestDto.setCostPrice(BigDecimal.ONE);
        requestDto.setModifiedBy("Mod");
        return requestDto;
    }

    public static Product buildProduct() {
        Brand brand = buildBrand();
        ProductCategory productCategory = buildProductCategory();
        Manufacturer manufacturer = buildManufacturer();
        brand.setManufacturer(manufacturer);
        MeasuringUnit measuringUnit = buildMeasuringUnit();
        WarrantyType warrantyType = buildWarrantyType();

        Product product = Product.builder()
                .productName("name")
                .vated(false)
                .status(Status.ACTIVE.name())
                .productCategory(productCategory)
                .brand(brand)
                .productDescription("desc")
                .productNotes("notes")
                .productHighlights("Highlights")
                .manufacturer(manufacturer)
                .measurementUnit(measuringUnit)
                .warrantyType(warrantyType)
                .warrantyDuration("duration")
                .warrantyAddress("address")
                .warrantyCover("cover")
                .productListing("listings")
                .build();
        product.setId(UUID.randomUUID());
        product.setPublicId(UUID.randomUUID());
        product.setVersion(BigInteger.ZERO);
        product.setCreatedBy("test");
        product.setCreatedDate(LocalDateTime.now());
        product.setLastModifiedBy("test");
        product.setLastModifiedDate(LocalDateTime.now());

        return product;
    }

    public static WarrantyType buildWarrantyType() {
        WarrantyType warrantyType = WarrantyType.builder()
                .warrantyTypeName("WarrantyTypeName")
                .status(Status.ACTIVE)
                .description("desc")
                .build();

        warrantyType.setId(UUID.randomUUID());
        warrantyType.setPublicId(UUID.randomUUID());
        warrantyType.setVersion(BigInteger.ZERO);
        warrantyType.setCreatedBy("test");
        warrantyType.setCreatedDate(LocalDateTime.now());
        warrantyType.setLastModifiedBy("test");
        warrantyType.setLastModifiedDate(LocalDateTime.now());

        return warrantyType;
    }

    public static Brand buildBrand() {
        Brand brand = Brand.builder()
                .brandName("Samsung")
                .description("Samsung")
                .status(Status.ACTIVE)
                .build();
        brand.setId(UUID.randomUUID());
        brand.setPublicId(UUID.randomUUID());
        brand.setVersion(BigInteger.ZERO);
        brand.setCreatedBy("test");
        brand.setCreatedDate(LocalDateTime.now());
        brand.setLastModifiedBy("test");
        brand.setLastModifiedDate(LocalDateTime.now());

        return brand;
    }

    public static ProductCategory buildProductCategory() {
        ProductCategory productCategory = ProductCategory.builder()
                .productCategoryName("test")
                .status(Status.ACTIVE)
                .depth(10)
                .imageUrl("url")
                .description("desc")
                .build();

        productCategory.setId(UUID.randomUUID());
        productCategory.setPublicId(UUID.randomUUID());
        productCategory.setVersion(BigInteger.ZERO);
        productCategory.setCreatedBy("test");
        productCategory.setCreatedDate(LocalDateTime.now());
        productCategory.setLastModifiedBy("test");
        productCategory.setLastModifiedDate(LocalDateTime.now());

        return productCategory;
    }

    public static MeasuringUnit buildMeasuringUnit() {
        MeasuringUnit measuringUnit = MeasuringUnit.builder()
                .name("Test")
                .abbreviation("Test")
                .description("Test")
                .status(Status.ACTIVE.name())
                .build();

        measuringUnit.setId(UUID.randomUUID());
        measuringUnit.setPublicId(UUID.randomUUID());
        measuringUnit.setVersion(BigInteger.ZERO);
        measuringUnit.setCreatedBy("test");
        measuringUnit.setCreatedDate(LocalDateTime.now());
        measuringUnit.setLastModifiedBy("test");
        measuringUnit.setLastModifiedDate(LocalDateTime.now());

        return measuringUnit;
    }

    public static Manufacturer buildManufacturer() {
        Manufacturer manufacturer = Manufacturer.builder()
                .manufacturerName("manufacturer")
                .description("desc")
                .status(Status.ACTIVE)
                .build();

        manufacturer.setId(UUID.randomUUID());
        manufacturer.setPublicId(UUID.randomUUID());
        manufacturer.setVersion(BigInteger.ZERO);
        manufacturer.setCreatedBy("test");
        manufacturer.setCreatedDate(LocalDateTime.now());
        manufacturer.setLastModifiedBy("test");
        manufacturer.setLastModifiedDate(LocalDateTime.now());

        return manufacturer;
    }

    public static ProductVariant buildProductVariant() {
        Product product = buildProduct();
        ProductVariant productVariant = new ProductVariant();
        productVariant.setStatus(Status.ACTIVE.name());
        productVariant.setProduct(product);
        productVariant.setOriginalPublicId(UUID.randomUUID());
        productVariant.setApprovedBy("test");
        productVariant.setApprovedDate(LocalDateTime.now());
        productVariant.setId(UUID.randomUUID());
        productVariant.setPublicId(UUID.randomUUID());
        productVariant.setVersion(BigInteger.ZERO);
        productVariant.setCreatedBy("test");
        productVariant.setCreatedDate(LocalDateTime.now());
        productVariant.setLastModifiedBy("test");
        productVariant.setLastModifiedDate(LocalDateTime.now());

        return productVariant;
    }

    public static VariantVersion buildVariantVersion() {
        return VariantVersion.builder()
                .id(UUID.randomUUID())
                .product(buildProduct())
                .productVariant(buildProductVariant())
                .sku("testsku")
                .variantType(buildVariantType())
                .variantName("name")
                .variantDescription("desc")
                .costPrice(BigDecimal.valueOf(100))
                .defaultImageUrl("url")
                .threshold(100)
                .leadTime(100)
                .status(Status.ACTIVE.name())
                .approvalStatus(ApprovalStatus.APPROVED.name())
                .version(BigInteger.ZERO)
                .approvedDate(LocalDateTime.now())
                .approvedBy("test")
                .build();
    }

    public static VariantAwaitingApproval buildVariantAwaitingApproval() {
        VariantAwaitingApproval variantAwaitingApproval = VariantAwaitingApproval.builder()
                .variantType(buildVariantType())
                .product(buildProduct())
                .variantName("variant name")
                .variantDescription("desc")
                .sku("testSku")
                .costPrice(BigDecimal.valueOf(100))
                .defaultImageUrl("url")
                .leadTime(100)
                .threshold(100)
                .status(Status.ACTIVE.name())
                .productVariant(buildProductVariant())
                .approvalStatus(ApprovalStatus.APPROVED.name())
                .completedDate(LocalDateTime.now())
                .completedBy("test")
                .rejectedReason("reason")
                .productVariantDetails("details")
                .build();

        variantAwaitingApproval.setId(UUID.randomUUID());
        variantAwaitingApproval.setPublicId(UUID.randomUUID());
        variantAwaitingApproval.setVersion(BigInteger.ZERO);
        variantAwaitingApproval.setCreatedBy("test");
        variantAwaitingApproval.setCreatedDate(LocalDateTime.now());
        variantAwaitingApproval.setLastModifiedBy("test");
        variantAwaitingApproval.setLastModifiedDate(LocalDateTime.now());

        return variantAwaitingApproval;
    }

    public static List<ImageCatalog> getImageCatalogList() {
        List<ImageCatalog> result = new ArrayList<>();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(new ProductVariant())
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(new VariantAwaitingApproval()).build();
        result.add(imageCatalog);

        return result;
    }

    public static List<ImageCatalog> getImageCatalogList(ProductVariant productVariant,
            VariantAwaitingApproval variantAwaitingApproval) {
        List<ImageCatalog> result = new ArrayList<>();
        ImageCatalog imageCatalog = ImageCatalog.builder()
                .status(Status.ACTIVE.name())
                .imageDescription("image-002")
                .imageUrl("http:mctoluene-img-0223.com")
                .productVariant(productVariant)
                .imageName("mctoluene-0033")
                .variantAwaitingApproval(variantAwaitingApproval)
                .build();
        result.add(imageCatalog);

        return result;
    }

}
