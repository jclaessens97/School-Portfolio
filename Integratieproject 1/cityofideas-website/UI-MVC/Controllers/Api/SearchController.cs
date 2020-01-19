using COI.BL;
using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;

namespace UI_MVC.Controllers.Api
{
    [Route("api/[controller]")]
    [ApiController]
    public class SearchController : ControllerBase
    {
        private readonly IPlatformManager _platformManager;
        private readonly IProjectManager _projectManager;
        private readonly IIdeationManager _ideationManager;

        public SearchController(
            [FromServices] IPlatformManager platformManager,
            [FromServices] IProjectManager projectManager,
            [FromServices] IIdeationManager ideationManager,
            [FromServices] IHostingEnvironment hostingEnvironment
        )
        {
            _platformManager = platformManager;
            _projectManager = projectManager;
            _ideationManager = ideationManager;
        }

        [HttpGet("platforms")]
        public IActionResult Platforms([FromQuery] string query)
        {
            var platforms = _platformManager.SearchPlatforms(query, Constants.AutoCompleteMinLength);

            foreach (var platform in platforms)
            {
                platform.Tenant = $"{HttpContext.Request.Scheme}://{platform.Tenant}.{HttpContext.Request.Host}";
            }

            return Ok(platforms);
        }

        [HttpGet("projects")]
        public IActionResult Projects([FromQuery] string query)
        {
            var projects = _projectManager.SearchProjects(query, Constants.AutoCompleteMinLength);

            foreach (var project in projects)
            {
                project.Platform.Tenant = $"{HttpContext.Request.Scheme}://{project.Platform.Tenant}.{HttpContext.Request.Host}";
            }

            return Ok(projects);
        }

        [HttpGet("ideations")]
        public IActionResult Ideations([FromQuery] string query)
        {
            var ideations = _ideationManager.SearchIdeations(query, Constants.AutoCompleteMinLength);

            foreach (var ideation in ideations)
            {
                ideation.Project.Platform.Tenant = $"{HttpContext.Request.Scheme}://{ideation.Project.Platform.Tenant}.{HttpContext.Request.Host}";
            }

            return Ok(ideations);
        }
        
        [HttpGet("app/ideations/{query}")]
        public IActionResult IdeationsApp(string query)
        {
            var ideations = _ideationManager.SearchIdeations(query, Constants.AutoCompleteMinLength);
            return Ok(ideations);
        }
        
        [HttpGet("app/projects/{query}")]
        public IActionResult ProjectsApp( string query)
        {
            var projects = _projectManager.SearchProjects(query, Constants.AutoCompleteMinLength);
            return Ok(projects);
        }
        
        [HttpGet("app/platforms/{query}")]
        public IActionResult PlatformsApp([FromQuery] string query)
        {
            var platforms = _platformManager.SearchPlatforms(query, Constants.AutoCompleteMinLength);
            return Ok(platforms);
        }
    }
}