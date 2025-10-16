package com.bidnbuy.server.service;

import com.bidnbuy.server.dto.OrderRequestDto;
import com.bidnbuy.server.dto.OrderResponseDto;
import com.bidnbuy.server.dto.PaymentCancelRequestDto;
import com.bidnbuy.server.entity.OrderEntity;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.repository.OrderRepository;
import com.bidnbuy.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;


    /**
     * 자동 취소 로직 (스케줄러에서 주기적으로 호출)
     * - CASE A: 아직 결제 진행 안됨 → 주문만 취소
     * - CASE B: 결제 완료된 주문 → Toss 취소 + 로그 남기기 + 주문 취소
     */
    @Transactional
    public void autoCancelExpiredOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24); // 낙찰 후 24시간 기준
        List<OrderEntity> expiredOrders = orderRepository.findExpiredOrders(deadline);

        for (OrderEntity order : expiredOrders) {
            // CASE A: 아직 결제 안 됨 → 주문만 취소
            if (order.getPayment() == null) {
                order.setOrderStatus("CANCELED");
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                continue;
            }

            // CASE B: 결제 완료된 주문 → Toss 취소 + 로그 남기기 + 주문 취소
            try {
                Integer cancelAmount = order.getPayment().getTotalAmount();

                // ✅ paymentService의 cancelPayment() 재사용 (일반 취소 로직 그대로 활용)
                PaymentCancelRequestDto dto = new PaymentCancelRequestDto(
                        order.getPayment().getTossPaymentKey(),
                        "결제 기한 초과 자동 취소",
                        cancelAmount
                );
                paymentService.cancelPayment(dto);

                // ✅ 주문 취소 상태 반영
                order.setOrderStatus("CANCELED");
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

            } catch (Exception e) {
                // 예외 발생 시 로그만 남기고 넘어가기 (스케줄 전체 멈추지 않게)
                System.err.println("자동 취소 실패 (orderId=" + order.getOrderId() + "): " + e.getMessage());
            }
        }
    }

    /**
     * 신규 주문 저장
     */
    public OrderEntity save(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        UserEntity seller = userRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + dto.getSellerId()));

        UserEntity buyer = userRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + dto.getBuyerId()));

        OrderEntity order = new OrderEntity();
        order.setSeller(seller);
        order.setBuyer(buyer);
        order.setType(dto.getType());
        order.setOrderStatus("PENDING"); // 초기 상태
        order.setRating(0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        OrderEntity saved = orderRepository.save(order);
        return new OrderResponseDto(saved.getOrderId(), saved.getOrderStatus());
    }

    public OrderEntity findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

}
