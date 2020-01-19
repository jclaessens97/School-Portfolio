using Microsoft.AspNetCore.Identity;

namespace COI.UI_MVC.Models.Users
{
    public class CustomIdentityErrorDescriber : IdentityErrorDescriber
    {
        public override IdentityError DefaultError()
        {
            return new IdentityError
                {Code = nameof(DefaultError), Description = $"Een onbekende fout is voorgevallen."};
        }

        public override IdentityError PasswordMismatch()
        {
            return new IdentityError {Code = nameof(PasswordMismatch), Description = "Fout wachtwoord."};
        }

        public override IdentityError InvalidToken()
        {
            return new IdentityError {Code = nameof(InvalidToken), Description = "ongeldig teken."};
        }

        public override IdentityError LoginAlreadyAssociated()
        {
            return new IdentityError
                {Code = nameof(LoginAlreadyAssociated), Description = "Er bestaat al een gebruiker met deze login."};
        }

        public override IdentityError InvalidUserName(string userName)
        {
            return new IdentityError
            {
                Code = nameof(InvalidUserName),
                Description = $"Naam '{userName}' is niet geldig, het mag enkel letters en cijfers bevatten."
            };
        }

        public override IdentityError InvalidEmail(string email)
        {
            return new IdentityError {Code = nameof(InvalidEmail), Description = $"Email '{email}' is niet geldig."};
        }

        public override IdentityError DuplicateUserName(string userName)
        {
            return new IdentityError
                {Code = nameof(DuplicateUserName), Description = $"Gebruikersnaam '{userName}' is al in gebruik."};
        }

        public override IdentityError DuplicateEmail(string email)
        {
            return new IdentityError
                {Code = nameof(DuplicateEmail), Description = $"Email '{email}' is al in gebruik."};
        }

        public override IdentityError UserAlreadyHasPassword()
        {
            return new IdentityError
                {Code = nameof(UserAlreadyHasPassword), Description = "Gebruiker heeft al een wachtwoord."};
        }

        public override IdentityError UserLockoutNotEnabled()
        {
            return new IdentityError
            {
                Code = nameof(UserLockoutNotEnabled),
                Description = "Blokkeren is niet ingeschakeld voor deze gebruiker."
            };
        }

        public override IdentityError PasswordTooShort(int length)
        {
            return new IdentityError
                {Code = nameof(PasswordTooShort), Description = "Wachtwoord moet minstens 6 karakters bevatten."};
        }

        public override IdentityError PasswordRequiresNonAlphanumeric()
        {
            return new IdentityError
            {
                Code = nameof(PasswordRequiresNonAlphanumeric),
                Description = "Wachtwoord moet minstens 1 niet-alfanumeriek karakter bevatten."
            };
        }

        public override IdentityError PasswordRequiresDigit()
        {
            return new IdentityError
            {
                Code = nameof(PasswordRequiresDigit),
                Description = "Wachtwoord moet minstens 1 cijfer bevatten ('0'-'9')."
            };
        }

        public override IdentityError PasswordRequiresLower()
        {
            return new IdentityError
            {
                Code = nameof(PasswordRequiresLower),
                Description = "Wachtwoord moet minstens 1 kleine letter bevatten ('a'-'z')."
            };
        }

        public override IdentityError PasswordRequiresUpper()
        {
            return new IdentityError
            {
                Code = nameof(PasswordRequiresUpper),
                Description = "Wachtwoord moet minstens 1 hoofdletter bevatten ('A'-'Z')."
            };
        }
    }
}