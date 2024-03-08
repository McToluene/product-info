package com.mctoluene.productinformationmanagement.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadQueueDto;
import com.mctoluene.productinformationmanagement.domain.request.image.ImageUploadRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImagePublisherService {

    @Value("${service.bus.connection-url}")
    private String connectionUrl;

    @Value("${service.bus.queue.name}")
    private String queueName;

    private final Gson gson;

    public void sendMessageToQueue(List<ImageUploadRequestDto> imageUploadRequestDto, String uploadedBy) {

        String message = gson.toJson(ImageUploadQueueDto.builder()
                .imageUploadRequestDtos(imageUploadRequestDto)
                .uploadedBy(uploadedBy)
                .build());

        try (ServiceBusSenderClient serviceBusSenderClient = new ServiceBusClientBuilder()
                .connectionString(connectionUrl)
                .sender()
                .queueName(queueName).buildClient()) {

            serviceBusSenderClient.sendMessage(new ServiceBusMessage(message));

            log.info("Message sent successfully to the queue {} ", imageUploadRequestDto);
        }
    }

}
