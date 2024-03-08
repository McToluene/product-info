package com.mctoluene.productinformationmanagement.service.strategy;

import org.springframework.stereotype.Component;

import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;

import javax.jms.JMSException;
import java.util.Map;

@Component
public class QueueFactory {
    private final Map<String, QueueStrategyInterface> queueStrategyInterface;

    public QueueFactory(Map<String, QueueStrategyInterface> notificationServices) {
        this.queueStrategyInterface = notificationServices;
    }

    public QueueStrategyInterface getNotificationService(String notificationType) {
        QueueStrategyInterface notificationService = queueStrategyInterface.get(notificationType);
        if (notificationService == null) {
            throw new RuntimeException("Unsupported notification type");
        }
        return notificationService;
    }

    public void execute(String notificationType, MessageContentEvent content) throws JMSException {
        QueueStrategyInterface queueStrategyInterface = getNotificationService(notificationType);
        queueStrategyInterface.sendNotification(content);
    }
}
