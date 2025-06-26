package org.services.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private double price;
    private String imageId;

    public ProductResponse() {

    }
}