package com.mctoluene.productinformationmanagement.service;

import com.google.gson.Gson;
import com.mctoluene.productinformationmanagement.domain.queuemessage.ImageUploadQueueDto;
import com.mctoluene.productinformationmanagement.service.internal.ImageInternalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.Message;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageListenerService {

    private final Gson gson;

    private final ImageInternalService imageInternalService;

    @JmsListener(destination = "${service.bus.queue.name}")
    public void readMessage(Message queueMessage) {
        ImageUploadQueueDto imageUploadQueueDto = null;
        try {
            if (queueMessage instanceof BytesMessage bytesMessage) {
                byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(data);
                String message = new String(data, StandardCharsets.UTF_8);
                log.info("reading from queue: {}", message);
                imageUploadQueueDto = gson.fromJson(message, ImageUploadQueueDto.class);
                log.info("Original Image upload Request dto size {} ",
                        imageUploadQueueDto.getImageUploadRequestDtos().size());
                queueMessage.acknowledge();
            } else {
                log.warn("Received unexpected message type: {}", queueMessage);

            }
        } catch (Exception e) {
            log.error("Error occurred while processing message: {} ", e.getMessage(), e);
        }

        if (Objects.nonNull(imageUploadQueueDto) && !imageUploadQueueDto.getImageUploadRequestDtos().isEmpty()) {
            imageInternalService.processImages(imageUploadQueueDto.getImageUploadRequestDtos(),
                    imageUploadQueueDto.getUploadedBy());
        }
    }
}
