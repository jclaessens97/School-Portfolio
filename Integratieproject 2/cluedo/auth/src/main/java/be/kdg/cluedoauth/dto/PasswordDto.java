package be.kdg.cluedoauth.dto;

import be.kdg.cluedoauth.helpers.annotations.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PasswordDto {
    private final String oldPassword;
    // @ValidPassword
    private final String newPassword;
}
