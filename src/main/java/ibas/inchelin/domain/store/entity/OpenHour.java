package ibas.inchelin.domain.store.entity;


import ibas.inchelin.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "open_hour",
        uniqueConstraints = @UniqueConstraint(name = "uk_openhour_store", columnNames = "store_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenHour extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private LocalTime monOpen;  private LocalTime monClose;
    private LocalTime tueOpen;  private LocalTime tueClose;
    private LocalTime wedOpen;  private LocalTime wedClose;
    private LocalTime thuOpen;  private LocalTime thuClose;
    private LocalTime friOpen;  private LocalTime friClose;
    private LocalTime satOpen;  private LocalTime satClose;
    private LocalTime sunOpen;  private LocalTime sunClose;
}