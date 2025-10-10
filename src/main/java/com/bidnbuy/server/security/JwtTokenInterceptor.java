package com.bidnbuy.server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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
                Authentication authentication = jwtProvider.getAuthentication(authToken);
                accessor.setUser(authentication);
            }else{
                throw new AccessDeniedException("없거나 유효하지 않은 토큰");
            }
        }
        return message;
    }

}
