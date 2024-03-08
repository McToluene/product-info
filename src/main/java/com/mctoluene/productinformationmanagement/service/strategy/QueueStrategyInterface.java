package com.mctoluene.productinformationmanagement.service.strategy;

import javax.jms.JMSException;
import javax.jms.Message;

import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;

public interface QueueStrategyInterface {
    public void sendNotification(MessageContentEvent content);
}
