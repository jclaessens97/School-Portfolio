package be.kdg.cluedoauth.integration;

import be.kdg.cluedoauth.config.JwtProperties;
import be.kdg.cluedoauth.dto.LoginDto;
import be.kdg.cluedoauth.dto.RegisterDto;
import be.kdg.cluedoauth.helpers.TokenUtil;
import be.kdg.cluedoauth.model.AppUser;
import be.kdg.cluedoauth.repositories.UserRepository;
import be.kdg.cluedoauth.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
public class AuthenticationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private AppUser registeredUser;

    @Before
    public void setUp() throws Exception {
        Optional<AppUser> userOpt = userRepository.findFirstByUsername("username");
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
        }

        var registerDto = new RegisterDto("username", "Passw0rd", "test@email.com");
        registeredUser = userService.register(objectMapper.convertValue(registerDto, AppUser.class));
    }

    @After
    public void tearDown() throws Exception {
        if (registeredUser != null) {
            userRepository.delete(registeredUser);
        }
    }

    @Test
    public void authenticate() throws Exception {

        var loginDto = new LoginDto("username", "Passw0rd");
        MvcResult result = mockMvc.perform(
            post("/auth/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto))
        )
        .andExpect(status().isOk())
        .andReturn();

        String token = result.getResponse().getHeader("Authorization");
        Assert.assertNotNull(token);
        Assert.assertTrue(token.startsWith("Bearer "));

        Claims claims = TokenUtil.decodeJWT(token);
        Assert.assertEquals(registeredUser.getUserId().toString(), claims.get("sub"));

        int iat = (int) claims.get("iat");
        int exp = (int) claims.get("exp");
        Assert.assertEquals(jwtProperties.getExpirationInMs() / 1000, exp - iat);

        String[] authorities = objectMapper.convertValue(claims.get("authorities"), String[].class);
        Assert.assertEquals("ROLE_USER", authorities[0]);
    }

    @Test
    public void authenticateInvalid() throws Exception {
        var loginDto = new LoginDto("invalid", "Passw0rd");
        mockMvc.perform(
            post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )
        .andExpect(status().isUnauthorized());
    }
}
