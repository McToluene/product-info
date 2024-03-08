package com.mctoluene.productinformationmanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.enums.TypeMessage;
import com.mctoluene.productinformationmanagement.domain.eventDto.MessageContentEvent;
import com.mctoluene.productinformationmanagement.domain.queuemessage.*;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.model.Brand;
import com.mctoluene.productinformationmanagement.service.AzureBusMessageQueueService;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.productinformationmanagement.service.ProductService;

import org.apache.qpid.jms.message.JmsTextMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.jms.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AzureBusMessageQueueServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private ProductService productService;
    @Mock
    private MessageSourceService messageSourceService;
    @Mock
    private Gson gson;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private AzureBusMessageQueueService azureBusMessageQueueService;
    AutoCloseable closeable;
    static String queue;
    static BulkProductUploadRequest bulkProductUploadRequest;

    static MessageContentEvent contentEvent;
    static BrandUploadRequest brandImageCatalogUploadRequest;

    @BeforeAll
    public static void init() {
        bulkProductUploadRequest = BulkProductUploadRequest.builder()
                .categoryUploadTemplateRequests(null)
                .imageUploadTemplateRequests(null)
                .priceTemplateRequests(null)
                .stockUpdateTemplateRequests(null)
                .createdBy("Jhon Doe")
                .build();

        queue = "bulk-product-uploads";

        brandImageCatalogUploadRequest = BrandUploadRequest
                .builder()
                .brandName("Mock brand name")
                .description("Mock description")
                .build();

        contentEvent = MessageContentEvent.builder()
                .typeMessage(TypeMessage.BRAND)
                .data(brandImageCatalogUploadRequest)
                .build();
    }

    @Test
    void sendMessage() throws JsonProcessingException, JMSException {
        TextMessage mockTextMessage = Mockito.mock(TextMessage.class);
        mockTextMessage.setText("Mocked message test");
        BulkProductUploadRequest mockRequest = new BulkProductUploadRequest();
        azureBusMessageQueueService.sendMessage(contentEvent);
        jmsTemplate.convertAndSend(queue, mockTextMessage);
    }

    @Test
    void sendMessageException() {
        azureBusMessageQueueService.sendMessage(contentEvent);
        doThrow(RuntimeException.class).when(jmsTemplate).convertAndSend(queue, bulkProductUploadRequest);
    }

    @Test
    void readMessageFromQueueTest() throws Exception {
        TextMessage mockTextMessage = Mockito.mock(TextMessage.class);
        Mockito.when(mockTextMessage.getBody(String.class)).thenReturn("yourSerializedMessage");
        Mockito.when(gson.fromJson("yourSerializedMessage", MessageContentEvent.class)).thenReturn(contentEvent);
        azureBusMessageQueueService.readMessageFromQueue(mockTextMessage);
    }

    private static String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
