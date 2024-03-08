package com.mctoluene.productinformationmanagement.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.BulkProductUploadRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
@Slf4j
public class BulkUploadMessageConverter extends MappingJackson2MessageConverter {

    ObjectMapper mapper;

    public BulkUploadMessageConverter() {
        mapper = new ObjectMapper();
    }

    @Override
    public Message toMessage(Object object, Session session)
            throws JMSException {
        MessageContentEvent messageContentEvent = (MessageContentEvent) object;
        String payload = null;
        try {
            payload = mapper.writeValueAsString(messageContentEvent);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        TextMessage message = session.createTextMessage();
        message.setText(payload);

        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        String payload = textMessage.getText();

        MessageContentEvent messageContentEvent = null;
        try {
            messageContentEvent = mapper.readValue(payload, MessageContentEvent.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return messageContentEvent;
    }
}
