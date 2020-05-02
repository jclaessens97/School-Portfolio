package be.kdg.cluedobackend.model.chat;

import be.kdg.cluedobackend.model.users.User;
import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private String content;
    @ManyToOne
    @Nullable
    private User user;
    private Date timestamp;

    public Message(String content, User user, Date timestamp) {
        this.content = content;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Message(String content) {
        this.content = content;
    }
}
