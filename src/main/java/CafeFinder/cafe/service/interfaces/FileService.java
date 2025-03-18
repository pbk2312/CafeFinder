package CafeFinder.cafe.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String saveProfileImage(MultipartFile file);

    void deleteProfileImage(String filePath);

}
