package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentCancelResponseDto {
    private Long paymentId;
    private Integer cancelAmount;
    private String status;       // CANCELLED
    private LocalDateTime cancelledAt;
}