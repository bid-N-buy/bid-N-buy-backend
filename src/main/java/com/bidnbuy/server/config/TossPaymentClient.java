package com.bidnbuy.server.config;

import com.bidnbuy.server.dto.PaymentRequestDTO;
import com.bidnbuy.server.dto.PaymentResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class TossPaymentClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secretKey = "test_sk_Gv6LjeKD8aE4pK1PvEwk8wYxAdXy"; // ⚠️ yml에서 주입 권장

    /**
     * 결제 승인 요청
     */
    public HttpResponse<String> requestConfirm(PaymentRequestDTO req) throws IOException, InterruptedException {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("paymentKey", req.getPaymentKey());
        node.put("orderId", req.getOrderId());     // = merchantOrderId
        node.put("amount", req.getAmount());


        String requestBody = objectMapper.writeValueAsString(node);

        log.info("📡 Toss Confirm Request body={}", requestBody);
        log.info("📡 Toss Confirm Auth={}", buildBasicAuthHeader(secretKey));

        HttpRequest httpReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                .header("Authorization", buildBasicAuthHeader(secretKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(httpReq, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 결제 취소 요청
     */
    public HttpResponse<String> requestPaymentCancel(String paymentKey, String cancelReason) throws IOException, InterruptedException {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("cancelReason", cancelReason);

        String requestBody = objectMapper.writeValueAsString(node);

        HttpRequest httpReq = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", buildBasicAuthHeader(secretKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(httpReq, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 응답 JSON → DTO 변환
     */
    public PaymentResponseDto parseConfirmResponse(String responseBody) throws IOException {
        return objectMapper.readValue(responseBody, PaymentResponseDto.class);
    }

    /**
     * 인증 헤더 생성 (SecretKey: Base64 인코딩)
     */
    private String buildBasicAuthHeader(String secretKey) {
        String toEncode = secretKey + ":";
        String base64 = Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
        return "Basic " + base64;
    }
}
