using COI.BL;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Extensions;
using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;

namespace UI_MVC.Controllers
{
    public class ProjectController : Controller
    {
        private readonly IProjectManager _projectManager;
        private readonly UserManager<User> _userManager;

        public ProjectController(
            UserManager<User> userManager,
            [FromServices] IProjectManager projectManager
        )
        {
            _userManager = userManager;
            _projectManager = projectManager;
        }

        [SubdomainOnly]
        public IActionResult Create()
        {
            return View();
        }

        public IActionResult Details(int id, [FromServices] IIoTManager ioTManager)
        {  
            var tenant = Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var project = _projectManager.GetProject(id);
            if (project == null)
            {
                return RedirectToAction("NotFound", "Home");
            }
            
            var subdomain = Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var projectSubdomain = project.Platform.Tenant;
            if (subdomain != projectSubdomain)
            {
                return RedirectToAction("NotFound", "Home");
            }
 
            User user = _userManager.GetUserAsync(User).Result;

            if (user != null)
            {
                ViewBag.IsAdmin = _userManager.IsUserAdminOrAbove(user,tenant);
            }
            else
            {
                ViewBag.IsAdmin = false;
            }

            ViewBag.IoTCount = ioTManager.GetIotCountByProject(project.ProjectId);
            
            return View(project);
        }

        public IActionResult Edit(int id)
        {
            var project = _projectManager.GetProject(id);
            return View(project);
        }

        public IActionResult List()
        {
            var projects = _projectManager.GetProjects();
            var active = new List<Project>();
            var finished = new List<Project>();

            projects.ToList().ForEach((p) =>
            {
                var endDate = p.EndDate;

                if (DateTime.Compare(endDate, DateTime.Now) > 0)
                {
                    finished.Add(p);
                } else
                {
                    active.Add(p);
                }
            });

            ViewBag.Active = active;
            ViewBag.Finished = finished;

            return View();
        }

        [HttpGet]
        public IActionResult CreateIoT(int id)
        {
            return View(id);
        }
    }
}