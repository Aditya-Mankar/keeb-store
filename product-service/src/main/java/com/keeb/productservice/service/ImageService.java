package com.keeb.productservice.service;

import com.keeb.productservice.model.Image;
import com.keeb.productservice.repository.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;

    public ResponseEntity<String> saveImage(Long productId, MultipartFile file) throws IOException {
        try {
            Image image = Image.builder()
                    .productId(productId)
                    .fileType(file.getContentType())
                    .fileName(StringUtils.cleanPath(file.getOriginalFilename()))
                    .data(file.getBytes())
                    .build();

            imageRepository.save(image);

            return ResponseEntity.ok("Image saved");
        } catch (Exception e) {
            log.info("Error while saving image " + e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
