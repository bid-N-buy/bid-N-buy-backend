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
//        log.error("################>>> Unauthorized at {}: {}", request.getRequestURI(), authException.getMessage(), authException);

//        log.error("인증되지 않은 사용자 접근(unauthorized):{}", authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = String.format(
                "{ \"error\": \"Unauthorized\", \"message\": \"%s\" }",
                authException.getMessage() != null ? authException.getMessage() : "인증 정보가 누락되었거나 유효하지 않습니다."
        );
        response.getWriter().write(jsonResponse);
    }
}
