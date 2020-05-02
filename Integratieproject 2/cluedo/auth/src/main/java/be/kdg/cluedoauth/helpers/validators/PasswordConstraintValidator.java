package be.kdg.cluedoauth.helpers.validators;

import be.kdg.cluedoauth.helpers.annotations.ValidPassword;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<String> messages = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            messages.add("Password is required.");
        }

        if (messages.isEmpty()) {
            var validator = new PasswordValidator(
                    Arrays.asList(
                            new LengthRule(6, 50),
                            new CharacterRule(EnglishCharacterData.UpperCase, 1),
                            new CharacterRule(EnglishCharacterData.LowerCase, 1),
                            new CharacterRule(EnglishCharacterData.Digit, 1)
                    )
            );


            RuleResult result = validator.validate(new PasswordData(value));
            if (result.isValid()) return true;

            messages = validator.getMessages(result);
        }

        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
            .addConstraintViolation()
            .disableDefaultConstraintViolation();

        return false;
    }
}
