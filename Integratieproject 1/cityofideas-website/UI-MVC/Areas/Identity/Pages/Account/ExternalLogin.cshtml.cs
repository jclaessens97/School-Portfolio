using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Logging;
using User = COI.BL.Domain.User.User;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Areas.Identity.Pages.Account
{
    [AllowAnonymous]
    public class ExternalLoginModel : PageModel
    {
        private readonly SignInManager<User> _signInManager;
        private readonly UserManager<User> _userManager;
        private readonly ILogger<ExternalLoginModel> _logger;

        public ExternalLoginModel(
            SignInManager<User> signInManager,
            UserManager<User> userManager,
            ILogger<ExternalLoginModel> logger)
        {
            _signInManager = signInManager;
            _userManager = userManager;
            _logger = logger;
        }

        [BindProperty]
        public InputModel Input { get; set; }

        public string LoginProvider { get; set; }

        public string ReturnUrl { get; set; }

        [TempData]
        public string ErrorMessage { get; set; }

        public class InputModel
        {
            [Required]
            [DataType(DataType.Text)]
            [Display(Name = "First name")]
            public string FirstName { get; set; }

            [Required]
            [DataType(DataType.Text)]
            [Display(Name = "Last name")]
            public string LastName { get; set; }
        }

        public IActionResult OnGetAsync()
        {
            return RedirectToPage("./Login");
        }

        public IActionResult OnPost(string provider, string returnUrl = null)
        {
            // Request a redirect to the external login provider.
            var redirectUrl = Url.Page("./ExternalLogin", pageHandler: "Callback", values: new { returnUrl });
            var properties = _signInManager.ConfigureExternalAuthenticationProperties(provider, redirectUrl);
            return new ChallengeResult(provider, properties);
        }

        public async Task<IActionResult> OnGetCallbackAsync(string returnUrl = null, string remoteError = null)
        {
            returnUrl = returnUrl ?? Url.Content("~/");
            
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            
            if (remoteError != null)
            {
                ErrorMessage = $"Error from external provider: {remoteError}";
                return RedirectToPage("./Login", new {ReturnUrl = returnUrl });
            }
            var info = await _signInManager.GetExternalLoginInfoAsync();
            if (info == null)
            {
                ErrorMessage = "Error loading external login information.";
                return RedirectToPage("./Login", new { ReturnUrl = returnUrl });
            }

            // Sign in the user with this external login provider if the user already has a login.
            var result = await _signInManager.ExternalLoginSignInAsync(info.LoginProvider, info.ProviderKey, isPersistent: false, bypassTwoFactor : true);
            if (result.Succeeded)
            {
                _logger.LogInformation("{Name} logged in with {LoginProvider} provider.", info.Principal.Identity.Name, info.LoginProvider);
                
                var email = info.Principal.FindFirst(ClaimTypes.Email).Value;
                var userFind = await _userManager.FindByEmailAsync(email);
                if (!_userManager.GetUsersForClaimAsync(new Claim("SuperAdmin", "SuperAdmin")).Result
                    .Contains(userFind))
                {
                    if (subdomain != null)
                    {
                        var claimsForSubdomain = _userManager.GetClaimsAsync(userFind).Result.FirstOrDefault(c => c.Type == subdomain);
                        if (claimsForSubdomain == null){
//                            await _signInManager.SignOutAsync();
//                            errors.Add("domein");
//                            return BadRequest(errors);
                            await _userManager.AddClaimAsync(userFind, new Claim(subdomain, "User"));
                            //TODO toaster melding? "Geregistreerd op een nieuw platform"
                        }
                    }
                }
                
                return LocalRedirect(returnUrl);
            }
            if (result.IsLockedOut)
            {
                return RedirectToPage("./Lockout");
            }
            else
            {
                // If the user does not have an account, create an account.
                var email = info.Principal.FindFirst(ClaimTypes.Email).Value;
                var firstName = "";
                var lastName = "";
                try
                {
                    firstName =
                        info.Principal.FindFirst(ClaimTypes.GivenName).Value[0].ToString().ToUpper() +
                        info.Principal.FindFirst(ClaimTypes.GivenName).Value.Substring(1).ToLower();
                }
                catch (Exception)
                {
                    firstName = info.Principal.Claims.Where(x => x.Type == "display-name").FirstOrDefault().Value.Split(" ")[0][0].ToString().ToUpper() + 
                                info.Principal.Claims.Where(x => x.Type == "display-name").FirstOrDefault().Value.Split(" ")[0].Substring(1).ToLower();
                }
                
                try
                {
                    lastName =
                        info.Principal.FindFirst(ClaimTypes.Surname).Value[0].ToString().ToUpper() +
                        info.Principal.FindFirst(ClaimTypes.Surname).Value.Substring(1).ToLower();
                }
                catch (Exception)
                {
                    lastName = info.Principal.Claims.Where(x => x.Type == "display-name").FirstOrDefault().Value.Split(" ")[1][0].ToString().ToUpper() + 
                               info.Principal.Claims.Where(x => x.Type == "display-name").FirstOrDefault().Value.Split(" ")[1].Substring(1).ToLower();
                }

                if (email == null)
                {
                    LoginProvider = info.LoginProvider;
                    ErrorMessage = "No email registered on your " + LoginProvider + "account";
                    return RedirectToPage("./Login", new { ReturnUrl = returnUrl });
                }
                
                // Ask firstname and last name if the user does not have an firstname or lastname
                if (firstName == String.Empty | lastName == String.Empty)
                {
                    ReturnUrl = returnUrl;
                    LoginProvider = info.LoginProvider;
                    if (info.Principal.HasClaim(c => c.Type == ClaimTypes.Name))
                    {
                        Input = new InputModel
                        {
                            FirstName = info.Principal.FindFirst(ClaimTypes.Name).ToString().Split(" ")[1][0].ToString().ToUpper() + info.Principal.FindFirst(ClaimTypes.Name).ToString().Split(" ")[1].Substring(1).ToLower(),
                            LastName = info.Principal.FindFirst(ClaimTypes.Name).ToString().Split(" ")[2][0].ToString().ToUpper() + info.Principal.FindFirst(ClaimTypes.Name).ToString().Split(" ")[2].Substring(1).ToLower() 
                        };
                    }
                    return Page();
                }
                
                // If the user does not have an account, then create an account.
                returnUrl = returnUrl ?? Url.Content("~/");
                
                // Get the information about the user from the external login provider
                var loginInfo = await _signInManager.GetExternalLoginInfoAsync();
                if (loginInfo == null)
                {
                    ErrorMessage = "Error loading external login information during confirmation.";
                    return RedirectToPage("./Login", new { ReturnUrl = returnUrl });
                }

                if (ModelState.IsValid)
                {
                    var userName =  firstName + "_" + lastName;
                    var countUser = _userManager.Users.Where(c => c.UserName.Contains(userName)).Count();
 
                    if (countUser > 0)
                    {
                        userName = userName + countUser;
                    }    
            
                    var user = new User { UserName = userName , Email = email, FirstName = firstName, LastName = lastName, EmailConfirmed = true};
                    var createUserResult = await _userManager.CreateAsync(user);
                    if (createUserResult.Succeeded)
                    {
                        await _userManager.AddClaimAsync(user, new Claim(subdomain, "User"));
                        createUserResult = await _userManager.AddLoginAsync(user, info);
                        if (createUserResult.Succeeded)
                        {
                            await _signInManager.SignInAsync(user, isPersistent: false);
                            _logger.LogInformation("User created an account using {Name} provider.", info.LoginProvider);
                            return LocalRedirect(returnUrl);
                        }
                    }
                    foreach (var error in createUserResult.Errors)
                    {
//                        ModelState.AddModelError(string.Empty, error.Description);
                        ErrorMessage = error.Description;
                    }
                }

                LoginProvider = info.LoginProvider;
                ReturnUrl = returnUrl;
                return RedirectToPage("./Login", new { ReturnUrl = returnUrl });
            }
        }

        public async Task<IActionResult> OnPostConfirmationAsync(string returnUrl = null)
        {
            returnUrl = returnUrl ?? Url.Content("~/");
            
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            
            // Get the information about the user from the external login provider
            var info = await _signInManager.GetExternalLoginInfoAsync();
            if (info == null)
            {
                ErrorMessage = "Error loading external login information during confirmation.";
                return RedirectToPage("./Login", new { ReturnUrl = returnUrl });
            }

            if (ModelState.IsValid)
            {
                var email = info.Principal.FindFirst(ClaimTypes.Email).ToString().Split(" ")[1];
                var firstName = Input.FirstName[0].ToString().ToUpper() + Input.FirstName.Substring(1).ToLower();
                var lastName = Input.LastName[0].ToString().ToUpper() + Input.LastName.Substring(1).ToLower();
                var userName =  firstName + "_" + lastName;
                var countUser = _userManager.Users.Where(c => c.UserName.Contains(userName)).Count();

                if (countUser > 0)
                {
                    userName = userName + countUser;
                } 
                
                var user = new User { UserName = userName, Email = email, FirstName = firstName, LastName = lastName, EmailConfirmed = true};
                var result = await _userManager.CreateAsync(user);
                if (result.Succeeded)
                {
                    await _userManager.AddClaimAsync(user, new Claim(subdomain, "User"));
                    result = await _userManager.AddLoginAsync(user, info);
                    if (result.Succeeded)
                    {
                        await _signInManager.SignInAsync(user, isPersistent: false);
                        _logger.LogInformation("User created an account using {Name} provider.", info.LoginProvider);
                        return LocalRedirect(returnUrl);
                    }
                }
                foreach (var error in result.Errors)
                {
                    ModelState.AddModelError(string.Empty, error.Description);
                }
            }

            LoginProvider = info.LoginProvider;
            ReturnUrl = returnUrl;
            return Page();
        }
    }
}
