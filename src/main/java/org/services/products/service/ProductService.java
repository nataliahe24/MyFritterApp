package org.services.products.service;


import lombok.RequiredArgsConstructor;
import org.services.products.dto.request.ProductRequest;
import org.services.products.dto.response.ProductResponse;
import org.services.products.model.ProductEntity;
import org.services.products.repository.ProductRepository;
import org.services.products.utils.page.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public ProductResponse createProduct(ProductRequest request) {
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        ProductEntity savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }
    public PageResult<ProductResponse> getAllProducts(int page, int size) {
        Page<ProductEntity> productPage = productRepository.findAll(PageRequest.of(page, size));
        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResult<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements()
        );
    }


    public ProductResponse updateProduct(String id, ProductRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        ProductEntity updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(String id) {
        productRepository.deleteProductById(id);
    }

    private ProductResponse mapToResponse(ProductEntity product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        return response;
    }
}