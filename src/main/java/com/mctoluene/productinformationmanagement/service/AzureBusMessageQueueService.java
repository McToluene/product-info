package com.mctoluene.productinformationmanagement.service;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.service.strategy.QueueFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.message.JmsTextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureBusMessageQueueService {

    @Value("${azure.bus.queue.name}")
    private String queueName;

    private final JmsTemplate jmsTemplate;

    private final QueueFactory queueFactory;

    private final MessageSourceService messageSourceService;

    public void sendMessage(MessageContentEvent message) {
        try {
            log.info("sending message to the queue {}", message, message.getTypeMessage());
            jmsTemplate.convertAndSend(queueName, message);
        } catch (RuntimeException ee) {
            log.error("Invalid message from jsmtemplate ");
            throw new ValidatorException(messageSourceService.getMessageByKey("cannot.send.queue.message"));

        }
    }

    @JmsListener(destination = "${azure.bus.queue.name}")
    public void readMessageFromQueue(Message queueMessage) throws JMSException {
        Gson gson = new Gson();
        if (queueMessage instanceof JmsTextMessage bytesMessage) {
            String message = bytesMessage.getBody(String.class);
            MessageContentEvent content = gson.fromJson(message, MessageContentEvent.class);
            queueFactory.execute(content.getTypeMessage(), content);
        }
    }

}
