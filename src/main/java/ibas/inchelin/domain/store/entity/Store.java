package ibas.inchelin.domain.store.entity;

import ibas.inchelin.domain.store.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_store_name_address", columnNames = {"place_name", "road_address_name"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String placeName;

    @Enumerated(EnumType.STRING)
    private Category categoryName;

    private String thumbnail;

    private Double x;
    private Double y;

    private String roadAddressName;

    private String phone;

    @Builder
    public Store(String storeName) {
        this.placeName = storeName;
    }
}
