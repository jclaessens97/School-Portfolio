package be.kdg.cluedoauth.services.impl;

import be.kdg.cluedoauth.controllers.senders.MessageSender;
import be.kdg.cluedoauth.dto.UserDto;
import be.kdg.cluedoauth.dto.messages.MessageType;
import be.kdg.cluedoauth.dto.messages.UserMessage;
import be.kdg.cluedoauth.exceptions.AuthException;
import be.kdg.cluedoauth.exceptions.AuthExceptionType;
import be.kdg.cluedoauth.helpers.RequestUtil;
import be.kdg.cluedoauth.model.AppUser;
import be.kdg.cluedoauth.model.Role;
import be.kdg.cluedoauth.model.security.AuthUser;
import be.kdg.cluedoauth.repositories.UserRepository;
import be.kdg.cluedoauth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("userServiceDatabase")
@Transactional
public class UserServiceDatabaseImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSender<UserMessage> sender;

    @Autowired
    public UserServiceDatabaseImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        MessageSender<UserMessage> sender
    ) throws AuthException {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sender = sender;

        seed();
    }

    private void seed() throws AuthException {
        int userCount = userRepository.findAll().size();
        if (userCount > 0) return;

        AppUser admin = new AppUser();
        admin.setUsername("admin");
        admin.setUserId(UUID.randomUUID());
        admin.setRoles(List.of(Role.ADMIN));
        admin.setPassword("admin");
        admin.setEmail("admin@cluedo.be");
        this.register(admin);

        AppUser user1 = new AppUser();
        user1.setUsername("user1");
        user1.setUserId(UUID.randomUUID());
        user1.setRoles(List.of(Role.USER));
        user1.setPassword("user1");
        user1.setEmail("user1@cluedo.be");
        this.register(user1);

        AppUser user2 = new AppUser();
        user2.setUsername("user2");
        user2.setUserId(UUID.randomUUID());
        user2.setRoles(List.of(Role.USER));
        user2.setPassword("user2");
        user2.setEmail("user2@cluedo.be");
        this.register(user2);

        AppUser user3 = new AppUser();
        user3.setUsername("user3");
        user3.setUserId(UUID.randomUUID());
        user3.setRoles(List.of(Role.USER));
        user3.setPassword("user3");
        user3.setEmail("user3n@cluedo.be");
        this.register(user3);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findFirstByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
               String.format("User with username %s not found.", username)
            ));

        List<GrantedAuthority> grantedAuthorities = roleListToGrantedAuthorityList(user.getRoles());
        return new AuthUser(
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            grantedAuthorities
        );
    }

    @Override
    public AppUser getUserInfo(UUID userId) throws AuthException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthExceptionType.USERID_DOES_NOT_EXIST
        ));
    }

    @Override
    public AppUser register(AppUser appUser) throws AuthException {
        if (userRepository.existsAppUserByEmail(appUser.getEmail())){
            throw new AuthException(AuthExceptionType.EMAIL_EXISTS);
        }

        if (userRepository.existsAppUserByUsername(appUser.getUsername())){
            throw new AuthException(AuthExceptionType.USERNAME_EXISTS);
        }

        appUser.setUserId(UUID.randomUUID());
        if (appUser.getRoles() == null || appUser.getRoles().isEmpty()){
            appUser.setRoles(List.of(Role.USER));
        }
        appUser.setPassword(passwordEncoder.
                encode(appUser.getPassword()));

        // Pushes created user on the queue
        sender.send(new UserMessage(MessageType.CREATE_USER, appUser.getUserId(), appUser.getUsername(), appUser.getRoles()));

        return userRepository.save(appUser);
    }

    @Override
    public void updateUser(UserDto userDto, String token) throws AuthException {
        AppUser appUser = getUser(token);

        if (!appUser.getEmail().toLowerCase().equals(userDto.getEmail().toLowerCase()) && userRepository.existsAppUserByEmail(userDto.getEmail())){
            throw new AuthException(AuthExceptionType.EMAIL_EXISTS);
        }

        if (!appUser.getUsername().toLowerCase().equals(userDto.getUsername().toLowerCase()) && userRepository.existsAppUserByUsername(userDto.getUsername())){
            throw new AuthException(AuthExceptionType.USERNAME_EXISTS);
        }

        appUser.setEmail(userDto.getEmail());
        appUser.setUsername(userDto.getUsername());
        sender.send(new UserMessage(MessageType.UPDATE_USER, appUser.getUserId(), appUser.getUsername(), appUser.getRoles()));
        userRepository.save(appUser);
    }

    @Override
    public AppUser updatePassword(String oldPassword, String newPassword, String token) throws AuthException {
        AppUser user = getUser(token);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthException(AuthExceptionType.WRONG_PASSWORD);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AuthException(AuthExceptionType.PASSWORD_IS_SAME);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }


    @Override
    public AppUser getUser(String token) throws AuthException {
        UUID userId = RequestUtil.getUserIdFromToken(token);
        return this.getUserInfo(userId);
    }

    //#region Helpers
    private List<GrantedAuthority> roleListToGrantedAuthorityList(List<Role> roles) {
        String rolesString = roles.stream().map(r -> "ROLE_" + r.name()).collect(Collectors.joining(","));
        return AuthorityUtils.commaSeparatedStringToAuthorityList(rolesString);
    }
    //#endregion
}
