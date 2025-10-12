package com.bidnbuy.server.dto;

import lombok.Data;

@Data
public class SaveAmountRequest {
    private String orderId;
    private int amount;
}
