package be.kdg.cluedobackend.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    private UUID userId;

    @Column
    private String userName;

    @Column
    private LocalDateTime created;

    @ElementCollection(targetClass = Role.class)
    @CollectionTable
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Role> roles;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private GameStatistics gameStatistics;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    public User(UUID userId, String userName, List<Role> roles) {
        this.userId = userId;
        this.userName = userName;
        this.created = LocalDateTime.now();
        this.roles = roles;
        this.gameStatistics = new GameStatistics();
    }
}
