package org.services.products.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.services.configurations.exceptions.ExceptionMessages;
import org.services.products.utils.exceptions.ImageUploadException;
import org.services.products.utils.exceptions.InvalidImageFormatException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.services.configurations.exceptions.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class GridFSService {

    private final GridFsTemplate gridFsTemplate;
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    public String uploadFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageFormatException(IMAGE_FILE_EMPTY_MESSAGE_ES);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageFormatException(INVALID_IMAGE_FORMAT_MESSAGE_ES);
        }
        

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new InvalidImageFormatException(IMAGE_TOO_LARGE_MESSAGE_ES);
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