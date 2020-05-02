package be.kdg.cluedoauth.dto.messages;

import be.kdg.cluedoauth.model.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserMessage implements MessageMarker {
    private final MessageType messageType;
    private final UUID userId;
    private final String username;

    private final List<Role> roles;
}
