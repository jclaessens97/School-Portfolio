package be.kdg.cluedobackend.helpers;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TokenUtils {
    public static UsernamePasswordAuthenticationToken parseToken(String header) {
        if (header != null && !header.isEmpty() && header.startsWith("Bearer ")) {
            try {
                JwtConsumer consumer = new JwtConsumerBuilder()
                    .setSkipAllValidators()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    .build();

                JwtClaims claims = consumer.processToClaims(header.replace("Bearer ", ""));
                List<String> tokenAuthorities = claims.getClaimValue("authorities", ArrayList.class);
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(
                    tokenAuthorities.stream().collect(Collectors.joining(","))
                );

                return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        return null;
    }
}
