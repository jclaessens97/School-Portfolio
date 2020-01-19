using COI.BL;
using COI.BL.Domain.User;
using COI.UI_MVC.Areas.Identity.Pages.Account;
using COI.UI_MVC.Extensions;
using COI.UI_MVC.Models.dto;
using COI.UI_MVC.Models.Users;
using COI.UI_MVC.Scheduler.Tasks;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using Castle.Core.Internal;
using System.Threading.Tasks;
using Castle.Core.Internal;
using UI_MVC.Scheduler.Scheduling;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Controllers.Api
{
    [ApiController]
    [Route("api/users")]
    public class UsersController : ControllerBase
    {
        private readonly UserManager<User> _userManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly IEmailSender _emailSender;
        private readonly SignInManager<User> _signInManager;

        public UsersController(
            UserManager<User> userManager,
            [FromServices] UnitOfWorkManager unitOfWorkManager,
            IEmailSender emailSender,
            SignInManager<User> signInManager)
        {
            _userManager = userManager;
            _unitOfWorkManager = unitOfWorkManager;
            _emailSender = emailSender;
            _signInManager = signInManager;
        }

        [HttpGet]
        public stringDTO Get()
        {
            List<UsersWithClaims> usersWithClaims = new List<UsersWithClaims>();

            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());

            //Get current user
            User userCurr = _userManager.GetUserAsync(User).Result;
            bool isSuperAdmin = _userManager.IsUserSuperAdmin(userCurr);
            //TODO isAdminOrAbove and !IsSuperAdmin
            
            //Get all users
            IList<User> users = _userManager.Users.ToList();

            //TODO bedenkelijk mag dit weg
            /*//Get all users
            if (!isSuperAdmin)
            {
                //TODO nakijken!
                Claim claim = new Claim(subdomain, subdomain);
                users = _userManager.GetUsersForClaimAsync(claim).Result;
                
                //Eventueel nieuwe methode
                /*var claimsForSubdomain = _userManager.GetClaimsAsync(userFind).Result.Where(c => c.Type == subdomain).FirstOrDefault();
                if (claimsForSubdomain == null){#1#
            }*/

            //Get all claims
            string[] claims = Enum.GetNames(typeof(ClaimsUsers));
            List<string> claimsList = claims.OfType<string>().ToList();

            foreach (var user in users)
            {
                //Get claims from user
                var userClaims = _userManager.GetClaimsAsync(user).Result.Where(c => c.Type == subdomain).ToList();
                foreach (var claim in userClaims)
                {
                    if (claimsList.Contains(claim.Value))
                    {
//                        currentClaim.Add(claim.Value);
                        usersWithClaims.Add(new UsersWithClaims()
                        {
                            UserId = user.Id,
                            Username = user.UserName,
                            FirstName = user.FirstName,
                            LastName = user.LastName,
                            Email = user.Email,
                            CurrentClaim = claim.Value,
                            LockOutEnabled = _userManager.IsLockedOutAsync(user).Result,
                            Claim = claims,
                            FirmName = user.FirmName,
                            Organisation = _userManager.userHasClaim(user, "Organisation")
                        });
                    }
                }               
            }

            User currentUser = _userManager.Users.First(c => c.UserName == User.Identity.Name);
            var currentClaimUser = _userManager.GetClaimsAsync(currentUser).Result;

            stringDTO curUser = new stringDTO()
            {
                list = usersWithClaims.OrderBy(o => o.Username).ToList(),
                userClaim = currentClaimUser
            };

            return curUser;
        }

        [HttpGet("{search}", Name = "Get")]
        public stringDTO Get(string search)
        {
            var usersWithClaims = new List<UsersWithClaims>();

            //Get all users
            var users = _userManager.Users.ToList();

            //Get all claims
            string[] claims = Enum.GetNames(typeof(ClaimsUsers));

            foreach (var user in users)
            {
                usersWithClaims.Add(new UsersWithClaims()
                {
                    UserId = user.Id,
                    Username = user.UserName,
                    Email = user.Email,
                    LockOutEnabled = _userManager.IsLockedOutAsync(user).Result,
                    Claim = claims,
                    Organisation = _userManager.userHasClaim(user, "Organisation")

                });
            }

            User currentUser = _userManager.Users.First(c => c.UserName == User.Identity.Name);
            var currentClaimUser = _userManager.GetClaimsAsync(currentUser).Result;

            stringDTO curUser = new stringDTO()
            {
                list = usersWithClaims.OrderBy(o => o.Username)
                    .Where(u => u.Username.ToUpper().Contains(search.ToUpper())).ToList(),
                userClaim = currentClaimUser
            };

            return curUser;
        }

        [HttpPost("ChangeClaim")]
        public async Task<ActionResult> ChangeClaim(stringDTO @string)
        {
            var username = @string.text.Split("|")[0];
            string newClaimValue = @string.text.Split("|")[1];
            var user = _userManager.Users.First(c => c.UserName == username);

            //Get all claims
            Array claims = Enum.GetNames(typeof(ClaimsUsers));
            List<string> claimsList = claims.OfType<string>().ToList();

            var currentClaim = new Claim("", "");
            var userClaims = _userManager.GetClaimsAsync(user).Result.ToList();
            foreach (var claim in userClaims)
            {
                if (claimsList.Contains(claim.Value))
                {
                    currentClaim = new Claim(claim.Type, claim.Value);
                }
            }

            await _userManager.ReplaceClaimAsync(user, new Claim(currentClaim.Type, currentClaim.Value),
                new Claim(currentClaim.Type, newClaimValue));
            _unitOfWorkManager.Save();
            return Ok();
        }

        [HttpPost("DeleteUser")]
        public async Task<ActionResult> DeleteUser(stringDTO username)
        {
            var userdel = _userManager.Users.First(c => c.UserName == username.text);
            await _userManager.DeleteAsync(userdel);
            _unitOfWorkManager.Save();
            return Ok();
        }

        [HttpPost("LockUser")]
        public async Task<ActionResult> LockUser([FromQuery] string username, int duration)
        {
            var user = _userManager.Users.First(c => c.UserName == username);

            if (_userManager.IsLockedOutAsync(user).Result && duration == -2)
            {
                user.LockoutEnd = DateTimeOffset.Now;
            }
            else
            {
                if (duration < 0)
                {
                    //ban permanently
                    user.LockoutEnabled = true;
                    user.LockoutEnd = DateTime.UtcNow.AddYears(99);
                }
                else
                {
                    user.LockoutEnabled = true;
                    user.LockoutEnd = DateTime.UtcNow.AddMinutes(duration);
                }
            }

            await _userManager.UpdateAsync(user);
            _unitOfWorkManager.Save();
            return Ok();
        }

        [AllowAnonymous]
        [HttpPost("LoginUser")]
        public async Task<ActionResult> LoginUser(UserDto user)
        {
            var errors = new List<string>();

            var userFind = await _userManager.FindByEmailAsync(user.Email);
            if (userFind == null)
            {
                errors.Add("Er bestaat geen account met emailadres " + user.Email);
                return BadRequest(errors);
            }

            var result =
                await _signInManager.PasswordSignInAsync(userFind.UserName, user.Password, user.Remember,
                    lockoutOnFailure: true);

            if (result.Succeeded)
            {
                var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
                if (!_userManager.GetUsersForClaimAsync(new Claim("SuperAdmin", "SuperAdmin")).Result
                    .Contains(userFind))
                {
                    if (subdomain != null)
                    {
                        var claimsForSubdomain = _userManager.GetClaimsAsync(userFind).Result.FirstOrDefault(c => c.Type == subdomain);
                        if (claimsForSubdomain == null){
                            await _signInManager.SignOutAsync();
                            errors.Add("account niet gekend op dit platform");
                            return BadRequest(errors);
                        }
                    }
                }

//                _logger.LogInformation("User logged in.");
                _unitOfWorkManager.Save();

                string jwtToken = string.Empty;
                string displayName = string.Empty;
                if (HttpContext.Request.Headers["Device"] == "android")
                {
                    jwtToken = GenerateToken(user.Email);
                    if (_userManager.IsUserOrganisation(userFind))
                    {
                        displayName = userFind.FirmName;
                    }
                    else
                    {
                        displayName = $"{userFind.FirstName} {userFind.LastName}";
                    }
                }
                
                return Ok(
                    new
                    {
                        Token = jwtToken,
                        UserName = userFind.UserName,
                        DisplayName = displayName,
                        user.Email
                    }
                );
            }

            if (result.IsLockedOut)
            {
//                _logger.LogWarning("User account locked out.");
                errors.Add("Dit account is geblokkeerd.");
                return BadRequest(errors);
            }

            if (result.IsNotAllowed)
            {
//                _logger.LogWarning("User account not confirmed.");
                errors.Add("Dit mailadres is nog niet bevestigd. Kijk email na.");
                return BadRequest(errors);
            }
            else
            {
                errors.Add("Geen geldige poging.");
                return BadRequest(errors);
            }
        }

        [AllowAnonymous]
        [HttpPost("RegisterUser")]
        public async Task<ActionResult> RegisterUser(
            UserDto user,
            [FromServices] IServiceProvider serviceProvider
        )
        {
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            var errors = new List<string>();

            if (user.Exists)
            {
                var existsUser = _userManager.Users.First(x => x.Email == user.Email);
                var userByEmail = _userManager.FindByEmailAsync(user.Email);
                //Login and add claim 
                var resultExists =
                    await _signInManager.PasswordSignInAsync(existsUser.UserName, user.Password, false,
                        lockoutOnFailure: true);
//                _logger.LogInformation("User logged in.");
                if (resultExists.Succeeded)
                {
                    await _userManager.AddClaimAsync(existsUser, new Claim(subdomain, "User"));
                    _unitOfWorkManager.Save();
                    RedirectToPage("./RegisterConfirmation");
                    return Ok(new {DisplayName = user.FirstName + " " + user.LastName});
                }
                else
                {
                    errors.Add("login is niet gelukt, kijk invoer na.");
                    return BadRequest(errors);
                }
            }
            else
            {
                var userName = user.UserName;
                var countUser = _userManager.Users.Where(c => c.UserName.Contains(user.UserName)).Count();
                if (countUser > 0)
                {
                    userName = userName + countUser;
                }

                var sex = "";
                if (user.SexM)
                {
                    sex = "Man";
                }
                else if (user.SexV)
                {
                    sex = "Vrouw";
                }

                var newUser = new User
                {
                    UserName = userName,
                    Email = user.Email,
                    FirstName = user.FirstName,
                    LastName = user.LastName,
                    FirmName = user.FirmName,
                    VATNumber = user.Vat,
                    BirthDay = user.BirthDate,
                    Sex = sex,
                    ZipCode = user.Zipcode,
                    SecurityStamp = Guid.NewGuid().ToString(),
                };


                //Standard claim= platform + user/organisation
                var claimType = "";
                if (subdomain == null)
                {
                    claimType = "main";
                }
                else
                {
                    claimType = subdomain;
                }

                var claimValue = "User";
                if (user.Organisation)
                {
                    claimValue = "Organisation";
                }

                var result = await _userManager.CreateAsync(newUser, user.Password);

                foreach (var error in result.Errors)
                {
                    errors.Add(error.Description);
                }

                
                if (result.Succeeded)
                {
                    // Add user to queue to match votes later on
                    if (subdomain != null)
                    {
                        using (var scope = serviceProvider.CreateScope())
                        {
                            var platformManager = scope.ServiceProvider.GetService<IPlatformManager>();
                            var voteSpreadTask = (VoteSpreadTask) scope.ServiceProvider.GetService<IScheduledTask>();

                            var platform = platformManager.GetPlatformByTenant(subdomain);

                            Queue<User> usersQueue;

                            if (voteSpreadTask.UserQueues.ContainsKey(platform))
                            {
                                usersQueue = voteSpreadTask.UserQueues[platform];
                            }
                            else
                            {
                                usersQueue = new Queue<User>();
                                usersQueue.Enqueue(newUser);
                            }

                            voteSpreadTask.UserQueues.Add(platform, usersQueue);
                        }
                    }

//                    _logger.LogInformation("User created a new account with password.");

                    await _userManager.AddClaimAsync(newUser, new Claim(claimType, claimValue));

                    /*if (subdomain != null)
                    {
                        await _userManager.AddClaimAsync(newUser, new Claim(subdomain, subdomain));
                    }*/

                    var code = await _userManager.GenerateEmailConfirmationTokenAsync(newUser);
                    var callbackUrl = Url.Page(
                        "/Account/ConfirmEmail",
                        pageHandler: null,
                        values: new {area = "Identity", userId = newUser.Id, code = code},
                        protocol: Request.Scheme);

                    await _emailSender.SendEmailAsync(user.Email, "Confirm your email",
                        $"Bevestig uw account door <a href='{HtmlEncoder.Default.Encode(callbackUrl)}'>hier</a> te klikken.");
                    _unitOfWorkManager.Save();
                    return Ok(new {DisplayName = user.FirstName + " " + user.LastName});
                }

                return BadRequest(errors);
            }
        }

        [HttpGet("Moderators/{id}")]
        public IActionResult GetModerators(int id, [FromServices] IProjectManager projectManager)
        {
            //Get platform 
            string platform = GetSubdomain(HttpContext.Request.Host.ToString());
            var mods = _userManager.GetUsersForClaimAsync(new Claim(platform, "Moderator")).Result;

            List<User> projectMods = null;
            if (id != -1)
            {
                projectMods = projectManager.GetProject(id).Moderators.Select(m => m.User).ToList();
            }

            List<UserViewModel> modsVm = new List<UserViewModel>();
            foreach (User mod in mods)
            {
                UserViewModel userVm = new UserViewModel()
                {
                    FirstName = mod.FirstName,
                    LastName = mod.LastName,
                    Email = mod.Email,
                    UserName = mod.UserName
                };

                if (projectMods != null)
                {
                    userVm.IsMod = projectMods.Contains(mod);
                }

                modsVm.Add(userVm);
            }

            return Ok(modsVm);
        }
        
        [HttpGet("Users")]
        public IActionResult GetUsers([FromServices] IProjectManager projectManager)
        {
            //Get platform 
            string platform = GetSubdomain(HttpContext.Request.Host.ToString());
            var users = _userManager.Users.ToList();
            List<User> admins = null;
            if (platform != null)
            {
                admins = _userManager.GetUsersForClaimAsync(new Claim(platform, "Admin")).Result.ToList();
            }
           

            List<UserViewModel> usersVm = new List<UserViewModel>();
            foreach (User user in users)
            {
                UserViewModel userVm = new UserViewModel()
                {
                    FirstName = user.FirstName,
                    LastName = user.LastName,
                    Email = user.Email,
                    UserName = user.UserName
                };

                if (admins != null && admins.Contains(user))
                {
                    userVm.IsMod = true;
                }

                if (_userManager.IsUserOrganisation(user))
                {
                    userVm.FirmName = user.FirmName;
                    userVm.IsOrganistation = true;
                }

                usersVm.Add(userVm);
            }

            return Ok(usersVm);
        }

        // Method to gain a new Jwt token from an outdated one. 
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        [HttpGet("RefreshToken")]
        public async Task<IActionResult> RefreshToken()
        {
            String secretInput = null;
            String email = null;
            if (HttpContext.User.HasClaim(c => c.Type == JwtRegisteredClaimNames.Jti))
            {
                try
                {
                    email = HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value;
                    secretInput = HttpContext.User.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Jti)
                        .Value;
                    Console.WriteLine(secretInput + email);
                }
                catch (NullReferenceException e)
                {
                    return BadRequest(e.StackTrace);
                }

                var userName = await _userManager.FindByEmailAsync(email);

                if (userName == null)
                {
                    return BadRequest("User not found");
                }

                if (await _signInManager.ValidateSecurityStampAsync(userName, secretInput))
                {
                    User user = await _userManager.FindByEmailAsync(email);
                    string jwtToken = GenerateToken(email);
                    return Ok(new
                        {Token = jwtToken, UserName = user.FirstName + " " + user.LastName, Email = user.Email});
                }
            }

            return BadRequest("Invalid token modelstate");
        }


        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> LogoutEverywhere()
        {
            string email = null;
            try
            {
                email = HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value;
            }
            catch (Exception e)
            {
                return BadRequest("Not a valid JWT" + e);
            }

            IdentityResult result =
                await _userManager.UpdateSecurityStampAsync(await _userManager.FindByEmailAsync(email));
            if (result == IdentityResult.Success)
            {
                return Ok();
            }

            return BadRequest("Not a valid JWT");
        }

        private string GenerateToken(string email)
        {
            var claims = new Claim[]
            {
                new Claim("Email", email),
                new Claim(JwtRegisteredClaimNames.Nbf, new DateTimeOffset(DateTime.Now).ToUnixTimeSeconds().ToString()),
                new Claim(JwtRegisteredClaimNames.Exp,
                    new DateTimeOffset(DateTime.Now.AddHours(4)).ToUnixTimeSeconds().ToString()),
                new Claim(JwtRegisteredClaimNames.Jti, _userManager.FindByEmailAsync(email).Result.SecurityStamp)
            };
            SymmetricSecurityKey symmetricSecurityKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes( /*TODO: KEY OPHALEN UIT CONFIG: Configuration["Authentication:Jwt:Bytes"]*/
                    "zevenhonderzwevendebeestenzwermeninhetrond"));
            SigningCredentials signingCredentials =
                new SigningCredentials(symmetricSecurityKey, SecurityAlgorithms.HmacSha256);

            JwtHeader jwtHeader = new JwtHeader(signingCredentials);
            JwtPayload jwtPayload = new JwtPayload(claims);
            JwtSecurityToken token = new JwtSecurityToken(jwtHeader, jwtPayload);
            return new JwtSecurityTokenHandler().WriteToken(token);
        }
        
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        [HttpGet("userInfo")]
        public IActionResult GetUserInfo()
        {
            User user = _userManager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            var userInfo = new UserInfoDTO()
            {
                UserName = user.UserName,
                FirstName = user.FirstName,
                LastName = user.LastName,
                Email = user.Email,
                PhoneNumber = user.PhoneNumber,
                Zipcode = user.ZipCode
               
            };
            return Ok(userInfo);

        }
        
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        [HttpPost("userAdjust")]
        public IActionResult PostUserInfo([FromBody] UserInfoDTO userInfo)
        {
            User user = _userManager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;


            user.ZipCode = userInfo.Zipcode;
            user.LastName = userInfo.LastName;
            user.FirstName = userInfo.FirstName;
            user.Email = userInfo.Email;
            user.PhoneNumber = userInfo.PhoneNumber;

            _userManager.UpdateAsync(user);
            
            _unitOfWorkManager.Save();
            
            return Ok();

        }
        
    }
    
    
}