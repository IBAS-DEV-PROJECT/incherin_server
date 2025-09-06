package ibas.inchelin;

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
}
