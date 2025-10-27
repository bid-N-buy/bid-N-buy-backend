package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionCreationResponseDto {
    private Integer auctionId;
    private String title;
    private String message;
}
