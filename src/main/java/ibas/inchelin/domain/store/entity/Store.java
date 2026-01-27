package ibas.inchelin.domain.store.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.store.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_store_name_address", columnNames = {"storeName", "address"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String thumbnail;

    private Double lat;
    private Double lng;

    private String address;

    private String phone;

    @Builder
    public Store(String storeName) {
        this.storeName = storeName;
    }
}
