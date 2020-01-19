using COI.BL;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.BL.Impl;
using COI.DAL;
using COI.UI_MVC.Extensions;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.DependencyInjection;
using static COI.UI_MVC.Util.Util;

namespace UI_MVC.Controllers
{
    public class PlatformController : Controller
    {
        private readonly IPlatformManager _platformManager;
        private readonly IActivityManager _activityManager;
        private readonly UserManager<User> _userManager;
        private static string _subdomain;

        public PlatformController(
            [FromServices] IPlatformManager platformManager,
            [FromServices] IActivityManager activityManager,
            UserManager<User> userManager,
            IHttpContextAccessor httpContextAccessor
        )
        {
            _platformManager = platformManager;
            _activityManager = activityManager;
            _userManager = userManager;
            _subdomain = GetSubdomain(httpContextAccessor.HttpContext.Request.Host.ToString());
        }

        //TODO usermanger meegeven in index of in constructor?! Refactor Jeroen please, vraag aan Sam voor refactor
        public IActionResult Index(string tenant, [FromServices] IIdeationManager ideationManager,
            [FromServices] UserManager<User> userManager,[FromServices] IIoTManager ioTManager)
        {
            var platform = _platformManager.GetPlatformByTenant(tenant);
            if (platform == null)
            {
                return RedirectToAction("NotFound", "Home");
            }

            var activities = _activityManager.GetActivityFeed(platform);
            var activityViewModels = new List<ActivityViewModel>();
            foreach (var activity in activities.Reverse())
            {
                var vm = new ActivityViewModel(activity);
                activityViewModels.Add(vm);
            }

            ViewBag.Activities = activityViewModels;
            ViewBag.IdeationReplyCount = ideationManager.GetIdeationReplyCount(platform.PlatformId);
            ViewBag.CommentCount = ideationManager.GetCommentCount(platform.PlatformId);
            ViewBag.VoteCount = ideationManager.GetTotalVoteCount(platform.PlatformId);
            ViewBag.IoTCount = ioTManager.GetIotCountByPlatform(platform.PlatformId);
            
            ViewBag.ProjectVoteCount = new List<int>();
            ViewBag.ProjectCommentCount = new List<int>();
            ViewBag.ProjectIdeationReplyCount = new List<int>();
            
            var users = userManager.Users;
            var count = 0;
            foreach (var userCount in users)
            {
                if (userManager.GetClaimsAsync(userCount).Result.SingleOrDefault(c => c.Type == _subdomain) != null)
                {
                    count++;
                }
            }
            ViewBag.UserCount = count;
            
            User user = userManager.GetUserAsync(User).Result;

            if (user != null)
            {
		        ViewBag.IsAdmin = userManager.IsUserAdminOrAbove(user,tenant);
            }
            else
            {
                ViewBag.IsAdmin = false;
            }

            return View(platform);
        }

        [OnlySuperAdmin]
        public IActionResult List()
        {
            var platforms = _platformManager.GetPlatforms();
            return View(platforms);
        }

        [OnlySuperAdmin]
        public IActionResult Create()
        {
            return View();
        }

        [OnlySuperAdmin]
        public IActionResult Edit(int id)
        {
            var platform = _platformManager.GetPlatform(id);

            var platformViewModel = new PlatformViewModel()
            {
                PlatformId = platform.PlatformId,
                Name = platform.Name,
                Tenant = platform.Tenant,
                Description = platform.Description,
                LogoUrl = platform.Logo.Url,

                SocialBarColor = platform.ColorScheme.SocialBarColor,
                NavbarColor = platform.ColorScheme.NavBarColor,
                BannerColor = platform.ColorScheme.BannerColor,
                ButtonColor = platform.ColorScheme.ButtonColor,
                ButtonTextColor = platform.ColorScheme.ButtonTextColor,
                TextColor = platform.ColorScheme.TextColor,
                BodyColor = platform.ColorScheme.BodyColor
            };

            return View(platformViewModel);
        }
    }
}