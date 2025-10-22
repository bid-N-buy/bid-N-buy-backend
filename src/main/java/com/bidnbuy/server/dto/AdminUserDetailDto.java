package com.bidnbuy.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 페널티 관련
    private int penaltyPoints;
    private String activityStatus;
    private boolean isSuspended;
    private LocalDateTime suspendedUntil;
    private int suspensionCount;
    private int banCount;

    // 페널티 히스토리 (dto)
    private List<PenaltyHistoryDto> penaltyHistory;

    private int auctionCount;  // 거래글 개수
    
    private String userType;
    private Double userTemperature;
    private LocalDateTime deletedAt;
}