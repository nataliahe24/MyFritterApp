package org.services.products.controller;

import lombok.RequiredArgsConstructor;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.products.dto.request.ProductRequest;
import org.services.products.dto.response.ProductResponse;
import org.services.products.dto.response.SaveProductResponse;
import org.services.products.service.ProductService;
import org.services.products.service.GridFSService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.services.products.utils.page.PageResult;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final GridFSService gridFSService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SaveProductResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile image)
   {

        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPrice(price);
        request.setImage(image);

       SaveProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResult<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String imageId) throws IOException {
        InputStreamResource resource = new InputStreamResource(gridFSService.downloadFile(imageId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=image")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPrice(price);
        request.setImage(image);
        
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProduct(@RequestParam String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ExceptionMessages.PRODUCT_DELETED_SUCCESS_MESSAGE_ES);
    }
}