package be.kdg.cluedobackend.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendLineId;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private User asking;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private User responding;

    @Column
    private LocalDateTime created;

    @Column
    private FriendType friendType;

    public Friend(User asking, User responding) {
        this.asking = asking;
        this.responding = responding;
        this.created = LocalDateTime.now();
        this.friendType = FriendType.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend)) return false;
        Friend friend = (Friend) o;
        return Objects.equals(friendLineId, friend.friendLineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendLineId);
    }
}
