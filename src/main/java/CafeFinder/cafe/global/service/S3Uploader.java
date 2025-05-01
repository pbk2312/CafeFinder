package CafeFinder.cafe.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    // S3로 파일 업로드하기
    public String upload(String filePath) throws RuntimeException {
        File targetFile = new File(filePath);
        String uploadImageUrl = putS3(targetFile, targetFile.getName()); // s3로업로드
        removeOriginalFile(targetFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) throws RuntimeException {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName,
            uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    //S3 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("fail to remove");
    }

    public void removeS3File(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new
            DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
