package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageSummaryDto {
    private String nickname;
    private String email;
    private Double temperature;
    private String profileImageUrl;

    private List<AuctionPurchaseHistoryDto> recentPurchases;
    private List<AuctionSalesHistoryDto> recentSales;

}
