package org.services.products.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.services.products.utils.exceptions.InvalidImageFormatException;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GridFSServiceTest {

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private GridFSService gridFSService;

    private MockMultipartFile validImage;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setUp() {
        validImage = new MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        invalidFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );
    }

    @Test
    void uploadFile_ValidImage_Success() throws IOException {

        ObjectId expectedObjectId = new ObjectId();
        when(gridFsTemplate.store(any(), anyString(), anyString()))
            .thenReturn(expectedObjectId);


        String result = gridFSService.uploadFile(validImage);


        assertNotNull(result);
        assertEquals(expectedObjectId.toString(), result);
        verify(gridFsTemplate).store(any(), eq("test-image.jpg"), eq("image/jpeg"));
    }

    @Test
    void uploadFile_InvalidFileType_ThrowsException() {

        assertThrows(InvalidImageFormatException.class, () -> {
            gridFSService.uploadFile(invalidFile);
        });

        verify(gridFsTemplate, never()).store(any(), anyString(), anyString());
    }

    @Test
    void uploadFile_NullFile_ThrowsException() {

        assertThrows(InvalidImageFormatException.class, () -> {
            gridFSService.uploadFile(null);
        });

        verify(gridFsTemplate, never()).store(any(), anyString(), anyString());
    }
} 