package be.kdg.cluedoauth.helpers;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.security.Security;
import java.util.UUID;

public final class RequestUtil {
    public static UUID getUserIdFromToken(String token) {
        Claims claims = TokenUtil.decodeJWT(token);
        return UUID.fromString(claims.getSubject());
    }
}
