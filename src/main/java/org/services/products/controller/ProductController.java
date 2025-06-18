package org.services.products.controller;


import lombok.RequiredArgsConstructor;
import org.services.products.dto.request.ProductRequest;
import org.services.products.dto.response.ProductResponse;
import org.services.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.services.products.utils.page.PageResult;



@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<PageResult<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProduct(@RequestParam String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Eliminado correctamente");
    }
}