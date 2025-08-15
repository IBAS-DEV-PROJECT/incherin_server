package ibas.inchelin.domain.store.repository;

import ibas.inchelin.domain.store.entity.OpenHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenHourRepository extends JpaRepository<OpenHour, Long> {
}

