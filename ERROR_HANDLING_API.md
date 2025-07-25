# Error Handling API Documentation

This document describes the comprehensive error handling system implemented using `@ControllerAdvice` for both Products and Users modules.

## Overview

The error handling system provides:
- **Centralized Exception Handling**: All exceptions are handled by dedicated `@ControllerAdvice` classes
- **Standardized Error Responses**: Consistent error response format across all endpoints
- **Proper HTTP Status Codes**: Appropriate HTTP status codes for different error types
- **Detailed Logging**: Comprehensive logging for debugging and monitoring
- **Frontend-Friendly**: Structured responses that are easy to handle in frontend applications

## Error Response Format

All error responses follow this standardized format:

```json
{
  "message": "Human-readable error message",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "PRODUCT_NOT_FOUND",
  "details": "uri=/api/v1/product/123"
}
```

### Response Fields

- **message**: Human-readable error description
- **timestamp**: When the error occurred (ISO 8601 format)
- **errorCode**: Unique error identifier for frontend handling
- **details**: Additional context (usually the request URI)

## Product Error Handling

### ProductControllerAdvisor

Handles all product-related exceptions in the `/api/v1/product/**` endpoints.

#### Handled Exceptions

| Exception | HTTP Status | Error Code | Description |
|-----------|-------------|------------|-------------|
| `ProductNotFoundException` | 404 | `PRODUCT_NOT_FOUND` | Product not found with given ID |
| `ImageUploadException` | 500 | `IMAGE_UPLOAD_ERROR` | Error uploading image to GridFS |
| `InvalidImageFormatException` | 400 | `INVALID_IMAGE_FORMAT` | Invalid image format or size |
| `IllegalArgumentException` | 400 | `BAD_REQUEST` | Invalid request parameters |
| `MethodArgumentTypeMismatchException` | 400 | `BAD_REQUEST` | Invalid parameter types |
| `Exception` | 500 | `INTERNAL_SERVER_ERROR` | Unexpected server errors |

#### Example Responses

**Product Not Found:**
```json
{
  "message": "Product not found with id: 123",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "PRODUCT_NOT_FOUND",
  "details": "uri=/api/v1/product/123"
}
```

**Invalid Image Format:**
```json
{
  "message": "Only image files are allowed. Supported types: JPEG, PNG, GIF, WEBP",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "INVALID_IMAGE_FORMAT",
  "details": "uri=/api/v1/product"
}
```

## User Error Handling

### UserControllerAdvisor

Handles all user-related exceptions in the `/api/v1/user/**` and `/api/v1/login/**` endpoints.

#### Handled Exceptions

| Exception | HTTP Status | Error Code | Description |
|-----------|-------------|------------|-------------|
| `UserNotFoundException` | 404 | `USER_NOT_FOUND` | User not found with given ID/email |
| `UserAlreadyExistsException` | 409 | `USER_ALREADY_EXISTS` | User already exists with email |
| `InvalidPasswordException` | 401 | `INVALID_PASSWORD` | Invalid password during login |
| `IllegalArgumentException` | 400 | `BAD_REQUEST` | Invalid request parameters |
| `MethodArgumentTypeMismatchException` | 400 | `BAD_REQUEST` | Invalid parameter types |
| `Exception` | 500 | `INTERNAL_SERVER_ERROR` | Unexpected server errors |

#### Example Responses

**User Not Found:**
```json
{
  "message": "User not found with id: user123",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "USER_NOT_FOUND",
  "details": "uri=/api/v1/user/user123"
}
```

**User Already Exists:**
```json
{
  "message": "User already exists with email: user@example.com",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "USER_ALREADY_EXISTS",
  "details": "uri=/api/v1/user"
}
```

**Invalid Password:**
```json
{
  "message": "Invalid password provided",
  "timestamp": "2024-01-15T10:30:45.123",
  "errorCode": "INVALID_PASSWORD",
  "details": "uri=/api/v1/login"
}
```

## Custom Exceptions

### Product Exceptions

#### ProductNotFoundException
```java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
```

#### ImageUploadException
```java
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### InvalidImageFormatException
```java
public class InvalidImageFormatException extends RuntimeException {
    public InvalidImageFormatException(String message) {
        super(message);
    }
}
```

### User Exceptions (Already Existing)

- `UserNotFoundException`
- `UserAlreadyExistsException`
- `InvalidPasswordException`

## Frontend Integration

### Error Handling in Frontend

```typescript
// Example Angular/TypeScript error handling
interface ErrorResponse {
  message: string;
  timestamp: string;
  errorCode: string;
  details: string;
}

async createProduct(productData: FormData): Promise<Product> {
  try {
    const response = await this.http.post<Product>('/api/v1/product', productData);
    return response;
  } catch (error) {
    const errorResponse: ErrorResponse = error.error;
    
    switch (errorResponse.errorCode) {
      case 'PRODUCT_NOT_FOUND':
        this.showError('Producto no encontrado');
        break;
      case 'INVALID_IMAGE_FORMAT':
        this.showError('Formato de imagen no válido');
        break;
      case 'IMAGE_UPLOAD_ERROR':
        this.showError('Error al subir la imagen');
        break;
      default:
        this.showError('Error inesperado');
    }
    
    throw error;
  }
}
```

### Error Code Constants

```typescript
// Frontend error code constants
export const ERROR_CODES = {
  // Product errors
  PRODUCT_NOT_FOUND: 'PRODUCT_NOT_FOUND',
  IMAGE_UPLOAD_ERROR: 'IMAGE_UPLOAD_ERROR',
  INVALID_IMAGE_FORMAT: 'INVALID_IMAGE_FORMAT',
  
  // User errors
  USER_NOT_FOUND: 'USER_NOT_FOUND',
  USER_ALREADY_EXISTS: 'USER_ALREADY_EXISTS',
  INVALID_PASSWORD: 'INVALID_PASSWORD',
  
  // Generic errors
  BAD_REQUEST: 'BAD_REQUEST',
  INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR'
};
```

## Logging

All exceptions are logged with appropriate log levels:

- **ERROR**: For business logic exceptions (not found, validation errors)
- **WARN**: For recoverable errors
- **DEBUG**: For detailed debugging information

### Log Format
```
2024-01-15 10:30:45.123 ERROR [ProductControllerAdvisor] Product not found: Producto no encontrado con id: 123
2024-01-15 10:30:45.124 ERROR [UserControllerAdvisor] User already exists: El correo ya está registrado: user@example.com
```

## Testing Error Scenarios

### Product Error Tests

```bash
# Test product not found
curl -X GET http://localhost:8030/api/v1/product/nonexistent-id

# Test invalid image upload
curl -X POST http://localhost:8030/api/v1/product \
  -F "name=Test Product" \
  -F "description=Test" \
  -F "price=10.00" \
  -F "image=@/path/to/textfile.txt"

# Test image too large
curl -X POST http://localhost:8030/api/v1/product \
  -F "name=Test Product" \
  -F "description=Test" \
  -F "price=10.00" \
  -F "image=@/path/to/large-image.jpg"
```

### User Error Tests

```bash
# Test user not found
curl -X GET http://localhost:8030/api/v1/user/nonexistent-id

# Test invalid login
curl -X POST http://localhost:8030/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"email":"nonexistent@example.com","password":"wrong"}'

# Test user already exists
curl -X POST http://localhost:8030/api/v1/user \
  -H "Content-Type: application/json" \
  -d '{"email":"existing@example.com","password":"password"}'
```

## Best Practices

1. **Always use custom exceptions** for business logic errors
2. **Include meaningful error messages** for debugging
3. **Use appropriate HTTP status codes** for different error types
4. **Log exceptions with context** for monitoring
5. **Provide consistent error response format** for frontend integration
6. **Handle exceptions at the appropriate level** (ControllerAdvice for HTTP errors)
7. **Include error codes** for frontend error handling
8. **Validate input early** to prevent unnecessary processing

## Configuration

The error handling system is automatically configured when the application starts. No additional configuration is required.

### Exception Constants

All error messages and codes are centralized in `ExceptionConstants.java` for easy maintenance and consistency. 