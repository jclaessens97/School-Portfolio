package be.kdg.cluedoauth.dto;

import be.kdg.cluedoauth.helpers.annotations.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@AllArgsConstructor
@Getter
@Setter
public class RegisterDto {
    @Size(min = 3, max = 15)
    private final String username;

    @ValidPassword
    private final String password;

    @NotBlank(message = "Email is required.")
    @Email
    private final String email;
}
