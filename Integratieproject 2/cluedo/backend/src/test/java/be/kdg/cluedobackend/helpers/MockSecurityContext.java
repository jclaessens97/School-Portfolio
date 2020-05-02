package be.kdg.cluedobackend.helpers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockSecurityContext {
    /**
     * Sets the authentication context as a logged in user with a given UUID
     * @param userId
     */
    public static void mockNormalUser(UUID userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add((GrantedAuthority) () -> "USER_ROLE");
        var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
