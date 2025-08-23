package ibas.inchelin.domain.inquiry.repository;

import ibas.inchelin.domain.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}