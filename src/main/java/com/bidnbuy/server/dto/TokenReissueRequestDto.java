package com.bidnbuy.server.dto;

import lombok.Getter;
import lombok.Setter;

//액세스 토큰 만료시 새 토큰을 발급받기 위해 (클라이어느에서 저장했던) 리프레시토큰값을 서버에 전달
@Getter
@Setter
public class TokenReissueRequestDto {
    private String refreshToken;
}
