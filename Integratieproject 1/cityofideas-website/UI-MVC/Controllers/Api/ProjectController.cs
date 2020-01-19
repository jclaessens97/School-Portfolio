using COI.BL;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Models.dto;
using COI.UI_MVC.Services;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Controllers.API
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProjectController : ControllerBase
    {
        private readonly UserManager<User> _userManager;
        private readonly IProjectManager _projectManager;
        private readonly IPlatformManager _platformManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly IFileUploader _fileUploader;

        public ProjectController(
            UserManager<User> userManager,
            [FromServices] IProjectManager projectManager,
            [FromServices] IPlatformManager platformManager,
            [FromServices] IFileUploader fileUploader, 
            [FromServices] UnitOfWorkManager unitOfWorkManager
        )
        {
            _userManager = userManager;
            _projectManager = projectManager;
            _platformManager = platformManager;
            _fileUploader = fileUploader;
            _unitOfWorkManager = unitOfWorkManager;
        }

        // GET: api/Project
        [HttpGet("All")]
        public IActionResult Get()
        {
            var projects = _projectManager.GetProjects();

            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            if (subdomain != null)
            {
                projects = projects.Where(p => p.Platform.Tenant == subdomain).AsEnumerable();
            }
            
            if (projects == null || projects.Count() == 0)
            {
                return NotFound("er zijn geen projecten teruggevonden");
            }

            return Ok(projects);
        }

        // GET: api/Project/5
        [HttpGet("{id}")]
        public IActionResult Get(int id)
        {
            var project = _projectManager.GetProject(id);

            if (project == null)
            {
                return NotFound("geen project met deze id");
            }    

            return Ok(project);
        }

        // POST: api/Project
        [HttpPost]
        public async Task<IActionResult> Post([FromForm] ProjectDTO projectDto)
        {
            if (ModelState.IsValid)
            {
                var phases = new List<Phase>();

                projectDto.Phases.ForEach(phaseDto =>
                {
                    var phase = new Phase()
                    {
                        Number = phaseDto.Number,
                        Title = phaseDto.Title,
                        Description = phaseDto.Description,
                    };

                    phases.Add(phase);
                });

                var fileName = Util.Util.GenerateDataStoreObjectName(projectDto.Logo.FileName);
                var imageObj = new Media()
                {
                    Name = fileName,
                    Url = await _fileUploader.UploadFile(fileName, "project-logos", projectDto.Logo),
                };

                var platform = _platformManager.GetPlatformByTenant(projectDto.PlatformTenant);

                
                var project = new Project()
                {
                    Title = projectDto.Title,
                    Goal = projectDto.Goal,
                    Logo = imageObj,
                    Phases = phases,
                    Platform = platform,
                    StartDate = projectDto.StartDate,
                    EndDate = projectDto.EndDate,
                    CurrentPhaseNumber = projectDto.CurrentPhase
                };

                if (projectDto.CurrentPhase > projectDto.Phases.Count)
                {
                    project.CurrentPhaseNumber = 0;
                }

                if (projectDto.Moderators != null)
                {
                    foreach (string moderatorUserName in projectDto.Moderators)
                    {
                        User mod = _userManager.FindByNameAsync(moderatorUserName).Result;
                        if (mod != null)
                        {
                            ProjectModerator moderator = new ProjectModerator()
                            {
                                Project = project,
                                User = mod
                            };
                            project.Moderators.Add(moderator);
                        }
                    }
                }
                
                platform.Projects.Add(project);
                _projectManager.AddProject(project);
                _unitOfWorkManager.Save();
                return Created("/project/details/" + project.ProjectId, new {id = project.ProjectId});
            }

            return StatusCode(400);
        }
        

        [HttpPut("{id}")]
        public async Task<IActionResult> Put(int id, [FromForm] ProjectDTO projectDto)
        {
            if (ModelState.IsValid)
            { 
                var project = _projectManager.GetProject(id);

                for (int i = 0; i < projectDto.Phases.Count; i++)
                {
                    if ((project.Phases.Count < i + 1))
                    {
                        var phase = new Phase()
                        {
                            Number = projectDto.Phases[i].Number,
                            Title = projectDto.Phases[i].Title,
                            Description = projectDto.Phases[i].Description,
                        };
                        project.Phases.Add(phase);
                    }
                    else
                    {
                        project.Phases[i].Title = projectDto.Phases[i].Title;
                        project.Phases[i].Description = projectDto.Phases[i].Description;
                    }
                }

                project.Title = projectDto.Title;
                project.Goal = projectDto.Goal;
                project.StartDate = projectDto.StartDate;
                project.EndDate = projectDto.EndDate;
                project.CurrentPhaseNumber = projectDto.CurrentPhase;
                
                if (projectDto.CurrentPhase > projectDto.Phases.Count)
                {
                    project.CurrentPhaseNumber = 1;
                }

                if (projectDto.LogoChanged)
                {
                    var fileName = Util.Util.GenerateDataStoreObjectName(projectDto.Title);
                    var imageObj = new Media()
                    {
                        Name = fileName,
                        Url = await _fileUploader.UploadFile(fileName, "project-logos", projectDto.Logo),
                    };
                    project.Logo = imageObj;
                }
                
                project.Moderators = new List<ProjectModerator>();
                if (projectDto.Moderators != null)
                {
                    foreach (string moderatorUserName in projectDto.Moderators)
                    {
                        User mod = _userManager.FindByNameAsync(moderatorUserName).Result;
                        if (mod != null)
                        {
                            ProjectModerator moderator = new ProjectModerator()
                            {
                                Project = project,
                                User = mod
                            };
                            project.Moderators.Add(moderator);
                        }
                    }
                }
                
                _unitOfWorkManager.Save();
                _projectManager.UpdateProject(project);
                return Created("/project/details/" + project.ProjectId, new {id = project.ProjectId});
            }

            return StatusCode(400);
        }


        [HttpGet("stats/{projectId}")]
        public IActionResult GetProjectStats(int projectId)
        {
            int commentCount = _projectManager.GetCommentCount(projectId);
            int voteCount = _projectManager.GetVoteCount(projectId);
            int ideationReplyCount = _projectManager.GetIdeationReplyCount(projectId);
            return Ok(new {voteCount, ideationReplyCount, commentCount});
        }
    }
}
