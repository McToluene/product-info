package com.mctoluene.productinformationmanagement.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.mctoluene.productinformationmanagement.domain.eventDto.ProductVariantApprovedEvent;
import com.mctoluene.productinformationmanagement.domain.stockone.ProductRequestDto;
import com.mctoluene.productinformationmanagement.model.ProductVariant;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private void publishProductVariantApprovedEvent(final ProductVariant productVariant,
            ProductRequestDto productRequestDto) {
        log.info("Publishing event for approved variant {} with request {}", productVariant, productRequestDto);
        ProductVariantApprovedEvent event = new ProductVariantApprovedEvent(productVariant, productRequestDto);
        applicationEventPublisher.publishEvent(event);
    }
}
