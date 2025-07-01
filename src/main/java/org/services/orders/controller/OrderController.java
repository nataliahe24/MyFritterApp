package org.services.orders.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.services.orders.dto.request.CreateOrderRequest;
import org.services.orders.dto.response.CreateOrderResponse;
import org.services.orders.dto.response.OrderResponse;
import org.services.orders.model.OrderEntity;
import org.services.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("User-Id") Long userId) {
        
        log.info("Received order creation request for user: {}", userId);
        
        CreateOrderResponse response = orderService.createOrder(request, userId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestHeader("User-Id") Long userId) {
        
        log.info("Fetching orders for user: {}", userId);
        
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable String orderId,
            @RequestHeader("User-Id") Long userId) {
        
        log.info("Fetching order: {} for user: {}", orderId, userId);
        
        OrderResponse order = orderService.getOrderById(orderId, userId);
        
        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable String status) {
        
        log.info("Fetching orders with status: {}", status);
        
        OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status.toUpperCase());
        List<OrderResponse> orders = orderService.getOrdersByStatus(orderStatus);
        
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        
        log.info("Updating order: {} status to: {}", orderId, status);
        
        OrderEntity.OrderStatus orderStatus = OrderEntity.OrderStatus.valueOf(status.toUpperCase());
        OrderResponse order = orderService.updateOrderStatus(orderId, orderStatus);
        
        return ResponseEntity.ok(order);
    }

    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<OrderResponse> getOrderByTrackingCode(
            @PathVariable String trackingCode) {
        
        log.info("Fetching order by tracking code: {}", trackingCode);
        
        // TODO: Implementar método en el servicio para buscar por código de seguimiento
        // OrderResponse order = orderService.getOrderByTrackingCode(trackingCode);
        
        // return ResponseEntity.ok(order);
        return ResponseEntity.notFound().build();
    }
} 