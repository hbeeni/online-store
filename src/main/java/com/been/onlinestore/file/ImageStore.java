package com.been.onlinestore.file;

import com.been.onlinestore.common.ErrorMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
public class ImageStore {

    @Value("${image.dir}")
    private String imageDir;

    @Value("${image.path}")
    private String imagePath;

    public String getFullPath(String imageName) {
        return imageDir + imageName;
    }

    public String getImageUrl(String imageName) {
        return imagePath + imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public byte[] getProductImage(String imageName) {
        byte[] imageByteArray;
        try (FileInputStream imageStream = new FileInputStream(getFullPath(imageName))) {
            imageByteArray = imageStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException(ErrorMessages.FAIL_TO_LOAD_IMAGE.getMessage());
        }

        return imageByteArray;
    }

    public String saveImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지를 첨부해주세요.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String savedFilename = createSavedFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(savedFilename)));

        return savedFilename;
    }

    public void deleteImage(String imageName) {
        Path path = Path.of(getFullPath(imageName));
        try {
            Files.delete(path);
            log.info("이미지 삭제 성공. image = {}", getFullPath(imageName));
        } catch (IOException e) {
            log.error("이미지 삭제 불가능", e);
        }
    }

    private String createSavedFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
