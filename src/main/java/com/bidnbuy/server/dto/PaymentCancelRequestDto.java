package com.bidnbuy.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequestDto {
    private Long paymentId;
    private String cancelReason;
}