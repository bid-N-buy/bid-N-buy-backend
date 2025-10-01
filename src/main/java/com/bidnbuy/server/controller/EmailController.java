package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.EmailRequestDto;
import com.bidnbuy.server.dto.EmailVerificationRequestDto;
import com.bidnbuy.server.service.AuthService;
import com.bidnbuy.server.service.EmailService;
import com.bidnbuy.server.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth/email")
@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final AuthService authService;

    //인증코드 발송
    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationEmail(@Valid @RequestBody EmailRequestDto requestDto){
        authService.sendVerificationEmail(requestDto.getEmail());

        return ResponseEntity.ok("인증 이메일이 성공적으로 발송되었습니다.");
    }

    //인증코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmailCode(@Valid @RequestBody EmailVerificationRequestDto requestDto){
        authService.completeEmailVerification(requestDto.getEmail(), requestDto.getCode());
        return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다. 로그인해주세요.");
    }

}
