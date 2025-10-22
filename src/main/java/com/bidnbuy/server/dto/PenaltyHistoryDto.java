package com.bidnbuy.server.dto;

import com.bidnbuy.server.enums.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyHistoryDto {
    private Long penaltyId;
    private PenaltyType type;
    private int points;
    private LocalDateTime createdAt;
    private boolean isActive;
}