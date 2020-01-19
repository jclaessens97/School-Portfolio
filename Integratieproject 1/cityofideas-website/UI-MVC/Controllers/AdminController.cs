using COI.BL;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Models.Users;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using static COI.UI_MVC.Util.Util;


namespace COI.UI_MVC.Controllers
{
    //[OnlyAdminAndAbove]
    [Route("[controller]/[action]")]
    public class AdminController : Controller
    {
        private readonly UserManager<User> _userManager;
        private readonly IVerifyRequestManager _verifyRequestManager;
        private readonly IPlatformRequestManager _platformRequestManager;

        public AdminController(
            UserManager<User> userManager,
            [FromServices] IVerifyRequestManager verifyRequestManager,
            [FromServices] IPlatformRequestManager platformRequestManager
        )
        {
            _userManager = userManager;
            _verifyRequestManager = verifyRequestManager;
            _platformRequestManager = platformRequestManager;
        }

        // GET
        public IActionResult Dashboard(
            [FromServices] IPlatformManager platformManager,
            [FromServices] IProjectManager projectManager,
            [FromServices] IIdeationManager ideationManager,
            [FromServices] UserManager<User> userManager
        )
        {
            ViewBag.TotalPlatforms = platformManager.GetPlatforms().Count();
            ViewBag.TotalProjects = projectManager.GetProjects().Count();
            ViewBag.TotalIdeations = ideationManager.GetIdeations().Count();

            ViewBag.TotalVotes = ideationManager.GetTotalVoteCount();
            ViewBag.TotalUsers = userManager.Users.Count();
            ViewBag.TotalOrganisations = userManager.GetUsersForClaimAsync(new Claim("Organisation", "Organisation")).Result.Count;

            return View();
        }
        
        // GET
        public IActionResult UserPanel(string search)
        {
            ViewData["Message"] = "Panel to manage users.";
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());

            List<UsersWithClaims> usersWithClaims = new List<UsersWithClaims>();

            //Get all users
            var users = _userManager.Users.ToList();

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
                            FirmName = user.FirmName
                        });
                    }
                }               
            }
            return View(usersWithClaims.OrderBy(o => o.Username).ToList());
        }

        public IActionResult RequestPanel()

        {
            ViewData["Message"] = "Panel to manage verifyrequests.";
            return View(_verifyRequestManager.Get());
        }

        public IActionResult PlatformRequestPanel()
        {
            ViewData["Message"] = "Panel to manage platform requests.";
            return View(_platformRequestManager.Get());
        }

        public IActionResult OrganisationPanel()

        {
            ViewData["Message"] = "Panel to manage organisations.";
            
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            List<UsersWithClaims> usersWithClaims = new List<UsersWithClaims>();

            //Get all users
            var users = _userManager.Users.ToList();

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
                            FirmName = user.FirmName
                        });
                    }
                }               
            }

            var organisations = usersWithClaims.Find(c => c.CurrentClaim.Contains("Organisation"));

            return View(organisations);
        }

        public IActionResult ReportPanel()
        {
            return View();
        }
    }
}