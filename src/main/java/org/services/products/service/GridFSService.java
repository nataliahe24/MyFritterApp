package org.services.products.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.services.products.utils.exceptions.ImageUploadException;
import org.services.products.utils.exceptions.InvalidImageFormatException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GridFSService {

    private final GridFsTemplate gridFsTemplate;
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    public String uploadFile(MultipartFile file) throws IOException {
        // Validate file type
        if (file == null || file.isEmpty()) {
            throw new InvalidImageFormatException("File cannot be null or empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageFormatException("Only image files are allowed. Supported types: JPEG, PNG, GIF, WEBP");
        }
        
        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidImageFormatException("File size cannot exceed 10MB");
        }
        
        String filename = file.getOriginalFilename();
        
        ObjectId objectId = gridFsTemplate.store(
            file.getInputStream(), 
            filename, 
            contentType
        );
        
        return objectId.toString();
    }

    public InputStream downloadFile(String fileId) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(fileId)
            )
        );
        
        if (gridFSFile == null) {
            throw new ImageUploadException("File not found with id: " + fileId);
        }
        
        return gridFsTemplate.getResource(gridFSFile).getInputStream();
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(fileId)
            )
        );
    }
} 