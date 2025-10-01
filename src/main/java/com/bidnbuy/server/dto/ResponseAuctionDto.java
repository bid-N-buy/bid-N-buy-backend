package com.bidnbuy.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseAuctionDto {
    // 상품 기본정보
    private Long auctionId;
    private String title;
    private Integer currentPrice;
    private LocalDateTime endTime;
    private String mainImageUrl;
    private String sellingStatus;
    private String categoryName;
}
