package com.bidnbuy.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketChatHandler {
    private final WebSocketHandler webSocketHandler;
}
