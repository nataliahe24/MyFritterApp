package org.services.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.services.orders.model.ShippingAddress;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String id;
    private Long userId;
    private List<OrderItemResponse> items;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ShippingAddress shippingAddress;
    private String paymentMethod;
    private String trackingCode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemResponse {
        private String productId;
        private String productName;
        private String productImageId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
} 