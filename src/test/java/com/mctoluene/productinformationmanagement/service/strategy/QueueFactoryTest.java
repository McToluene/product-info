package com.mctoluene.productinformationmanagement.service.strategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.service.strategy.BrandMessageQueueConverter;

import javax.jms.JMSException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class QueueFactoryTest {

    static MessageContentEvent contentEvent;
    @Mock
    private BrandMessageQueueConverter brandMessageQueueConverter;

    @BeforeAll
    public static void init() {
        contentEvent = MessageContentEvent.builder()
                .typeMessage(TypeMessage.BRAND)
                .data(new Object())
                .build();
    }

    @Test
    void whenContructorInvoked_ThenRecognizeInterfaceStrategy() {
        try (MockedConstruction<BrandMessageQueueConverter> mockQueueStrategyService = Mockito.mockConstruction(
                BrandMessageQueueConverter.class, (mock, context) -> {
                    doNothing().when(mock).sendNotification(contentEvent);
                })) {
        }
    }

    @Test
    void whenGetNotificationServiceInvoked_ThenRecognizeInterfaceStrategy() {
        Map mockAvailableActions = mock(Map.class);
        when(mockAvailableActions.get("brand-message")).thenReturn(brandMessageQueueConverter);
    }

    @Test
    void whenGetNotificationServiceInvoked_ThenFailInterfaceStrategy() {
        Map mockAvailableActions = mock(Map.class);
        when(mockAvailableActions.get(null)).thenThrow(new RuntimeException("Unsupported notification type"));
        assertThrows(RuntimeException.class, () -> mockAvailableActions.get(null));
    }

    @Test
    void whenExecuteServiceInvoked_ThenRecognizeInterfaceStrategy() throws JMSException {
        doNothing().when(brandMessageQueueConverter).sendNotification(contentEvent);
    }
}
