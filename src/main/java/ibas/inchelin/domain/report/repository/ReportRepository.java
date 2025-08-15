package ibas.inchelin.domain.report.repository;

import ibas.inchelin.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}

