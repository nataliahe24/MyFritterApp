package org.services.orders.repository;

import org.services.orders.model.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderEntity, String> {

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("{'userId': ?0, 'status': ?1}")
    List<OrderEntity> findByUserIdAndStatus(Long userId, OrderEntity.OrderStatus status);

    List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderEntity.OrderStatus status);

    boolean existsByTrackingCode(String trackingCode);
}

