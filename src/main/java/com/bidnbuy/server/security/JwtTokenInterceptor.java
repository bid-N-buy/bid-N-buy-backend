package com.bidnbuy.server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String authToken = accessor.getFirstNativeHeader("Auth-Token");

            if(authToken != null && jwtProvider.validateToken(authToken)){
                try{
                    final Long userIdLong = jwtProvider.getUserIdFromToken(authToken);
                    if(userIdLong == null){
                        throw new AccessDeniedException("토큰에서 유효한 사용자 id를 추출할 수 없습니다.");
                    }
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userIdLong,
                            null,
                            Collections.emptyList()
                    );
                    accessor.setUser(authentication);

                    accessor.getSessionAttributes().put(
                            SimpMessageHeaderAccessor.USER_HEADER,
                            authentication
                    );
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    throw new AccessDeniedException("JWT 처리 중 오류 발생: " + e.getMessage());
                }
            }else{
                System.err.println("STOMP CONNECT 거부: 유효하지 않은 Auth-Token입니다. [Token: " + (authToken != null ? authToken : "NULL") + "]");
                throw new AccessDeniedException("없거나 유효하지 않은 토큰");
            }
        }
        return message;
    }

}
