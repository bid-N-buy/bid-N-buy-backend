package com.bidnbuy.server.service;

import com.bidnbuy.server.dto.PaymentResponseDto;
import com.bidnbuy.server.dto.SaveAmountRequest;
import com.bidnbuy.server.entity.OrderEntity;
import com.bidnbuy.server.entity.PaymentEntity;
import com.bidnbuy.server.enums.paymentStatus;
import com.bidnbuy.server.repository.OrderRepository;
import com.bidnbuy.server.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

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

        payment.setTossPaymentKey(dto.getPaymentKey());
        payment.setTossPaymentStatus(mapToPaymentStatus(dto.getStatus()));
        payment.setTossPaymentMethod(mapToPaymentMethod(dto.getMethod()));

        if (dto.getRequestedAt() != null) {
            OffsetDateTime odt = OffsetDateTime.parse(dto.getRequestedAt());
            payment.setRequestedAt(odt.toLocalDateTime());
        }
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
                return paymentStatus.PaymentMethod.CARD;
            case "VIRTUAL_ACCOUNT":
                return paymentStatus.PaymentMethod.VIRTUAL_ACCOUNT;
            case "TRANSFER":
                return paymentStatus.PaymentMethod.TRANSFER;
            case "MOBILE_PHONE":
                return paymentStatus.PaymentMethod.MOBILE;
            case "CULTURE_GIFT_CERTIFICATE":
                return paymentStatus.PaymentMethod.CULTURE_GIFT;
            case "BOOK_GIFT_CERTIFICATE":
                return paymentStatus.PaymentMethod.BOOK_GIFT;
            case "GAME_GIFT_CERTIFICATE":
                return paymentStatus.PaymentMethod.GAME_GIFT;
            case "SIMPLE_PAY":
            case "TOSSPAY":
            case "PAYCO":
                return paymentStatus.PaymentMethod.SIMPLE_PAY;
            default:
                throw new IllegalArgumentException("지원하지 않는 결제수단: " + tossMethod);
        }
    }
}
