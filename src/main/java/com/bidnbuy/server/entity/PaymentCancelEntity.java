package com.bidnbuy.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_cancel")
public class PaymentCancelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cancelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    private String cancelReason;
    private Integer cancelAmount;

    @Column(nullable = false)
    private LocalDateTime cancelRequestedAt;

    private LocalDateTime cancelledAt;
}
