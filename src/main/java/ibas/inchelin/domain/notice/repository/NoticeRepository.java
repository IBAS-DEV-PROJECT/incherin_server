package ibas.inchelin.domain.notice.repository;

import ibas.inchelin.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 이 인터페이스가 데이터 접근 계층(Repository)임을 Spring에게 알려줌
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}

