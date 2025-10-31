package com.bidnbuy.server.repository.projection;

import java.time.LocalDateTime;

public interface AuctionListProjection {
    Long getAuctionId();
    String getTitle();
    Integer getCurrentPrice();
    LocalDateTime getCreatedAt();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    String getSellingStatus();
    Long getSellerId();
    String getSellerNickname();
    String getMainImageUrl();
    Integer getWishCount();
}