package com.mctoluene.productinformationmanagement.service.internal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.mctoluene.productinformationmanagement.configuration.FeignClientConfig;
import com.mctoluene.productinformationmanagement.domain.request.notification.NotificationDto;
import com.mctoluene.commons.response.AppResponse;

@FeignClient(name = "notification-client", configuration = FeignClientConfig.class, url = "${notification.service.url}")
public interface NotificationInternalService {

    @GetMapping("/orchestration/send-notification")
    ResponseEntity<AppResponse<Void>> send(NotificationDto data);
}
