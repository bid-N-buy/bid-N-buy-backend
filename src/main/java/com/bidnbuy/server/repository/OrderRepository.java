package com.bidnbuy.server.repository;

import com.bidnbuy.server.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

}
