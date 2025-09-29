package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.*;
import com.bidnbuy.server.entity.RefreshTokenEntity;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.security.JwtProvider;
import com.bidnbuy.server.service.RefreshTokenService;
import com.bidnbuy.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final JwtProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(UserService userService, JwtProvider tokenProvider, RefreshTokenService refreshTokenService){
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto){
        try{
            UserEntity user  = UserEntity.builder()
                    .email(userDto.getEmail())
                    .nickname(userDto.getNickname())
                    .password(userDto.getPassword())
                    .build();
            UserEntity registeredUser = userService.create(user);

            UserDto responseUserDto = UserDto.builder()
                    .email(registeredUser.getEmail())
                    .nickname(registeredUser.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseUserDto);
        }catch(Exception e){
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto){
        UserEntity loginUser = userService.findByEmailAndPassword(
                userDto.getEmail(),
                userDto.getPassword()
        );
        //로그인 성공/실패 응답 처리
        if(loginUser != null){
            //로그인 성공 시 access토큰 생성
            String accessToken = tokenProvider.createAccessToken(loginUser.getUserId());
            //refresh token 생성
            String refreshToken = tokenProvider.createRefreshToken(loginUser.getUserId());

            //refresh token 만료시간, 디비에 저장, 갱신
            long refreshTokenExpirationTime = tokenProvider.getAccessTokenExpirationTime();
            Instant expiryDate = Instant.now().plusMillis(refreshTokenExpirationTime);
            refreshTokenService.saveOrUpdate(loginUser, refreshToken, expiryDate);

            TokenResponseDto tokenInfo = TokenResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .grantType(tokenProvider.getGrantType())
                    .accessTokenExpiresIn(tokenProvider.getAccessTokenExpirationTime())
                    .build();
            AuthResponseDto responseDto = AuthResponseDto.builder()
                    .email(loginUser.getEmail())
                    .nickname(loginUser.getNickname())
                    .tokenInfo(tokenInfo)
                    .build();
            return ResponseEntity.ok().body(responseDto);
        }else{
            ResponseDto responseDto = ResponseDto.builder()
                    .error("Login failed. Check your email and password.")
                    .build();
            return ResponseEntity.status(401).body(responseDto);
        }
    }

    //토큰 재발급
    @PostMapping("/reissue")
        public ResponseEntity<?> reissueToken(@RequestBody TokenReissueRequestDto requestDto){
        String refreshToken = requestDto.getRefreshToken();

        //refresh token 유효성 검증
        String userIdStr = tokenProvider.validateAndGetUserId(refreshToken);
        if(userIdStr == null){
            //유효하지 않거나 만료된 r.token
            ResponseDto responseDto = ResponseDto.builder().error("Invalid or expired Refresh Token").build();
            return ResponseEntity.status(401).body(responseDto);
        }
        long userId = Long.parseLong(userIdStr);

        //디비에 저장된 refreshToken값과 일치하는지 확인
        Optional<RefreshTokenEntity> tokenEntityOptional = refreshTokenService.findByTokenValue(refreshToken);
        if(tokenEntityOptional.isEmpty()){
            ResponseDto responseDto = ResponseDto.builder().error("refresh token found not DB").build();
            return ResponseEntity.status(401).body(responseDto);
        }

        //사용자 정보 조회
        UserEntity user = userService.getById(userId);
        if(user == null){
            ResponseDto responseDto = ResponseDto.builder().error("토큰과 관련된 사용자가 없음").build();
            return ResponseEntity.status(404).body(responseDto);
        }

        //재발급
        String newAccessToken = tokenProvider.createAccessToken(userId);

        //응답
        TokenResponseDto tokenInfo = TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .grantType(tokenProvider.getGrantType())
                .accessTokenExpiresIn(tokenProvider.getAccessTokenExpirationTime())
                .build();

        //응답 반환(사용자 정보 담아서)
        AuthResponseDto reissueResponse = AuthResponseDto.builder()
                .tokenInfo(tokenInfo)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
        return ResponseEntity.ok().body(reissueResponse);
    }


    //토큰 테스트를 위한 테스트 메서드
    @GetMapping("/test")
    public ResponseEntity<?> testAuth(@AuthenticationPrincipal String userId) {
        ResponseDto responseDto = ResponseDto.builder()
                .error("Authenticated! userId: " + userId)
                .build();
        return ResponseEntity.ok().body(responseDto);
    }
}
