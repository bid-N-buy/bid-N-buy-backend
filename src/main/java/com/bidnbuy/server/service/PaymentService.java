package com.bidnbuy.server.service;

import com.bidnbuy.server.config.TossPaymentClient;
import com.bidnbuy.server.dto.*;
import com.bidnbuy.server.entity.OrderEntity;
import com.bidnbuy.server.entity.PaymentCancelEntity;
import com.bidnbuy.server.entity.PaymentEntity;
import com.bidnbuy.server.enums.paymentStatus;
import com.bidnbuy.server.repository.OrderRepository;
import com.bidnbuy.server.repository.PaymentCancelRepository;
import com.bidnbuy.server.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCancelRepository paymentCancelRepository;
    private final TossPaymentClient tossPaymentClient;

    // 취소
    @Transactional
    public PaymentCancelResponseDto cancelPayment(PaymentCancelRequestDto requestDto) {
        PaymentEntity payment = paymentRepository.findById(requestDto.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

        // 이미 취소된 경우
        if (payment.getTossPaymentStatus() == paymentStatus.PaymentStatus.CANCEL) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        try {
            TossCancelResponseDto tossResponse = tossPaymentClient.cancelPayment(
                    payment.getTossPaymentKey(),
                    requestDto.getCancelReason()
            );

            // DB에 취소 기록 저장
            PaymentCancelEntity cancel = new PaymentCancelEntity();
            cancel.setPayment(payment);
            cancel.setCancelReason(tossResponse.getCancelReason());
            cancel.setCancelAmount(tossResponse.getCancelAmount());
            cancel.setCancelRequestedAt(LocalDateTime.now());
            cancel.setCancelledAt(tossResponse.getCancelledAt());
            paymentCancelRepository.save(cancel);

            payment.setTossPaymentStatus(paymentStatus.PaymentStatus.CANCEL);
            paymentRepository.save(payment);

            return PaymentCancelResponseDto.builder()
                    .paymentId(payment.getPaymentId())
                    .cancelAmount(cancel.getCancelAmount())
                    .status(payment.getTossPaymentStatus().name())
                    .cancelledAt(cancel.getCancelledAt())
                    .build();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Toss 취소 요청 중 오류 발생", e);
        }
    }




    /**
     * merchantOrderId로 Payment 조회
     */
    public Optional<PaymentEntity> findByMerchantOrderId(String merchantOrderId) {
        return paymentRepository.findByMerchantOrderId(merchantOrderId);
    }

    /**
     * PENDING 상태의 Payment 생성
     */
    @Transactional
    public PaymentEntity createPendingPayment(OrderEntity order, SaveAmountRequest request) {
        // 이미 merchantOrderId 가 존재하면 그대로 리턴
        Optional<PaymentEntity> existing = paymentRepository.findByMerchantOrderId(request.getMerchantOrderId());
        if (existing.isPresent()) {
            return existing.get();
        }

        // 없으면 새로 생성
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setMerchantOrderId(request.getMerchantOrderId());
        payment.setTotalAmount(request.getAmount());
        payment.setTossPaymentStatus(paymentStatus.PaymentStatus.PENDING);
        payment.setRequestedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    /**
     * Toss 결제 승인 응답으로 Payment 갱신
     */
    @Transactional
    public PaymentEntity saveConfirmedPayment(PaymentResponseDto dto) {
        PaymentEntity payment = paymentRepository.findByMerchantOrderId(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found by merchantOrderId: " + dto.getOrderId()));

        // ✅ 이미 성공 상태라면 그대로 리턴
        if (payment.getTossPaymentStatus() == paymentStatus.PaymentStatus.SUCCESS) {
            log.info("이미 승인된 결제: {}", dto.getOrderId());
            return payment;
        }

        payment.setTossPaymentKey(dto.getPaymentKey());
        payment.setTossPaymentStatus(mapToPaymentStatus(dto.getStatus()));
        payment.setTossPaymentMethod(mapToPaymentMethod(dto.getMethod()));

        if (dto.getApprovedAt() != null) {
            OffsetDateTime odt = OffsetDateTime.parse(dto.getApprovedAt());
            payment.setApprovedAt(odt.toLocalDateTime());
        }


        return paymentRepository.save(payment);
    }

    /**
     * Toss status 문자열 → 내부 Enum 매핑
     */
    private paymentStatus.PaymentStatus mapToPaymentStatus(String tossStatus) {
        if (tossStatus == null) return paymentStatus.PaymentStatus.FAIL;

        switch (tossStatus) {
            case "DONE":
                return paymentStatus.PaymentStatus.SUCCESS;
            case "CANCELED":
            case "PARTIAL_CANCELED":
                return paymentStatus.PaymentStatus.CANCEL;
            case "ABORTED":
            case "EXPIRED":
                return paymentStatus.PaymentStatus.FAIL;
            case "REFUND":
                return paymentStatus.PaymentStatus.REFUND;
            default:
                return paymentStatus.PaymentStatus.FAIL; // 안전 fallback
        }
    }

    /**
     * Toss method 문자열 → 내부 Enum 매핑
     */
    private paymentStatus.PaymentMethod mapToPaymentMethod(String tossMethod) {
        if (tossMethod == null) return null;

        switch (tossMethod) {
            case "CARD":
            case "카드":
                return paymentStatus.PaymentMethod.CARD;
            case "VIRTUAL_ACCOUNT":
            case "가상계좌":
                return paymentStatus.PaymentMethod.VIRTUAL_ACCOUNT;
            case "TRANSFER":
            case "계좌이체":
                return paymentStatus.PaymentMethod.TRANSFER;
            case "MOBILE_PHONE":
            case "휴대폰":
                return paymentStatus.PaymentMethod.MOBILE;
            case "CULTURE_GIFT_CERTIFICATE":
            case "문화상품권":
                return paymentStatus.PaymentMethod.CULTURE_GIFT;
            case "BOOK_GIFT_CERTIFICATE":
            case "도서문화상품권":
                return paymentStatus.PaymentMethod.BOOK_GIFT;
            case "GAME_GIFT_CERTIFICATE":
            case "게임문화상품권":
                return paymentStatus.PaymentMethod.GAME_GIFT;
            case "SIMPLE_PAY":
            case "EASY_PAY":
            case "간편결제":
            case "TOSSPAY":
            case "PAYCO":
                return paymentStatus.PaymentMethod.SIMPLE_PAY;
            default:
                log.warn("⚠️ 매핑되지 않은 Toss method 들어옴: {}", tossMethod);
                return null; // 예외 던지지 말고 fallback
        }
    }
}
