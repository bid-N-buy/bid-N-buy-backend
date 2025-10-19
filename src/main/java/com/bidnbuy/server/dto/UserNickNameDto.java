package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserNickNameDto {
    private String nickname;
}
