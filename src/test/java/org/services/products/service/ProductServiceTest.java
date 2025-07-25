package org.services.products.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.services.products.dto.request.ProductRequest;
import org.services.products.dto.response.ProductResponse;
import org.services.products.dto.response.SaveProductResponse;
import org.services.products.model.ProductEntity;
import org.services.products.repository.ProductRepository;
import org.services.products.utils.exceptions.ImageUploadException;
import org.services.products.utils.exceptions.ProductNotFoundException;
import org.services.products.utils.page.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private GridFSService gridFSService;

    @InjectMocks
    private ProductService productService;

    private ProductEntity testProduct;
    private ProductRequest testProductRequest;
    private MockMultipartFile testImage;

    @BeforeEach
    void setUp() {
        testProduct = new ProductEntity();
        testProduct.setId("test-id");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(29.99);
        testProduct.setImageId("image-id");

        testProductRequest = new ProductRequest();
        testProductRequest.setName("Test Product");
        testProductRequest.setDescription("Test Description");
        testProductRequest.setPrice(29.99);

        testImage = new MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    @Test
    void createProduct_Success() {
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        SaveProductResponse result = productService.createProduct(testProductRequest);

        assertNotNull(result);
        assertEquals("Producto creado exitosamente", result.message());
        assertNotNull(result.time());

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_WithImage_Success() throws IOException {

        testProductRequest.setImage(testImage);
        when(gridFSService.uploadFile(any())).thenReturn("uploaded-image-id");
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);


        SaveProductResponse result = productService.createProduct(testProductRequest);


        assertNotNull(result);
        verify(gridFSService).uploadFile(testImage);
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_WithImage_UploadError() throws IOException {

        testProductRequest.setImage(testImage);
        when(gridFSService.uploadFile(any())).thenThrow(new IOException("Upload failed"));


        assertThrows(ImageUploadException.class, () -> {
            productService.createProduct(testProductRequest);
        });

        verify(gridFSService).uploadFile(testImage);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void getAllProducts_Success() {

        List<ProductEntity> products = Arrays.asList(testProduct);
        Page<ProductEntity> productPage = new PageImpl<>(products, PageRequest.of(0, 5), 1);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);


        PageResult<ProductResponse> result = productService.getAllProducts(0, 5);


        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(1, result.getTotalElements());

        verify(productRepository).findAll(PageRequest.of(0, 5));
    }

    @Test
    void updateProduct_Success() {

        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);


        ProductResponse result = productService.updateProduct("test-id", testProductRequest);


        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());

        verify(productRepository).findById("test-id");
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_WithNewImage_Success() throws IOException {

        testProductRequest.setImage(testImage);
        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        when(gridFSService.uploadFile(any())).thenReturn("new-image-id");
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);


        ProductResponse result = productService.updateProduct("test-id", testProductRequest);


        assertNotNull(result);
        verify(gridFSService).uploadFile(testImage);
        verify(productRepository).findById("test-id");
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_ProductNotFound() {

        when(productRepository.findById("non-existent-id")).thenReturn(Optional.empty());


        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct("non-existent-id", testProductRequest);
        });

        verify(productRepository).findById("non-existent-id");
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void updateProduct_WithImage_UploadError() throws IOException {

        testProductRequest.setImage(testImage);
        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        when(gridFSService.uploadFile(any())).thenThrow(new IOException("Upload failed"));


        assertThrows(ImageUploadException.class, () -> {
            productService.updateProduct("test-id", testProductRequest);
        });

        verify(gridFSService).uploadFile(testImage);
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void deleteProduct_Success() {

        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteProductById("test-id");


        productService.deleteProduct("test-id");


        verify(productRepository).findById("test-id");
        verify(productRepository).deleteProductById("test-id");
    }

    @Test
    void deleteProduct_WithImage_Success() {

        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        doNothing().when(gridFSService).deleteFile("image-id");
        doNothing().when(productRepository).deleteProductById("test-id");


        productService.deleteProduct("test-id");


        verify(productRepository).findById("test-id");
        verify(gridFSService).deleteFile("image-id");
        verify(productRepository).deleteProductById("test-id");
    }

    @Test
    void deleteProduct_ProductNotFound() {

        when(productRepository.findById("non-existent-id")).thenReturn(Optional.empty());


        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct("non-existent-id");
        });

        verify(productRepository).findById("non-existent-id");
        verify(productRepository, never()).deleteProductById(anyString());
    }

    @Test
    void deleteProduct_WhenImageDeleteFails_ShouldThrowException() {

        testProduct.setImageId("image-id");

        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        doThrow(new RuntimeException("Image delete failed"))
                .when(gridFSService).deleteFile("image-id");


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct("test-id");
        });

        assertEquals("Image delete failed", exception.getMessage());

        verify(productRepository).findById("test-id");
        verify(gridFSService).deleteFile("image-id");
        verify(productRepository, never()).deleteProductById(anyString());
    }
} 