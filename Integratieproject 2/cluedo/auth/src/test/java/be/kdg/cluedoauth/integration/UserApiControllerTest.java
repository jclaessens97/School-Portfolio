package be.kdg.cluedoauth.integration;

import be.kdg.cluedoauth.config.JwtProperties;
import be.kdg.cluedoauth.dto.*;
import be.kdg.cluedoauth.exceptions.AuthException;
import be.kdg.cluedoauth.exceptions.AuthExceptionType;
import be.kdg.cluedoauth.helpers.MockSecurityContext;
import be.kdg.cluedoauth.helpers.TokenUtil;
import be.kdg.cluedoauth.model.AppUser;
import be.kdg.cluedoauth.model.security.AuthUser;
import be.kdg.cluedoauth.repositories.UserRepository;
import be.kdg.cluedoauth.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        Optional<AppUser> userOpt = userRepository.findFirstByUsername("username");
        userOpt.ifPresent(appUser -> userRepository.delete(appUser));
    }

    @Test
    public void register() throws Exception {
        var registerDto = new RegisterDto("username", "Passw0rd", "test@email.com");

        MvcResult result = mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        UserDto userDto = objectMapper.readValue(response, UserDto.class);
        Assert.assertEquals("username", userDto.getUsername());
        Assert.assertEquals("test@email.com", userDto.getEmail());
        Assert.assertFalse(userDto.isVerified());
    }

    @Test
    public void registerEmptyUsername() throws Exception {
        var registerDto = new RegisterDto("", "Passw0rd", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("username"));
        Assert.assertEquals("size must be between 3 and 15", errors.get("username"));
    }

    @Test
    public void registerEmptyPassword() throws Exception {
        var registerDto = new RegisterDto("username", "", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
        .andExpect(status().isBadRequest())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password is required.", errors.get("password"));
    }

    @Test
    public void registerPasswordWithoutUppercase() throws Exception {
        var registerDto = new RegisterDto("username", "passw0rd", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password must contain 1 or more uppercase characters.", errors.get("password"));
    }

    @Test
    public void registerPasswordWithoutLowercase() throws Exception {
        var registerDto = new RegisterDto("username", "PASSW0RD", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password must contain 1 or more lowercase characters.", errors.get("password"));
    }

    @Test
    public void registerPasswordWithoutDigits() throws Exception {
        var registerDto = new RegisterDto("username", "Password", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password must contain 1 or more digit characters.", errors.get("password"));
    }

    @Test
    public void registerPasswordTooShort() throws Exception {
        var registerDto = new RegisterDto("username", "P4ss", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password must be 6 or more characters in length.", errors.get("password"));
    }


    @Test
    public void registerPasswordTooLong() throws Exception {
        var registerDto = new RegisterDto("username", "P4sssssssssssssssssssssssssssssssssssssssssssssssssss", "test@email.com");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("password"));
        Assert.assertEquals("Password must be no more than 50 characters in length.", errors.get("password"));
    }

    @Test
    public void registerEmptyEmail() throws Exception {
        var registerDto = new RegisterDto("username", "Passw0rd", "");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("email"));
        Assert.assertEquals("Email is required.", errors.get("email"));
    }

    @Test
    public void registerInvalidEmail() throws Exception {
        var registerDto = new RegisterDto("username", "Passw0rd", "invalid");

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        HashMap<String, String> errors = objectMapper.readValue(response, HashMap.class);
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey("email"));
        Assert.assertEquals("must be a well-formed email address", errors.get("email"));
    }

    @Test
    public void registerDoubleUsername() throws Exception {
        var registerDto = new RegisterDto("username", "Passw0rd", "test@email.com");
        var registerDoubleDto = new RegisterDto("username", "Passw0rd", "test1@email.com");

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
        .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDoubleDto))
        )
        .andExpect(status().isBadRequest())
        .andReturn();


        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        ApiError error = objectMapper.readValue(response, ApiError.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus());
        Assert.assertEquals(AuthExceptionType.USERNAME_EXISTS.toString(), error.getMessage());
    }

    @Test
    public void registerDoubleEmail() throws Exception {
        var registerDto = new RegisterDto("username", "Passw0rd", "test@email.com");
        var registerDoubleDto = new RegisterDto("username1", "Passw0rd", "test@email.com");

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto))
        )
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDoubleDto))
        )
                .andExpect(status().isBadRequest())
                .andReturn();


        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        ApiError error = objectMapper.readValue(response, ApiError.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, error.getHttpStatus());
        Assert.assertEquals(AuthExceptionType.EMAIL_EXISTS.toString(), error.getMessage());
    }

    @Test
    public void updatePassword() throws Exception {
        AppUser user = new AppUser();
        UUID userId = null;
        user.setEmail("test@com");
        user.setPassword("Admin1");
        user.setUsername("test");
        if ( ! userRepository.existsAppUserByUsernameOrEmail("test", "test@com")) {
            userId = userService.register(user).getUserId();
        } else {
            userId = userRepository.findFirstByUsername("test").get().getUserId();
        }
        var loginDto = new LoginDto("test", "Admin1");
        MvcResult result = mockMvc.perform(
                post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
        )
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization");

        var passwordDto = new PasswordDto("Admin1", "Admin2");


        mockMvc.perform(put("/auth/changePass")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                token)
                .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());

        AppUser passwordChanged = userRepository.findById(userId).get();
        Assert.assertTrue(new BCryptPasswordEncoder().matches("Admin2", passwordChanged.getPassword()));
    }

    @Test
    public void updateWithWrongOldPassword() throws Exception {

        AppUser user = new AppUser();
        UUID userId = null;
        user.setEmail("test2@com");
        user.setPassword("Admin1");
        user.setUsername("test2");
        if ( ! userRepository.existsAppUserByUsernameOrEmail("test2", "test2@com")) {
            userId = userService.register(user).getUserId();
        } else {
            userId = userRepository.findFirstByUsername("test").get().getUserId();
        }
        var loginDto = new LoginDto("test2", "Admin1");
        MvcResult result = mockMvc.perform(
                post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
        )
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization");

        var passwordDto = new PasswordDto("Admin5", "Admin2");


        mockMvc.perform(put("/auth/changePass")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        token)
                .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updateUserInfo() throws Exception {

        AppUser user = new AppUser();
        UUID userId = null;
        user.setEmail("test2@com");
        user.setPassword("Admin1");
        user.setUsername("test2");
        if ( ! userRepository.existsAppUserByUsernameOrEmail("test2", "test2@com")) {
            userId = userService.register(user).getUserId();
        } else {
            userId = userRepository.findFirstByUsername("test").get().getUserId();
        }
        var loginDto = new LoginDto("test2", "Admin1");
        MvcResult result = mockMvc.perform(
                post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
        )
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getHeader("Authorization");

        var userDtoToSend = new UserDto();
        userDtoToSend.setEmail("brens@com");
        userDtoToSend.setUsername("brens");
        userDtoToSend.setVerified(false);


        MvcResult result1 = mockMvc.perform(put("/auth/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,
                        token)
                .content(objectMapper.writeValueAsString(userDtoToSend)))
                .andExpect(status().isOk())
                .andReturn();

        String stringResult = result1.getResponse().getContentAsString();
        UserDto userDtoReceived = objectMapper.readValue(stringResult, UserDto.class);
        Assert.assertEquals(userDtoToSend.getEmail(), userDtoReceived.getEmail());
        Assert.assertEquals(userDtoToSend.getUsername(), userDtoReceived.getUsername());

    }


}
