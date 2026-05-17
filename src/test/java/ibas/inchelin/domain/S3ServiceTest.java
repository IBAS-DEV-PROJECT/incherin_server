package ibas.inchelin.domain;

import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Template s3Template;

    @InjectMocks
    private S3Service s3Service;

    private static final String BUCKET = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Service, "bucket", BUCKET);
    }

    // ───────────────────────────────────────────────
    // uploadOne
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("uploadOne - 업로드 성공 시 S3 URL 반환")
    void uploadOne_success_returnsUrl() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "data".getBytes());

        String url = s3Service.uploadOne(file);

        assertThat(url).startsWith("https://" + BUCKET + ".s3.ap-northeast-2.amazonaws.com/public/");
        assertThat(url).endsWith("-photo.jpg");
    }

    @Test
    @DisplayName("uploadOne - s3Template.upload를 올바른 bucket과 key로 호출")
    void uploadOne_callsS3TemplateWithCorrectBucketAndKey() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "data".getBytes());

        s3Service.uploadOne(file);

        verify(s3Template).upload(
                eq(BUCKET),
                argThat(key -> key.startsWith("public/") && key.endsWith("-photo.jpg")),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("uploadOne - 매번 다른 UUID로 고유한 URL 생성")
    void uploadOne_generatesUniqueUrls() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "data".getBytes());

        String url1 = s3Service.uploadOne(file);
        String url2 = s3Service.uploadOne(file);

        assertThat(url1).isNotEqualTo(url2);
    }

    // ───────────────────────────────────────────────
    // uploadMany
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("uploadMany - 여러 파일 업로드 시 모든 URL 반환")
    void uploadMany_multipleFiles_returnsAllUrls() {
        MockMultipartFile file1 = new MockMultipartFile("file", "a.jpg", "image/jpeg", "data1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "b.jpg", "image/jpeg", "data2".getBytes());

        List<String> urls = s3Service.uploadMany(List.of(file1, file2));

        assertThat(urls).hasSize(2);
        assertThat(urls.get(0)).endsWith("-a.jpg");
        assertThat(urls.get(1)).endsWith("-b.jpg");
    }

    @Test
    @DisplayName("uploadMany - 빈 리스트 입력 시 빈 리스트 반환")
    void uploadMany_emptyList_returnsEmptyList() {
        List<String> urls = s3Service.uploadMany(List.of());

        assertThat(urls).isEmpty();
    }

    @Test
    @DisplayName("uploadMany - 업로드 실패 시 CompletionException(RuntimeException) 발생")
    void uploadMany_uploadFails_throwsCompletionException() throws IOException {
        MultipartFile badFile = mock(MultipartFile.class);
        given(badFile.getOriginalFilename()).willReturn("fail.jpg");
        given(badFile.getInputStream()).willThrow(new IOException("disk error"));

        assertThatThrownBy(() -> s3Service.uploadMany(List.of(badFile)))
                .isInstanceOf(CompletionException.class)
                .cause()
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("파일 업로드 실패: fail.jpg");
    }

    // ───────────────────────────────────────────────
    // deleteFile
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteFile - 유효한 URL이면 올바른 key로 deleteObject 호출")
    void deleteFile_validUrl_callsDeleteObjectWithKey() {
        String key = "public/some-uuid-photo.jpg";
        String url = "https://" + BUCKET + ".s3.ap-northeast-2.amazonaws.com/" + key;

        s3Service.deleteFile(url);

        verify(s3Template).deleteObject(BUCKET, key);
    }

    @Test
    @DisplayName("deleteFile - null URL이면 deleteObject 미호출")
    void deleteFile_nullUrl_doesNotCallDelete() {
        s3Service.deleteFile(null);

        verify(s3Template, never()).deleteObject(any(), any());
    }

    @Test
    @DisplayName("deleteFile - 빈 문자열 URL이면 deleteObject 미호출")
    void deleteFile_emptyUrl_doesNotCallDelete() {
        s3Service.deleteFile("");

        verify(s3Template, never()).deleteObject(any(), any());
    }

    @Test
    @DisplayName("deleteFile - bucket이 다른 URL이면 deleteObject 미호출")
    void deleteFile_differentBucketUrl_doesNotCallDelete() {
        String url = "https://other-bucket.s3.ap-northeast-2.amazonaws.com/public/photo.jpg";

        s3Service.deleteFile(url);

        verify(s3Template, never()).deleteObject(any(), any());
    }

    // ───────────────────────────────────────────────
    // deleteFiles
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("deleteFiles - 여러 유효한 URL이면 각각 deleteObject 호출")
    void deleteFiles_multipleValidUrls_callsDeleteForEach() {
        String key1 = "public/uuid1-a.jpg";
        String key2 = "public/uuid2-b.jpg";
        String url1 = "https://" + BUCKET + ".s3.ap-northeast-2.amazonaws.com/" + key1;
        String url2 = "https://" + BUCKET + ".s3.ap-northeast-2.amazonaws.com/" + key2;

        s3Service.deleteFiles(List.of(url1, url2));

        verify(s3Template).deleteObject(BUCKET, key1);
        verify(s3Template).deleteObject(BUCKET, key2);
    }

    @Test
    @DisplayName("deleteFiles - 빈 리스트이면 deleteObject 미호출")
    void deleteFiles_emptyList_doesNotCallDelete() {
        s3Service.deleteFiles(List.of());

        verify(s3Template, never()).deleteObject(any(), any());
    }

    @Test
    @DisplayName("deleteFiles - 유효하지 않은 URL 포함 시 유효한 URL만 삭제")
    void deleteFiles_mixedUrls_deletesOnlyValidOnes() {
        String validKey = "public/uuid-photo.jpg";
        String validUrl = "https://" + BUCKET + ".s3.ap-northeast-2.amazonaws.com/" + validKey;
        String invalidUrl = "https://other-bucket.s3.ap-northeast-2.amazonaws.com/public/photo.jpg";

        s3Service.deleteFiles(List.of(validUrl, invalidUrl));

        verify(s3Template, times(1)).deleteObject(BUCKET, validKey);
        verify(s3Template, times(1)).deleteObject(any(), any());
    }
}
