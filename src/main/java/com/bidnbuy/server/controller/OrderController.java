package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.*;
import com.bidnbuy.server.entity.UserEntity;
import com.bidnbuy.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/rating")
    public ResponseEntity<String> rateOrder(
            @PathVariable Long orderId,
            @RequestBody RatingRequest request
    ) {

        Long buyerId = 2L;
        // TODO : 토큰방식으로 변경
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = (User) authentication.getPrincipal();
//        long buyerId = Long.parseLong(user.getUsername());

        orderService.rateOrder(orderId, buyerId, request.getRating());
        return ResponseEntity.ok("별점이 등록되었습니다.");
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto dto) {
        OrderResponseDto response = orderService.createOrder(dto);
        return ResponseEntity.ok(response);
    }

    // 주문 전체 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(
            @RequestParam String type,
            @RequestParam(required = false) String status,
            @RequestHeader("X-USER-ID") Long userId // 📝 임시: 실제론 JWT에서 추출
    ) {
        List<OrderResponseDto> orders = orderService.getMyOrders(userId, type, status);
        return ResponseEntity.ok(orders);
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetail(
            @PathVariable Long orderId,
            @RequestHeader("X-USER-ID") Long userId // 📝 임시: 실제론 JWT에서 추출
    ) {
        OrderResponseDto response = orderService.getOrderDetail(orderId, userId);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderUpdateResponseDto> updateOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-USER-ID") Long userId,  // JWT에서 추출 예정
            @RequestBody OrderUpdateRequestDto dto
    ) {
        OrderUpdateResponseDto response = orderService.updateOrderStatus(orderId, userId, dto);
        return ResponseEntity.ok(response);
    }




}
