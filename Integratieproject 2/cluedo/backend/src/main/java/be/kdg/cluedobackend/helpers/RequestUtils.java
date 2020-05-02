package be.kdg.cluedobackend.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.UUID;

public final class RequestUtils {
    public static UUID getUserIdFromAuth(SecurityContext ctx) {
        Authentication auth = ctx.getAuthentication();
        String subject = auth.getPrincipal().toString();
        return UUID.fromString(subject);
    }
}
