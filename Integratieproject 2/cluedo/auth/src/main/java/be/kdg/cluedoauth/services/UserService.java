package be.kdg.cluedoauth.services;

import be.kdg.cluedoauth.dto.UserDto;
import be.kdg.cluedoauth.exceptions.AuthException;
import be.kdg.cluedoauth.model.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    AppUser register(AppUser appUser) throws AuthException;
    AppUser getUserInfo(UUID userId) throws AuthException;
    void updateUser(UserDto userDto, String token) throws AuthException;

    AppUser updatePassword(String password, String newPassword, String token) throws AuthException;
    AppUser getUser(String token) throws Exception;
}
