package ibas.inchelin.domain.user.entity;

import ibas.inchelin.domain.BaseTimeEntity;
import ibas.inchelin.domain.user.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(unique = true)
    private String nickname;

    private String name;

    private String bio;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, nullable = false)
    private String sub;

    @Builder
    public User(String email, String name, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    @PrePersist
    public void prePersist() {
        if (this.sub == null) {
            this.sub = UUID.randomUUID().toString();
        }
        if (this.nickname == null || this.nickname.isBlank()) {
            this.nickname = "user" + UUID.randomUUID();
        }
    }
}
