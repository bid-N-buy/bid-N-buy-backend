package com.bidnbuy.server.controller;

import com.bidnbuy.server.dto.PenaltyRequestDto;
import com.bidnbuy.server.enums.PenaltyType;
import com.bidnbuy.server.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/penalty")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class PenaltyController {
    
    private final PenaltyService penaltyService;

    @PostMapping
    public ResponseEntity<?> applyPenalty(@RequestBody PenaltyRequestDto request) {
        log.info("관리자 페널티 부과 요청: userId={}, type={}", request.getUserId(), request.getType());
        
        try {
            PenaltyType penaltyType = PenaltyType.valueOf(request.getType());
            penaltyService.applyPenalty(request.getUserId(), penaltyType);
            
            return ResponseEntity.ok().body("페널티가 성공적으로 부과되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("페널티 부과 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("페널티 부과 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("페널티 부과 중 오류가 발생했습니다.");
        }
    }
}