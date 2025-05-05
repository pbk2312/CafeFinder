package CafeFinder.cafe.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.InputStream;
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

    // 기존 File 기반 업로드
    public String upload(String filePath) {
        File targetFile = new File(filePath);
        String uploadImageUrl = putS3(targetFile, targetFile.getName());
        removeOriginalFile(targetFile);
        return uploadImageUrl;
    }

    // InputStream 기반 업로드 (임시 파일 없이)
    public String upload(InputStream inputStream, String fileName, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType); // ex: "image/png"
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            log.error("S3 업로드 실패", e);
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success");
        } else {
            log.info("fail to remove");
        }
    }

    public void removeS3File(String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
