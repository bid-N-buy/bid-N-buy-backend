package com.bidnbuy.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatReadStatusUpdateDto {
    private Long chatroomId;
    private Long readerId;
    private int updatedCount;
}
