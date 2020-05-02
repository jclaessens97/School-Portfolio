package be.kdg.cluedoauth.model.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * All extra userdata that is needed in the Principal Authentication object
 */
@Getter
public class AuthUser extends User {
    private UUID userId;

    public AuthUser(UUID userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
