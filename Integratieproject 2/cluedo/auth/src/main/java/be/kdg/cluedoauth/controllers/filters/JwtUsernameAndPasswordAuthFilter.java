package be.kdg.cluedoauth.controllers.filters;

import be.kdg.cluedoauth.config.JwtProperties;
import be.kdg.cluedoauth.dto.LoginDto;
import be.kdg.cluedoauth.helpers.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class JwtUsernameAndPasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthFilter.class);
    private final AuthenticationManager authManager;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    public JwtUsernameAndPasswordAuthFilter(
        AuthenticationManager authManager,
        JwtProperties jwtProperties,
        ObjectMapper objectMapper
    ) {
        this.authManager = authManager;
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;

        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtProperties.getUri(), "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginDto creds = objectMapper.readValue(request.getInputStream(), LoginDto.class);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                creds.getUsername(),
                creds.getPassword(),
                Collections.emptyList()
            );
            return authManager.authenticate(token);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException {
        try {
            String token = TokenUtil.createToken(authResult, jwtProperties);
            response.addHeader("Authorization", "Bearer " + token);
        } catch (GeneralSecurityException ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
