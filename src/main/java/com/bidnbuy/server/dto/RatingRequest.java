package com.bidnbuy.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private int rating; // 1~10 점
}