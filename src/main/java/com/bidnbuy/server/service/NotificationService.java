package com.bidnbuy.server.service;

import com.bidnbuy.server.entity.NotificationEntity;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.enums.NotificationType;
import com.bidnbuy.server.repository.NotificationRepository;
import com.bidnbuy.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void sendNotification(Long userId, NotificationType type, String content) {
        // userId 로 UserEntity 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        NotificationEntity noti = NotificationEntity.builder()
                .user(user)   // ✅ UserEntity 전체를 넣어야 함
                .type(type)   // ✅ enum
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(noti);
    }
}
