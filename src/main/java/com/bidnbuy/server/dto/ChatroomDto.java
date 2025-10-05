package com.bidnbuy.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ChatroomDto {
    private long chatroomId;
    private long buyerID;
    private long sellerId;
    private long auctionId;
    private LocalDateTime createdAt;
}
