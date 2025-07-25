# Orders API Documentation

Este documento describe el sistema completo de pedidos (orders) implementado para manejar el proceso de compra desde el carrito hasta la confirmación del pedido.

## Overview

El sistema de pedidos proporciona:
- **Creación de pedidos**: Convertir el carrito de compras en un pedido real
- **Gestión de estados**: Seguimiento del estado del pedido (pendiente, en proceso, enviado, etc.)
- **Códigos de seguimiento**: Identificación única para cada pedido
- **Validaciones**: Verificación de productos, stock y datos del usuario
- **Cálculo automático**: Total del pedido basado en productos y cantidades

## Estructura de Datos

### OrderEntity (MongoDB Document)
```json
{
  "_id": "ObjectId",
  "userId": "Long",
  "items": [
    {
      "productId": "String",
      "productName": "String", 
      "productImageId": "String",
      "quantity": "Integer",
      "unitPrice": "BigDecimal",
      "subtotal": "BigDecimal"
    }
  ],
  "total": "BigDecimal",
  "status": "PENDING|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "shippingAddress": {
    "street": "String",
    "city": "String",
    "state": "String",
    "country": "String",
    "postalCode": "String",
    "phoneNumber": "String",
    "recipientName": "String"
  },
  "paymentMethod": "String",
  "trackingCode": "String"
}
```

## API Endpoints

### 1. Crear Pedido
```
POST /api/v1/orders
Content-Type: application/json
User-Id: {userId}
```

**Request Body:**
```json
{
  "items": [
    {
      "productId": "123",
      "quantity": 2
    },
    {
      "productId": "456", 
      "quantity": 1
    }
  ],
  "shippingAddress": {
    "street": "Calle 10 #5-21",
    "city": "Cúcuta",
    "state": "Norte de Santander",
    "country": "Colombia",
    "postalCode": "540001",
    "phoneNumber": "+573001234567",
    "recipientName": "Juan Pérez"
  },
  "paymentMethod": "pago_contraentrega"
}
```

**Response:**
```json
{
  "message": "Pedido creado exitosamente",
  "orderId": "507f1f77bcf86cd799439011",
  "status": "pendiente",
  "createdAt": "2025-01-15T10:30:45.123",
  "trackingCode": "ORD-20250115-1234"
}
```

### 2. Obtener Pedidos del Usuario
```
GET /api/v1/orders
User-Id: {userId}
```

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "userId": 123,
    "items": [
      {
        "productId": "123",
        "productName": "Producto A",
        "productImageId": "image123",
        "quantity": 2,
        "unitPrice": 15000.00,
        "subtotal": 30000.00
      }
    ],
    "total": 30000.00,
    "status": "pendiente",
    "createdAt": "2025-01-15T10:30:45.123",
    "updatedAt": "2025-01-15T10:30:45.123",
    "shippingAddress": {
      "street": "Calle 10 #5-21",
      "city": "Cúcuta",
      "country": "Colombia"
    },
    "paymentMethod": "pago_contraentrega",
    "trackingCode": "ORD-20250115-1234"
  }
]
```

### 3. Obtener Pedido por ID
```
GET /api/v1/orders/{orderId}
User-Id: {userId}
```

**Response:** Mismo formato que el item individual en la lista anterior.

### 4. Obtener Pedidos por Estado
```
GET /api/v1/orders/status/{status}
```

**Estados disponibles:**
- `PENDING` - pendiente
- `PROCESSING` - en proceso  
- `SHIPPED` - enviado
- `DELIVERED` - entregado
- `CANCELLED` - cancelado

### 5. Actualizar Estado del Pedido
```
PUT /api/v1/orders/{orderId}/status?status={newStatus}
```

**Response:** Pedido actualizado con el nuevo estado.

### 6. Buscar Pedido por Código de Seguimiento
```
GET /api/v1/orders/tracking/{trackingCode}
```

**Response:** Pedido encontrado o 404 si no existe.

## Estados del Pedido

| Estado | Descripción | Acciones Permitidas |
|--------|-------------|-------------------|
| **PENDING** | Pedido creado, pendiente de procesamiento | → PROCESSING, CANCELLED |
| **PROCESSING** | Pedido en proceso de preparación | → SHIPPED, CANCELLED |
| **SHIPPED** | Pedido enviado al cliente | → DELIVERED |
| **DELIVERED** | Pedido entregado exitosamente | - |
| **CANCELLED** | Pedido cancelado | - |

## Validaciones

### Al Crear Pedido
1. **Productos**: Verificar que todos los productos existen
2. **Cantidades**: Debe ser mayor a 0
3. **Stock**: Verificar disponibilidad (implementación futura)
4. **Dirección**: Todos los campos requeridos
5. **Método de Pago**: Debe ser especificado
6. **Usuario**: Debe estar autenticado

### Al Consultar Pedidos
1. **Autorización**: Solo el propietario puede ver sus pedidos
2. **Existencia**: Verificar que el pedido existe

## Códigos de Seguimiento

### Formato
```
ORD-YYYYMMDD-XXXX
```

**Ejemplo:** `ORD-20250115-1234`

- **ORD**: Prefijo fijo
- **YYYYMMDD**: Fecha de creación
- **XXXX**: 4 dígitos aleatorios

### Características
- Único por pedido
- Generado automáticamente
- Fácil de recordar y compartir
- Incluye fecha de creación

## Ejemplos de Uso

### Crear Pedido desde Frontend
```javascript
const createOrder = async (cartItems, shippingAddress) => {
  const response = await fetch('/api/v1/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'User-Id': getCurrentUserId()
    },
    body: JSON.stringify({
      items: cartItems.map(item => ({
        productId: item.id,
        quantity: item.quantity
      })),
      shippingAddress,
      paymentMethod: 'pago_contraentrega'
    })
  });
  
  return response.json();
};
```

### Seguimiento de Pedido
```javascript
const trackOrder = async (trackingCode) => {
  const response = await fetch(`/api/v1/orders/tracking/${trackingCode}`);
  return response.json();
};
```

## Manejo de Errores

### Errores Comunes

| Error | HTTP Status | Descripción |
|-------|-------------|-------------|
| `OrderException` | 400 | Error general del pedido |
| `ProductNotFoundException` | 404 | Producto no encontrado |
| `InsufficientStockException` | 400 | Stock insuficiente |
| `IllegalArgumentException` | 400 | Parámetros inválidos |

### Ejemplo de Error Response
```json
{
  "message": "El pedido no puede estar vacío",
  "timestamp": "2025-01-15T10:30:45.123",
  "errorCode": "BAD_REQUEST",
  "details": "uri=/api/v1/orders"
}
```

## Flujo de Proceso

### 1. Creación del Pedido
```
Carrito → Validación → Cálculo → Creación → Respuesta
```

### 2. Seguimiento del Pedido
```
Cliente → Consulta → Verificación → Respuesta
```

### 3. Actualización de Estado
```
Admin → Validación → Actualización → Respuesta
```

## Consideraciones Técnicas

### Base de Datos
- **MongoDB**: Documentos embebidos para flexibilidad
- **Índices**: userId, status, trackingCode, createdAt
- **Transacciones**: Para operaciones críticas

### Seguridad
- **Autenticación**: Header User-Id requerido
- **Autorización**: Solo propietario puede ver pedidos
- **Validación**: Input sanitization

### Performance
- **Paginación**: Para listas grandes de pedidos
- **Caché**: Estados frecuentemente consultados
- **Índices**: Optimización de consultas

## Próximas Mejoras

1. **Control de Stock**: Validación automática de inventario
2. **Notificaciones**: Email/SMS de actualizaciones
3. **Pagos**: Integración con pasarelas de pago
4. **Envíos**: Integración con servicios de courier
5. **Reportes**: Analytics y estadísticas de ventas 