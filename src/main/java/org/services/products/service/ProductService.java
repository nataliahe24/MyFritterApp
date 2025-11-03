package org.services.products.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.products.dto.request.ProductRequest;
import org.services.products.dto.response.ProductResponse;
import org.services.products.dto.response.SaveProductResponse;
import org.services.products.utils.exceptions.ImageUploadException;
import org.services.products.utils.exceptions.ProductNotFoundException;
import org.services.products.model.ProductEntity;
import org.services.products.repository.ProductRepository;
import org.services.products.utils.page.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

import static org.services.configurations.exceptions.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final GridFSService gridFSService;

    public SaveProductResponse createProduct(ProductRequest request) {
        ProductEntity product = new ProductEntity(
                request.getName(),
                request.getDescription(),
                request.getPrice()
        );

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageId = gridFSService.uploadFile(request.getImage());
                product.setImageId(imageId);
            } catch (IOException e) {
                throw new ImageUploadException(IMAGE_UPLOAD_ERROR_MESSAGE_ES + e.getMessage(), e);
            }
        }

        ProductEntity savedProduct = productRepository.save(product);
        return new SaveProductResponse(PRODUCT_CREATED_SUCCESS_MESSAGE_ES, LocalDateTime.now());
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
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE_ES + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());


        if (request.getImage() != null && !request.getImage().isEmpty()) {

            if (product.getImageId() != null) {
                    gridFSService.deleteFile(product.getImageId());
                }
            
            try {
                String imageId = gridFSService.uploadFile(request.getImage());
                product.setImageId(imageId);
            } catch (IOException e) {
                throw new ImageUploadException(IMAGE_UPLOAD_ERROR_MESSAGE_ES + e.getMessage(), e);
            }
        }

        ProductEntity updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(String id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE_ES + id));

        if (product.getImageId() != null) {
                gridFSService.deleteFile(product.getImageId());
        }

        productRepository.deleteProductById(id);
    }

    private ProductResponse mapToResponse(ProductEntity product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setImageId(product.getImageId());
        return response;
    }
}