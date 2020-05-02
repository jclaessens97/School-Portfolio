package be.kdg.cluedoauth.controllers.api;

import be.kdg.cluedoauth.dto.PasswordDto;
import be.kdg.cluedoauth.dto.RegisterDto;
import be.kdg.cluedoauth.dto.UserDto;
import be.kdg.cluedoauth.exceptions.AuthException;

import be.kdg.cluedoauth.exceptions.AuthExceptionType;
import be.kdg.cluedoauth.helpers.RequestUtil;
import be.kdg.cluedoauth.model.AppUser;
import be.kdg.cluedoauth.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class UserApiController {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public UserApiController(
        ObjectMapper objectMapper,
        UserService userService
    ) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto registerDto) throws AuthException {
        AppUser newUser = objectMapper.convertValue(registerDto, AppUser.class);

        AppUser registeredUser = userService.register(newUser);
        if (registeredUser != null) {
            UserDto userDto = objectMapper.convertValue(registeredUser, UserDto.class);
            return ResponseEntity.ok(userDto);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/userDetails")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader("authorization") String token) throws Exception {
        AppUser user = userService.getUser(token);
        UserDto userDto = objectMapper.convertValue(user, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDto> updateUserInfo(@RequestHeader("authorization") String token, @RequestBody UserDto userDto) throws AuthException {
        userService.updateUser(userDto, token);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/changePass")
    public ResponseEntity<UserDto> changePassword(@RequestHeader("authorization") String token, @Valid @RequestBody PasswordDto passwordDto) throws Exception {
        AppUser appUser = userService.updatePassword(passwordDto.getOldPassword(), passwordDto.getNewPassword(), token);
        UserDto userDto = objectMapper.convertValue(appUser, UserDto.class);
        return ResponseEntity.ok(userDto);
    }

}
