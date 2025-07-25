# Product Image Upload API

This API now supports image uploads for products using MongoDB GridFS for efficient storage of large binary files.

## Features

- **GridFS Storage**: Images are stored using MongoDB GridFS, which automatically splits large files into chunks
- **Image Validation**: Only image files (JPEG, PNG, GIF, WEBP) up to 10MB are allowed
- **Automatic Cleanup**: Old images are automatically deleted when products are updated or deleted
- **Image Retrieval**: Images can be retrieved via their GridFS ID

## API Endpoints

### Create Product with Image
```
POST /api/v1/product
Content-Type: multipart/form-data

Parameters:
- name (String, required): Product name
- description (String, required): Product description  
- price (double, required): Product price
- image (File, optional): Product image file
```

**Example using curl:**
```bash
curl -X POST http://localhost:8030/api/v1/product \
  -F "name=Sample Product" \
  -F "description=This is a sample product" \
  -F "price=29.99" \
  -F "image=@/path/to/image.jpg"
```

### Update Product with Image
```
PUT /api/v1/product/{id}
Content-Type: multipart/form-data

Parameters:
- name (String, required): Product name
- description (String, required): Product description
- price (double, required): Product price
- image (File, optional): New product image file
```

### Get Product Image
```
GET /api/v1/product/image/{imageId}
```

Returns the image file with appropriate content type headers.

### Get All Products
```
GET /api/v1/product?page=0&size=5
```

Returns products with their image IDs included in the response.

## Response Format

### Product Response
```json
{
  "id": "product_id",
  "name": "Product Name",
  "description": "Product Description",
  "price": 29.99,
  "imageId": "gridfs_file_id"
}
```

## Error Handling

- **Invalid File Type**: Returns 400 Bad Request if non-image file is uploaded
- **File Too Large**: Returns 400 Bad Request if file exceeds 10MB
- **File Not Found**: Returns 404 Not Found if image ID doesn't exist
- **Upload Error**: Returns 500 Internal Server Error if GridFS upload fails

## Technical Details

### GridFS Configuration
- Files are automatically chunked by MongoDB GridFS
- Chunk size: 255KB (MongoDB default)
- Metadata includes original filename and content type
- Automatic cleanup of orphaned files

### Supported Image Formats
- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- WebP (.webp)

### File Size Limits
- Maximum file size: 10MB
- Recommended: Under 5MB for optimal performance

## Database Schema

### Product Collection
```javascript
{
  "_id": "ObjectId",
  "name": "String",
  "description": "String", 
  "price": "Number",
  "imageId": "String" // GridFS file ID
}
```

### GridFS Collections
- `fs.files`: File metadata
- `fs.chunks`: File data chunks 