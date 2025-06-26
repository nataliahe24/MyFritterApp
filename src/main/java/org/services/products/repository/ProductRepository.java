package org.services.products.repository;


import jakarta.transaction.Transactional;
import org.services.products.model.ProductEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    @Transactional
    void deleteProductById(String id);

}
