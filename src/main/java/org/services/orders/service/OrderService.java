package org.services.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.orders.dto.request.CreateOrderRequest;
import org.services.orders.dto.response.CreateOrderResponse;
import org.services.orders.dto.response.OrderResponse;
import org.services.orders.utils.exceptions.*;
import org.services.orders.model.OrderEntity;
import org.services.orders.model.OrderItem;
import org.services.orders.repository.OrderRepository;
import org.services.orders.utils.TrackingCodeGenerator;
import org.services.products.model.ProductEntity;
import org.services.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.services.configurations.exceptions.ExceptionMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TrackingCodeGenerator trackingCodeGenerator;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, Long userId) {
        log.info("Creating order for user: {}", userId);


        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new EmptyOrderException(ExceptionMessages.ORDER_EMPTY);
        }


        if (request.getShippingAddress() == null) {
            throw new InvalidAddressException(ExceptionMessages.ADDRESS_IS_REQUIRED);
        }


        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new InvalidPaymentMethodException(ExceptionMessages.PAYMENT_IS_REQUIRED);
        }


        List<OrderItem> orderItems = validateAndCreateOrderItems(request.getItems());
        BigDecimal total = calculateTotal(orderItems);


        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotal(total);
        order.setStatus(OrderEntity.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTrackingCode(generateUniqueTrackingCode());


        OrderEntity savedOrder = orderRepository.save(order);

        log.info("Order created successfully with ID: {}", savedOrder.getId());

        return new CreateOrderResponse(
                "Pedido creado exitosamente",
                savedOrder.getId(),
                savedOrder.getStatus().getSpanishName(),
                savedOrder.getCreatedAt(),
                savedOrder.getTrackingCode()
        );
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        log.info("Fetching orders for user: {}", userId);
        
        List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(String orderId, Long userId) {
        log.info("Fetching order: {} for user: {}", orderId, userId);
        
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        
        if (order.isEmpty()) {
            throw new EmptyOrderException(ORDER_NOT_FOUND);
        }
        
        OrderEntity orderEntity = order.get();
        

        if (!orderEntity.getUserId().equals(userId)) {
            throw new InvalidPaymentMethodException(INVALID_PAYMENT_METHOD);
        }
        
        return mapToOrderResponse(orderEntity);
    }

    public List<OrderResponse> getOrdersByStatus(OrderEntity.OrderStatus status) {
        log.info("Fetching orders with status: {}", status);
        
        List<OrderEntity> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderEntity.OrderStatus newStatus) {
        log.info("Updating order: {} status to: {}", orderId, newStatus);
        
        Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            throw new OrderNotFoundException(ORDER_NOT_FOUND);
        }
        
        OrderEntity order = orderOpt.get();
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        OrderEntity updatedOrder = orderRepository.save(order);
        

        return mapToOrderResponse(updatedOrder);
    }

    private List<OrderItem> validateAndCreateOrderItems(List<CreateOrderRequest.OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::validateAndCreateOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItem validateAndCreateOrderItem(CreateOrderRequest.OrderItemRequest itemRequest) {

        Optional<ProductEntity> productOpt = productRepository.findById(itemRequest.getProductId());
        
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException( PRODUCT_NOT_FOUND_MESSAGE_ES + itemRequest.getProductId());
        }
        
        ProductEntity product = productOpt.get();
        

        if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
            throw new EmptyOrderException(ORDER_EMPTY);
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImageId(product.getImageId());
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
        orderItem.calculateSubtotal();
        
        return orderItem;
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateUniqueTrackingCode() {
        String trackingCode;
        int attempts = 0;
        final int maxAttempts = 10;
        
        do {
            trackingCode = trackingCodeGenerator.generateTrackingCode();
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new ErrorCreatingTrackingCodeException(ERROR_TRACKING_CODE);
            }
        } while (orderRepository.existsByTrackingCode(trackingCode));
        
        return trackingCode;
    }

    private OrderResponse mapToOrderResponse(OrderEntity order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus().getSpanishName());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setShippingAddress(order.getShippingAddress());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTrackingCode(order.getTrackingCode());

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);
        
        return response;
    }

    private OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderResponse.OrderItemResponse(
                item.getProductId(),
                item.getProductName(),
                item.getProductImageId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
} 