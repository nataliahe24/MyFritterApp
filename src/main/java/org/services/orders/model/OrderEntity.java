package org.services.orders.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class OrderEntity {

    @Id
    private String id;

    private Long userId;
    private List<OrderItem> items;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ShippingAddress shippingAddress;
    private String paymentMethod;
    private String trackingCode;

    public enum OrderStatus {
        PENDING("pendiente"),
        PROCESSING("en proceso"),
        SHIPPED("enviado"),
        DELIVERED("entregado"),
        CANCELLED("cancelado");

        private final String spanishName;

        OrderStatus(String spanishName) {
            this.spanishName = spanishName;
        }

        public String getSpanishName() {
            return spanishName;
        }
    }
} 