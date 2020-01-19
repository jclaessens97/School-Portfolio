using System;
using System.Linq;
using System.Security.Claims;
using System.Security.Policy;
using System.Text.Encodings.Web;
using System.Threading.Tasks;
using COI.BL.Domain.User;
using COI.DAL.EF;
using COI.UI_MVC.Areas.Identity.Pages.Account;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Internal;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.Routing;
using Microsoft.Extensions.Logging;
using SignInResult = Microsoft.AspNetCore.Identity.SignInResult;

namespace COI.UI_MVC.Areas.Identity.Services
{
    public class AuthRepository : IDisposable
    {
        private readonly SignInManager<User> _signInManager;
        private readonly UserManager<User> _userManager;
//        private readonly ILogger<RegisterModel> _logger;
        private readonly IEmailSender _emailSender;
        
        public AuthRepository( 
            UserManager<User> userManager,
//            ILogger<RegisterModel> logger,
            IEmailSender emailSender, 
            SignInManager<User> signInManager
            )
        {
            _userManager = userManager;
//            _logger = logger;
            _emailSender = emailSender;
            _signInManager = signInManager;
        }

        /*public async Task<IdentityResult> RegisterUser(RegisterModel.InputModel model, IUrlHelper Url, HttpRequest Request)
        {
            var firstName = model.FirstName[0].ToString().ToUpper() + model.FirstName.Substring(1).ToLower();
            var lastName = model.LastName[0].ToString().ToUpper() + model.LastName.Substring(1).ToLower();
            var userName = firstName + "_" + lastName;
            var countUser = _userManager.Users.Where(c => c.UserName.Contains(userName)).Count();

            if (countUser > 0)
            {
                userName = userName + countUser;
            }

            var user = new User {UserName = userName, Email = model.Email, FirstName = firstName, LastName = lastName};
            var result = await _userManager.CreateAsync(user, model.Password);
            if (result.Succeeded)
            {
                _logger.LogInformation("User created a new account with password.");

                await _userManager.AddClaimAsync(user, new Claim("User", "3"));

                var code = await _userManager.GenerateEmailConfirmationTokenAsync(user);
                var callbackUrl = Url.Page(
                    "/Account/ConfirmEmail",
                    pageHandler: null,
                    values: new { userId = user.Id, code = code },
                    protocol: Request.Scheme);
                
                Console.WriteLine("Callback URL = "+callbackUrl);
                //TODO wegkrijgen van "PageHelper"
                //await _emailSender.SendEmailAsync(model.Email, "Confirm your email",
                  //  $"Please confirm your account by <a href='{HtmlEncoder.Default.Encode(callbackUrl)}'>clicking here</a>.");
            }
            return result;
        }*/

        public async Task<User> FindUser(string email)
        {
            User user = await _userManager.FindByEmailAsync(email);
            return user;
        }

        public void Dispose()
        {
            _userManager.Dispose();
        }

        public String getUserName(String email)
        {
            return _userManager.FindByEmailAsync(email).Result.UserName;
        }
        public String getSecurityStamp(String email)
        {
            return _userManager.FindByEmailAsync(email).Result.SecurityStamp;
        }
        
      /*
  
        public async Task<SignInResult> LoginUser(LoginModel.InputModel model)
        {
            var userName = await _userManager.FindByEmailAsync(model.Email);

            if (userName == null)
            {
                return null;
            }
                
            var result = await _signInManager.PasswordSignInAsync(userName, model.Password, model.RememberMe, lockoutOnFailure: true);
            if (result.Succeeded)
            {
                _logger.LogInformation("User logged in.");
            }
            if (result.IsLockedOut)
            {
                _logger.LogWarning("User account locked out.");
            }

            return result;
        }
        
        public async Task<bool> AutoLoginUser(string email, string secret)
        {
            var userName = await _userManager.FindByEmailAsync(email);
            
            if (userName == null)
            {
                return false;
            }
            
            return await _signInManager.ValidateSecurityStampAsync(userName, secret);
            
        }*/

        public async Task<bool> ForgotPassword(ForgotPasswordModel.InputModel model, IUrlHelper Url, HttpRequest Request)
        {
            var user = await _userManager.FindByEmailAsync(model.Email);
            if (user == null || !(await _userManager.IsEmailConfirmedAsync(user)))
            {
                return false;
            }

            // For more information on how to enable account confirmation and password reset please 
            // visit https://go.microsoft.com/fwlink/?LinkID=532713
            var code = await _userManager.GeneratePasswordResetTokenAsync(user);
            //TODO
            var callbackUrl = Url.Page(
                "/Account/ResetPassword",
                pageHandler: null,
                values: new {code},
                protocol: Request.Scheme);

            await _emailSender.SendEmailAsync(
                model.Email,
                "Reset Password",
                $"Please reset your password by <a href='{HtmlEncoder.Default.Encode(callbackUrl)}'>clicking here</a>.");
            return true;
        }

        public async Task<IdentityResult> LogoutEveryWhere(string email)
        {
            User user = await _userManager.FindByEmailAsync(email);
            return await _userManager.UpdateSecurityStampAsync(await _userManager.FindByEmailAsync(email)); 
        }
    }
}