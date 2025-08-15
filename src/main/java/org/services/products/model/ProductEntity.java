package org.services.products.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class ProductEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageId;

    public ProductEntity(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;

    }

}