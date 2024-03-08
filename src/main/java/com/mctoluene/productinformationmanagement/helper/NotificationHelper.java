package com.mctoluene.productinformationmanagement.helper;

import com.mctoluene.productinformationmanagement.domain.request.notification.NotificationDto;
import com.mctoluene.productinformationmanagement.domain.request.notification.NotificationPayload;

public class NotificationHelper {
    private NotificationHelper() {
    }

    public static NotificationDto buildNotificationRequest(String createdBy) {
        NotificationPayload payload = new NotificationPayload();

        payload.getAdditionalParameters().put("status", "Image upload failure");
        payload.getAdditionalParameters().put("name", "Image upload failure");
        payload.getAdditionalParameters().put("countryCode", "NG");
        payload.getAdditionalParameters().put("email", createdBy);
        payload.getAdditionalParameters().put("fileName", "Image upload");
        payload.getAdditionalParameters().put("subject", "export");
        return NotificationDto.builder()
                .source("Image")
                .payload(payload)
                .build();
    }
}
