package com.bidnbuy.server.service;

import com.bidnbuy.server.entity.UserFcmTokenEntity;
import com.bidnbuy.server.enums.NotificationType;
import com.bidnbuy.server.repository.UserFcmTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final UserFcmTokenRepository userTokenRepository;

    public void sendNotification(Long userId, String title, String body, String type, Long notiId, LocalDateTime createdAt) {
        // 1. 유저의 최신 토큰 조회
        List<UserFcmTokenEntity> tokens = userTokenRepository.findByUser_UserId(userId);

        if (tokens.isEmpty()) {
            log.warn("⚠️ [FCM] userId={} 토큰 없음 → 푸시 건너뜀", userId);
            return;
        }

        //setNotification => OS측에서 백그라운드 알림, 나머지 putData => 모달, 포그라운드 알림에서 사용가능하게 변경
        for (UserFcmTokenEntity token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .putData("title", title)
                        .putData("body", body)
                        .putData("type", type)
                        .putData("notificationId", String.valueOf(notiId))
                        .putData("createdAt", createdAt.toString())
                        .build();

                String response = FirebaseMessaging.getInstance().send(message);
                log.info("✅ [FCM] 푸시 전송 성공: userId={}, token={}, response={}",
                        userId, token.getToken(), response);

            } catch (FirebaseMessagingException e) {
                log.error("❌ [FCM] 전송 실패: userId={}, token={}, 이유={}",
                        userId, token.getToken(), e.getMessage());

                // 잘못된 토큰은 DB에서 제거
                if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                        e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    userTokenRepository.delete(token);
                    log.warn("🗑️ 잘못된 토큰 삭제됨: {}", token.getToken());
                }
            }
        }
    }

    public void createChatSendNotification(Long userId, String title, String body, String type, Long notiId, LocalDateTime createdAt, Long auctionId,
                                           Long sellerId) {
        // 유저의 토큰 조회
        List<UserFcmTokenEntity> tokens = userTokenRepository.findByUser_UserId(userId);

        if (tokens.isEmpty()) {
            log.warn("⚠️ [FCM] userId={} 토큰 없음 → 푸시 전송 건너뜀", userId);
            return;
        }

        // 토큰별로 FCM 전송
        for (UserFcmTokenEntity token : tokens) {
            try {
                Message.Builder builder = Message.builder()
                        .setToken(token.getToken())
                        .putData("title", title)
                        .putData("body", body)
                        .putData("type", type)
                        .putData("notificationId", String.valueOf(notiId))
                        .putData("createdAt", createdAt.toString());

                // ✅ auctionId, sellerId 직접 추가
                if (auctionId != null)
                    builder.putData("auctionId", String.valueOf(auctionId));
                if (sellerId != null)
                    builder.putData("sellerId", String.valueOf(sellerId));

                String response = FirebaseMessaging.getInstance().send(builder.build());
                log.info("✅ [FCM] 푸시 전송 성공 (userId={}, token={}, response={})",
                        userId, token.getToken(), response);

            } catch (FirebaseMessagingException e) {

                // 잘못된 토큰 정리
                if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                        e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    userTokenRepository.delete(token);
                }
            }
        }
    }

}
