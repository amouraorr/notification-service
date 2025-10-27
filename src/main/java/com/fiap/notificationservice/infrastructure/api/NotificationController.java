package com.fiap.notificationservice.infrastructure.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/health")
public class NotificationController {

    @GetMapping("/api/health")
    public String health() { return "OK - RODANDO";

    }

}