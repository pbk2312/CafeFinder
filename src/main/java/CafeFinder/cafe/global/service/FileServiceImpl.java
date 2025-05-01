package CafeFinder.cafe.global.service;

import static CafeFinder.cafe.global.exception.ErrorCode.PROFILE_SAVE_EXCEPTION;

import CafeFinder.cafe.global.exception.ErrorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            // 임시 로컬 저장 없이 바로 S3 업로드
            String originalFilename = file.getOriginalFilename();
            String tempPath = saveToTemp(file, originalFilename);
            String s3Url = s3Uploader.upload(tempPath);
            deleteLocalTemp(tempPath);
            return s3Url;
        } catch (IOException | RuntimeException e) {
            log.error("프로필 이미지 저장 실패", e);
            throw new ErrorException(PROFILE_SAVE_EXCEPTION);
        }
    }

    @Override
    public void deleteProfileImage(String filePath) {
        if (filePath != null && !filePath.equals(defaultProfileImage)) {
            // S3에 저장된 파일 키 추출
            String fileName = extractKey(filePath);
            s3Uploader.removeS3File(fileName);
        }
    }

    /**
     * MultipartFile을 로컬 임시 파일로 저장
     */
    private String saveToTemp(MultipartFile file, String originalFilename) throws IOException {
        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), fileName);
        File tempFile = path.toFile();
        file.transferTo(tempFile);
        return tempFile.getAbsolutePath();
    }

    /**
     * 로컬 임시 파일 삭제
     */
    private void deleteLocalTemp(String tempPath) {
        File file = new File(tempPath);
        if (file.exists() && !file.delete()) {
            log.warn("임시 파일 삭제 실패: {}", tempPath);
        }
    }

    /**
     * S3 URL에서 객체 키만 추출
     */
    private String extractKey(String url) {
        // https://{bucket}.s3.amazonaws.com/{key}
        int idx = url.lastIndexOf('/');
        return idx != -1 ? url.substring(idx + 1) : url;
    }
}
