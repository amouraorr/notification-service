package com.fiap.notificationservice.adapter.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("notificationControllerWeb")
@RequestMapping("/api/notification/web")
public class NotificationController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("notification-ok");
    }
}