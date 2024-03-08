package com.mctoluene.productinformationmanagement.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.event.listener.ProductServiceEventListener;
import com.mctoluene.productinformationmanagement.model.ProductVariant;
import com.mctoluene.productinformationmanagement.service.internal.ImageCatalogInternalService;
import com.mctoluene.productinformationmanagement.service.internal.ShoppingExperienceClientInternalService;
import com.mctoluene.productinformationmanagement.service.internal.StockOneProductInternalService;
import com.mctoluene.productinformationmanagement.service.internal.VariantVersionInternalService;
import com.mctoluene.commons.response.AppResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductServiceEventListenerTest {

    @Mock
    private ImageCatalogInternalService imageCatalogInternalService;

    @Mock
    private VariantVersionInternalService variantVersionInternalService;

    @Mock
    private ShoppingExperienceClientInternalService shoppingExperienceClientInternalService;

    @Mock
    private StockOneProductInternalService stockOneProductInternalService;

    @InjectMocks
    private ProductServiceEventListener productServiceEventListener;

    @Test
    void handleProductVariantApprovedEventTest() {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        ProductRequestDto productRequestDto = ProductRequestDto.builder().build();
        ProductVariantApprovedEvent event = new ProductVariantApprovedEvent(productVariant, productRequestDto);

        AppResponse appResponse = new AppResponse(HttpStatus.OK.value(), "test", "test", null, null);
        when(stockOneProductInternalService.createProduct(any()))
                .thenReturn(ResponseEntity.ok(appResponse));
        when(shoppingExperienceClientInternalService.createAlgoliaRequest(anyList()))
                .thenReturn(ResponseEntity.ok(appResponse));
        when(variantVersionInternalService.findByProductVariantId(any()))
                .thenReturn(EntityHelpers.buildVariantVersion());

        productServiceEventListener.handleProductVariantApprovedEvent(event);
    }

    @Test
    void handleProductVariantApprovedEventExceptionTest() throws Exception {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        ProductRequestDto productRequestDto = ProductRequestDto.builder().build();
        ProductVariantApprovedEvent event = new ProductVariantApprovedEvent(productVariant, productRequestDto);

        AppResponse appResponse = new AppResponse(HttpStatus.BAD_REQUEST.value(),
                "test", "test",
                null, "remote-server-not-responding");

        String error = mapToJson(appResponse);

        when(stockOneProductInternalService.createProduct(any()))
                .thenThrow(new RuntimeException(error));
        when(shoppingExperienceClientInternalService.createAlgoliaRequest(anyList()))
                .thenReturn(ResponseEntity.ok(appResponse));
        when(variantVersionInternalService.findByProductVariantId(any()))
                .thenReturn(EntityHelpers.buildVariantVersion());

        productServiceEventListener.handleProductVariantApprovedEvent(event);
    }

    private static String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
