package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.ImageDTO;
import com.bidnbuy.server.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping
    public ResponseEntity<?> registe(
            @AuthenticationPrincipal Long userId,
            @RequestBody ImageDTO dto) {

        imageService.create(dto, userId);

        return ResponseEntity.ok("이미지가 등록되었습니다");
    }
}
