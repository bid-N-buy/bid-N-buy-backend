package com.bidnbuy.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyRequestDto {
    private Long userId;
    private String type; // LEVEL_1, LEVEL_2, LEVEL_3
}