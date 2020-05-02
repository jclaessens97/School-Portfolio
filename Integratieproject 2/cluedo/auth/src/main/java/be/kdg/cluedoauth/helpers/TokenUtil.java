package be.kdg.cluedoauth.helpers;

import be.kdg.cluedoauth.config.JwtProperties;
import be.kdg.cluedoauth.model.security.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class TokenUtil {
    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkQ//9Q/HfzuVq1Fnc3RmvlsstjWeIpzYo6PcQLs7PaaEnzjnJteK1k5AjxZcx1mOvpQqwJfqgcp3+wLgEsCmzMHFuMFlDtrf+l5ehDdgN3mkMM6AvzkkC1lNKZvOwDVedzqSNvWqNpxqoqGa9xFAFttOM+ir6+mdKqTtM93MHjh9wAAKWAkR/0KkXH6338hfwVbcYza2XMfwEM4foVcbN8sefDtC9xD9OkiZR37sn/opZMGuF/lIcrrFBwFGwwsNmfoSegBJstROT8J/f+hjPmitvd8XTtKdnOtwq9z77NAc/zQnC+5R0063JKxzTtroGQYqZjpeOjaBpUePqjrCwIDAQAB";

    public static String createToken(Authentication auth, JwtProperties jwtProperties)
            throws IOException, GeneralSecurityException {
        JwtBuilder builder = Jwts.builder();

        AuthUser user = (AuthUser) auth.getPrincipal();
        builder.setSubject(user.getUserId().toString());
        builder = addClaim(builder, auth.getAuthorities());
        builder = addExpiration(builder, jwtProperties.getExpirationInMs());
        builder = signToken(builder, jwtProperties.getPrivateKeyPath());

        return builder.compact();
    }

    private static JwtBuilder addClaim(JwtBuilder jwtBuilder, Collection<? extends GrantedAuthority> authorities) {
        return jwtBuilder
            .claim(
            "authorities",
            authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );
    }

    private static JwtBuilder addExpiration(JwtBuilder jwtBuilder, long expirationInMs) {
        final long now = System.currentTimeMillis();
        return jwtBuilder
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expirationInMs));
    }

    private static JwtBuilder signToken(JwtBuilder jwtBuilder, String privateKeyPath) throws IOException, GeneralSecurityException {
        PrivateKey key = getPrivateKey(privateKeyPath);
        return jwtBuilder
            .signWith(SignatureAlgorithm.RS256, key);
    }

    private static PrivateKey getPrivateKey(String privateKeyPath) throws IOException, GeneralSecurityException {
        String privateKeyFileContent = parsePrivateKeyFile(privateKeyPath);
        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyFileContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private static String parsePrivateKeyFile(String privateKeyPath) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (InputStream is = TokenUtil.class.getResourceAsStream("/" + privateKeyPath)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            boolean inKey = false;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (!inKey) {
                    if (line.startsWith("-----BEGIN ") && line.endsWith(" PRIVATE KEY-----")) {
                        inKey = true;
                    }
                } else {
                    if (line.startsWith("-----END ") && line.endsWith(" PRIVATE KEY-----")) {
                        inKey = false;
                        break;
                    }
                    sb.append(line);
                }
            }
        }

        return sb.toString();
    }

    public static Claims decodeJWT(String token) {
        byte[] decode = DatatypeConverter.parseBase64Binary(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
        PublicKey pk = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            pk = kf.generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {}

        return Jwts.parser()
            .setSigningKey(pk)
            .parseClaimsJws(token.replace("Bearer ", ""))
            .getBody();
    }
}
