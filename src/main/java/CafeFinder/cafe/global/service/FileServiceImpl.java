package CafeFinder.cafe.global.service;

import static CafeFinder.cafe.global.exception.ErrorCode.PROFILE_SAVE_EXCEPTION;

import CafeFinder.cafe.global.exception.ErrorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.default.image}")
    private String defaultProfileImage;

    @Override
    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return defaultProfileImage;
        }
        try {
            String filePath = generateFilePath(file.getOriginalFilename());
            transferFile(file, filePath);
            return filePath;
        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패", e);
            throw new ErrorException(PROFILE_SAVE_EXCEPTION);
        }
    }

    @Override
    public void deleteProfileImage(String filePath) {
        if (filePath != null && !filePath.equals(defaultProfileImage)) {
            deleteFile(filePath);
        }
    }

    private String generateFilePath(String originalFilename) {
        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path path = Paths.get(uploadDir, fileName);
        return path.toString();
    }

    private void transferFile(MultipartFile file, String filePath) throws IOException {
        File destination = new File(filePath);
        file.transferTo(destination);
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            log.warn("파일 삭제 실패: {}", filePath);
        }
    }

}
