package com.mctoluene.productinformationmanagement.event;

import com.mctoluene.productinformationmanagement.TestHelpers.EntityHelpers;
import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.event.EventPublisher;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ExtendWith(SpringExtension.class)
public class EventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EventPublisher eventPublisher;

    @Test
    void publishProductVariantApprovedEventTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ProductVariant productVariant = EntityHelpers.buildProductVariant();
        ProductRequestDto productRequestDto = ProductRequestDto.builder().build();
        Method method = EventPublisher.class.getDeclaredMethod("publishProductVariantApprovedEvent",
                ProductVariant.class, ProductRequestDto.class);
        method.setAccessible(true);
        method.invoke(eventPublisher, productVariant, productRequestDto);

    }

}
