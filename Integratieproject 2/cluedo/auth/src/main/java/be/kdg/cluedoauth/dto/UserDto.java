package be.kdg.cluedoauth.dto;

import be.kdg.cluedoauth.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private boolean verified;
    private List<Role> roles;
}
