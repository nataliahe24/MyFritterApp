package org.services.orders.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.services.orders.dto.request.CreateOrderRequest;
import org.services.orders.dto.response.CreateOrderResponse;
import org.services.orders.dto.response.OrderResponse;
import org.services.orders.exceptions.OrderException;
import org.services.orders.exceptions.ProductNotFoundException;
import org.services.orders.model.OrderEntity;
import org.services.orders.utils.OrderItem;
import org.services.orders.utils.ShippingAddress;
import org.services.orders.repository.OrderRepository;
import org.services.orders.utils.TrackingCodeGenerator;
import org.services.products.model.ProductEntity;
import org.services.products.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TrackingCodeGenerator trackingCodeGenerator;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest validRequest;
    private ProductEntity testProduct;
    private OrderEntity savedOrder;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new ProductEntity();
        testProduct.setId("test-product-id");
        testProduct.setName("Test Product");
        testProduct.setPrice(15000.0);
        testProduct.setImageId("image-id");


        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId("test-product-id");
        itemRequest.setQuantity(2);

        validRequest = new CreateOrderRequest();
        validRequest.setItems(List.of(itemRequest));
        validRequest.setShippingAddress(new ShippingAddress(
                "Calle 10 #5-21", "Cúcuta", "Norte de Santander", "Colombia", "540001", "+573001234567", "Juan Pérez"
        ));
        validRequest.setPaymentMethod("pago_contraentrega");


        savedOrder = new OrderEntity();
        savedOrder.setId("order-id");
        savedOrder.setUserId(123L);
        savedOrder.setStatus(OrderEntity.OrderStatus.PENDING);
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());
        savedOrder.setTrackingCode("ORD-20250115-1234");
        savedOrder.setPaymentMethod("pago_contraentrega");
        savedOrder.setShippingAddress(new ShippingAddress(
                "Calle 10 #5-21",
                "Cúcuta",
                "Norte de Santander",
                "Colombia",
                "540001",
                "+573001234567",
                "Juan Pérez"
        ));
        savedOrder.setTotal(java.math.BigDecimal.valueOf(30000));


        OrderItem item = new OrderItem();
        item.setProductId("test-product-id");
        item.setProductName("Test Product");
        item.setProductImageId("image-id");
        item.setQuantity(2);
        item.setUnitPrice(java.math.BigDecimal.valueOf(15000));
        item.calculateSubtotal();
        savedOrder.setItems(List.of(item));
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(productRepository.findById("test-product-id")).thenReturn(Optional.of(testProduct));
        when(trackingCodeGenerator.generateTrackingCode()).thenReturn("ORD-20250115-1234");
        when(orderRepository.existsByTrackingCode("ORD-20250115-1234")).thenReturn(false);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        // Act
        CreateOrderResponse result = orderService.createOrder(validRequest, 123L);

        // Assert
        assertNotNull(result);
        assertEquals("Pedido creado exitosamente", result.getMessage());
        assertEquals("order-id", result.getOrderId());
        assertEquals("pendiente", result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertEquals("ORD-20250115-1234", result.getTrackingCode());

        verify(productRepository).findById("test-product-id");
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_EmptyItems_ThrowsException() {

        validRequest.setItems(Arrays.asList());


        assertThrows(OrderException.class, () -> {
            orderService.createOrder(validRequest, 123L);
        });

        verify(productRepository, never()).findById(anyString());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_NullShippingAddress_ThrowsException() {

        validRequest.setShippingAddress(null);


        assertThrows(OrderException.class, () -> {
            orderService.createOrder(validRequest, 123L);
        });

        verify(productRepository, never()).findById(anyString());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_NullPaymentMethod_ThrowsException() {

        validRequest.setPaymentMethod(null);


        assertThrows(OrderException.class, () -> {
            orderService.createOrder(validRequest, 123L);
        });

        verify(productRepository, never()).findById(anyString());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_ProductNotFound_ThrowsException() {

        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId("non-existent-id");
        itemRequest.setQuantity(1);
        validRequest.setItems(List.of(itemRequest));


        when(productRepository.findById("non-existent-id")).thenReturn(Optional.empty());


        assertThrows(ProductNotFoundException.class, () -> {
            orderService.createOrder(validRequest, 123L);
        });

        verify(productRepository).findById("non-existent-id");
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_InvalidQuantity_ThrowsException() {

        when(productRepository.findById("test-product-id")).thenReturn(Optional.of(testProduct));

        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId("test-product-id");
        itemRequest.setQuantity(0);

        validRequest.setItems(List.of(itemRequest));


        assertThrows(OrderException.class, () -> {
            orderService.createOrder(validRequest, 123L);
        });

        verify(productRepository).findById("test-product-id"); // se alcanza esta línea
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void getUserOrders_Success() {
        // Arrange
        List<OrderEntity> orders = Arrays.asList(savedOrder);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(123L)).thenReturn(orders);

        // Act
        List<OrderResponse> result = orderService.getUserOrders(123L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(savedOrder.getId(), result.get(0).getId());
        assertEquals(savedOrder.getUserId(), result.get(0).getUserId());

        verify(orderRepository).findByUserIdOrderByCreatedAtDesc(123L);
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById("order-id")).thenReturn(Optional.of(savedOrder));

        // Act
        OrderResponse result = orderService.getOrderById("order-id", 123L);

        // Assert
        assertNotNull(result);
        assertEquals(savedOrder.getId(), result.getId());
        assertEquals(savedOrder.getUserId(), result.getUserId());

        verify(orderRepository).findById("order-id");
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderException.class, () -> {
            orderService.getOrderById("non-existent-id", 123L);
        });

        verify(orderRepository).findById("non-existent-id");
    }

    @Test
    void getOrderById_Unauthorized_ThrowsException() {
        // Arrange
        when(orderRepository.findById("order-id")).thenReturn(Optional.of(savedOrder));

        // Act & Assert
        assertThrows(OrderException.class, () -> {
            orderService.getOrderById("order-id", 999L); // Different user
        });

        verify(orderRepository).findById("order-id");
    }


    @Test
    void updateOrderStatus_NotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderException.class, () -> {
            orderService.updateOrderStatus("non-existent-id", OrderEntity.OrderStatus.PROCESSING);
        });

        verify(orderRepository).findById("non-existent-id");
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }
} 