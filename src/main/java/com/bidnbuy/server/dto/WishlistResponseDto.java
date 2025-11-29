package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class WishlistResponseDto {
    private Long auctionId;
    private String title;
    private String mainImageUrl;

    // 2. 가격 및 시간 정보
    private Integer currentPrice;
    private LocalDateTime endTime;

    // 3. 판매자 정보
    private String sellerNickname;
    private String sellingStatus;

    private boolean isDeleted; // 물품 삭제 여부
    private String deleteMessage; // 삭제 메시지 (예: "경매 물품이 삭제되었습니다")
}
