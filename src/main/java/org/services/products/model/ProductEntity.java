package org.services.products.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products") // MongoDB collection name
public class ProductEntity {

    @Id
    private String id; // MongoDB usa generalmente String para IDs (ObjectId)

    private String name;
    private String description;
    private double price;
    private String imageId; // GridFS file ID for the product image
}