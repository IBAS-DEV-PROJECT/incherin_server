package ibas.inchelin.domain;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadOne(MultipartFile file) throws IOException {
        String key = "public/" + UUID.randomUUID() + "-" + Objects.requireNonNull(file.getOriginalFilename());

        var metadata = io.awspring.cloud.s3.ObjectMetadata.builder()
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Template.upload(bucket, key, file.getInputStream(), metadata);

        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, key);
    }

    public List<String> uploadMany(List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadOne(file));
        }
        return urls;
    }

    /**
     * S3에서 파일을 삭제합니다.
     * @param fileUrl S3 파일의 전체 URL
     */
    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        if (key != null) {
            s3Template.deleteObject(bucket, key);
        }
    }

    /**
     * S3에서 여러 파일을 삭제합니다.
     * @param fileUrls S3 파일들의 전체 URL 리스트
     */
    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    /**
     * S3 URL에서 키를 추출합니다.
     * @param fileUrl S3 파일의 전체 URL
     * @return 추출된 키, URL 형식이 올바르지 않으면 null
     */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // https://bucket-name.s3.ap-northeast-2.amazonaws.com/key 형식에서 key 추출
        String pattern = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/", bucket);
        if (fileUrl.startsWith(pattern)) {
            return fileUrl.substring(pattern.length());
        }

        return null;
    }
}
