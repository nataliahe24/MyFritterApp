package org.services.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {

    private String message;
    private String orderId;
    private String status;
    private LocalDateTime createdAt;
    private String trackingCode;
} 