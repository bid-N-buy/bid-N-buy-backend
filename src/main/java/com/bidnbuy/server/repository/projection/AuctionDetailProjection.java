package com.bidnbuy.server.repository.projection;

import java.time.LocalDateTime;

public interface AuctionDetailProjection {
    Long getAuctionId();
    String getTitle();
    String getDescription();
    Integer getCurrentPrice();
    Integer getMinBidPrice();
    Integer getBidCount();
    LocalDateTime getStartTime();
    LocalDateTime getCreatedAt();
    LocalDateTime getEndTime();
    Long getCategoryId();

    Long getSellerId();
    String getSellerNickname();
    String getSellerProfileImageUrl();
    Double getSellerTemperature();
    String getSellingStatus();
}