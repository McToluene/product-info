package com.mctoluene.productinformationmanagement.domain.request.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDto {
    private String source;
    private NotificationPayload payload;
}
