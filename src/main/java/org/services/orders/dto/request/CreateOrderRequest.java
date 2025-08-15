package org.services.orders.dto.request;

import lombok.Data;
import org.services.orders.utils.ShippingAddress;

import java.util.List;

@Data
public class CreateOrderRequest {

    private List<OrderItemRequest> items;
    private ShippingAddress shippingAddress;
    private String paymentMethod;

    @Data
    public static class OrderItemRequest {
        private String productId;
        private Integer quantity;
    }
} 