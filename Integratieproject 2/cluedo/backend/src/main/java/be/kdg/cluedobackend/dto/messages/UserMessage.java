package be.kdg.cluedobackend.dto.messages;

import be.kdg.cluedobackend.model.users.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {
    private MessageType messageType;
    private UUID userId;
    private String username;

    private List<Role> roles;
}
