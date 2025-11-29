package com.bidnbuy.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException{

        String path = request.getRequestURI();
        if (path.startsWith("/auth/kakao") || path.startsWith("/api/auth/kakao")
                || path.startsWith("/auth/naver") || path.startsWith("/api/auth/naver")) {
            log.info("소셜 로그인 경로이므로 Unauthorized 무시: {}", path);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = String.format(
                "{ \"error\": \"Unauthorized\", \"message\": \"%s\" }",
                authException.getMessage() != null ? authException.getMessage() : "인증 정보가 누락되었거나 유효하지 않습니다."
        );
        response.getWriter().write(jsonResponse);
    }
}
