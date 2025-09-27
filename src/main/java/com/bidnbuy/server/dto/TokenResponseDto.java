package com.bidnbuy.server.dto;

import lombok.Builder;
import lombok.Getter;

//로그인 시 최초발급, 재발급 성공 시 사용
//클라이언트에게 엑세스 토크놔 리프레시 토큰 정보 전달
@Builder
@Getter
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String grantType; //Bearer
    private Long accessTokenExpiresIn; //토큰 만료시간
}
