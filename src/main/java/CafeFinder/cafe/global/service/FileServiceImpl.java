package CafeFinder.cafe.global.service;

import static CafeFinder.cafe.global.exception.ErrorCode.PROFILE_SAVE_EXCEPTION;

import CafeFinder.cafe.global.exception.ErrorException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3Uploader s3Uploader;

    @Value("${file.default.image}")
    private String defaultProfileImage;

    @Override
    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return defaultProfileImage;
        }

        try (InputStream inputStream = file.getInputStream()) {
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;
            return s3Uploader.upload(inputStream, fileName, file.getContentType());
        } catch (IOException | RuntimeException e) {
            log.error("프로필 이미지 저장 실패", e);
            throw new ErrorException(PROFILE_SAVE_EXCEPTION);
        }
    }
}
