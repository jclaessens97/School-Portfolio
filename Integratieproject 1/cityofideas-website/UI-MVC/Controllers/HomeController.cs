using COI.BL;
using COI.UI_MVC.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using COI.BL.Domain.Platform;
using Microsoft.AspNetCore.Hosting.Internal;

namespace COI.UI_MVC.Controllers
{
    public class HomeController : Controller
    {
        private readonly IPlatformManager _platformManager;

        public HomeController([FromServices] IPlatformManager platformManager)
        {
            _platformManager = platformManager;
        }

        public IActionResult Index(
            [FromServices] IActivityManager activityManager    
        )
        {
            var platforms = _platformManager.GetPlatforms();

            foreach (var platform in platforms)
            {
                platform.Tenant = $"{HttpContext.Request.Scheme}://{platform.Tenant}.{HttpContext.Request.Host}";
            }

            ViewBag.Platforms = platforms;

            var activities = activityManager.GetActivityFeed();
            var activityViewModels = new List<ActivityViewModel>();
            foreach (var activity in activities.Reverse())
            {
                var vm = new ActivityViewModel(activity);
                activityViewModels.Add(vm);
            }

            ViewBag.Activities = activityViewModels;

            return View();
        }

        [HttpGet("search")]
        public IActionResult Search(
            [FromQuery] string qry,
            [FromServices] IProjectManager projectManager,
            [FromServices] IIdeationManager ideationManager
        )
        {
            ViewBag.Query = qry;

            var platforms = _platformManager.SearchPlatforms(qry);
            var projects = projectManager.SearchProjects(qry);
            var ideations = ideationManager.SearchIdeations(qry);

            foreach (var platform in platforms)
            {
                platform.Tenant = $"{HttpContext.Request.Scheme}://{platform.Tenant}.{HttpContext.Request.Host}";
            }

            foreach (var project in projects)
            {
                if (!project.Platform.Tenant.Contains("http"))
                {
                    project.Platform.Tenant = $"{HttpContext.Request.Scheme}://{project.Platform.Tenant}.{HttpContext.Request.Host}";
                }
            }

            foreach (var ideation in ideations)
            {
                if (!ideation.Project.Platform.Tenant.Contains("http"))
                {
                    ideation.Project.Platform.Tenant = $"{HttpContext.Request.Scheme}://{ideation.Project.Platform.Tenant}.{HttpContext.Request.Host}";
                }
            }

            ViewBag.PlatformResults = platforms.Any() ? platforms : null;
            ViewBag.ProjectResults = projects.Any() ? projects : null;
            ViewBag.IdeationResults = ideations.Any() ? ideations : null;

            return View();
        }

        [HttpGet("platforms")]
        public IActionResult Platforms()
        {
            return View();
        }
        
        [HttpGet("notfound")]
        public IActionResult NotFound()
        {
            return View();
        }
        
        [HttpGet("Contact")]
        public IActionResult Contact()
        {
            ViewData["Message"] = "Uw contactpagina.";
            return View();
        }
        
        [HttpGet("About")]
        public IActionResult About()
        {
            string tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            Platform platform = _platformManager.GetPlatformByTenant(tenant);
            if (platform == null)
            {
                return NotFound();
            }
            ViewData["Message"] = "Over ons";
            return View(platform);
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel {RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier});
        }
    }
}