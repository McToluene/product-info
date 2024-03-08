package com.mctoluene.productinformationmanagement.domain.request.notification;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationPayload {
    private Map<String, String> additionalParameters = new HashMap<>();
}
