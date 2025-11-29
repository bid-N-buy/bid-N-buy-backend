package com.bidnbuy.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code; //사용자 입력 인증 코드
}
