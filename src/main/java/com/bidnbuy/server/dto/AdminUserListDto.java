package com.bidnbuy.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserListDto {
    private Long userId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt; // 가입일
    private int penaltyPoints; // 누적 페널티 점수
    private String activityStatus; // 활동 상태 (활동/정지/강퇴)
    private boolean isSuspended; // 정지 여부
    private LocalDateTime suspendedUntil; // 정지해제일
}