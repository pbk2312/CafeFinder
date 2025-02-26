package CafeFinder.cafe.service.member;

import CafeFinder.cafe.exception.ProfileFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Log4j2
public class FileService {

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Value("${file.default.image}")
    private String DEFAULT_PROFILE_IMAG;


    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return DEFAULT_PROFILE_IMAG; // 기본 프로필 이미지 경로 반환
        }

        try {
            // 저장할 파일명 (UUID 사용)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = Paths.get(UPLOAD_DIR, fileName).toString();

            // 로컬에 파일 저장
            File destFile = new File(filePath);
            file.transferTo(destFile);

            return filePath; // DB에 저장할 프로필 이미지 경로 반환
        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패", e);
            throw new ProfileFileException();
        }
    }

}
