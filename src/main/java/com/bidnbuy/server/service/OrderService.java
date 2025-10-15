package com.bidnbuy.server.service;

import com.bidnbuy.server.dto.OrderRequestDto;
import com.bidnbuy.server.dto.OrderResponseDto;
import com.bidnbuy.server.entity.OrderEntity;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.repository.OrderRepository;
import com.bidnbuy.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

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
