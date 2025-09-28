package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.ResponseDto;
import com.bidnbuy.server.dto.UserDto;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto){
        try{
            UserEntity user  = UserEntity.builder()
                    .email(userDto.getEmail())
                    .nickname(userDto.getNickname())
                    .password(userDto.getPassword())
                    .build();
            UserEntity registeredUser = service.create(user);

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
        UserEntity loginUser = service.findByEmailAndPassword(
                userDto.getEmail(),
                userDto.getPassword()
        );
        //로그인 성공/실패 응답 처리
        if(loginUser != null){
            //토큰 응답 추후 구현
            UserDto reponseUserDto = UserDto.builder()
                    .email(userDto.getEmail())
                    .nickname(userDto.getNickname())
                    .build();
            return ResponseEntity.ok().body(reponseUserDto);
        }else{
            ResponseDto responseDto = ResponseDto.builder()
                    .error("Login failed. Check your email and password.")
                    .build();
            return ResponseEntity.status(401).body(responseDto);
        }
    }
}
