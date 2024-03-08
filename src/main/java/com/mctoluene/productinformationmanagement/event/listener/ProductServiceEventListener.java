package com.mctoluene.productinformationmanagement.event.listener;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.request.shoppingexperience.ShoppingExperienceCreateProductRequest;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.helper.UtilsHelper;
import com.mctoluene.productinformationmanagement.model.ImageCatalog;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.model.VariantVersion;
import com.mctoluene.productinformationmanagement.service.internal.ImageCatalogInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ShoppingExperienceClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.StockOneProductInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantVersionInternalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductServiceEventListener {

    private final ImageCatalogInternalService imageCatalogInternalService;

    private final VariantVersionInternalService variantVersionInternalService;

    private final ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    private final StockOneProductInternalService stockOneProductInternalService;

    @Async
    @EventListener
    public void handleProductVariantApprovedEvent(ProductVariantApprovedEvent event) {
        createProductOnStockOne(event.productRequestDto());
        createRequestForAlgolia(event.productVariant());

    }

    private void createProductOnStockOne(ProductRequestDto productRequestDto) {
        log.info("logging request for product creation on stockone {}", productRequestDto);
        try {
            log.info("Request Dto {}", new Gson().toJson(productRequestDto));
            stockOneProductInternalService.createProduct(productRequestDto);
        } catch (Exception ex) {
            log.info("From stockOne service -> {}", ex.getMessage());
            String parsedMessage = UtilsHelper.parseErrorString(ex.getMessage());
            log.info("Error saving product on stock-one.. {}", parsedMessage);
        }
    }

    private void createRequestForAlgolia(ProductVariant productVariant) {

        log.info("Variants to be indexed on algolia -> {}", productVariant);

        List<ShoppingExperienceCreateProductRequest> shoppingExperienceRequest = new ArrayList<>();

        List<ImageCatalog> imageCatalogs = imageCatalogInternalService.findByProductVariantId(productVariant.getId());
        List<String> imageUrls = imageCatalogs.stream()
                .map(ImageCatalog::getImageUrl)
                .toList();

        shoppingExperienceRequest.add(buildShoppingExperienceRequest(productVariant, imageUrls));

        log.info("Request to create products on algolia via shopping experience service --> {} ",
                shoppingExperienceRequest);

        shoppingExperienceClientInternalService.createAlgoliaRequest(shoppingExperienceRequest);
    }

    private ShoppingExperienceCreateProductRequest buildShoppingExperienceRequest(ProductVariant productVariant,
            List<String> imageUrls) {
        VariantVersion newestVariantVersion = variantVersionInternalService
                .findByProductVariantId(productVariant.getId());
        String categoryName = newestVariantVersion.getProduct().getProductCategory().getProductCategoryName();

        return ShoppingExperienceCreateProductRequest.builder()
                .categoryName(categoryName)
                .name(newestVariantVersion.getVariantName())
                .publicId(productVariant.getPublicId())
                .sku(newestVariantVersion.getSku())
                .imageUrls(imageUrls)
                .build();
    }
}
