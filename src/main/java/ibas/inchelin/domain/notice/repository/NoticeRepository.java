package ibas.inchelin.domain.notice.repository;

import ibas.inchelin.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}

