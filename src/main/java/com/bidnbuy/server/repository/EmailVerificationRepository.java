package com.bidnbuy.server.repository;

import com.bidnbuy.server.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity,Long> {
    //이메일 주소로 인증정보 찾기
    Optional<EmailVerificationEntity> findByEmail(String email);
}
