package be.kdg.cluedoauth.config;

import be.kdg.cluedoauth.controllers.filters.JwtAuthorizationFilter;
import be.kdg.cluedoauth.controllers.filters.JwtUsernameAndPasswordAuthFilter;
import be.kdg.cluedoauth.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(
        JwtProperties jwtProperties,
        UserService userService,
        ObjectMapper objectMapper,
        PasswordEncoder passwordEncoder
    ) {
        this.jwtProperties = jwtProperties;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .exceptionHandling().authenticationEntryPoint((req, res, ex) -> {
                LOGGER.error(ex.getMessage());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            })
        .and()
            .addFilter(new JwtUsernameAndPasswordAuthFilter(authenticationManager(), jwtProperties, objectMapper))
                .addFilter(new JwtAuthorizationFilter(authenticationManager()))
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, jwtProperties.getUri()).permitAll()
            .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
            .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(passwordEncoder);
    }
}
