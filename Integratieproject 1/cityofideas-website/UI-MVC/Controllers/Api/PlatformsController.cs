using System.Collections.Generic;
using COI.BL;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Platform;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Models;
using COI.UI_MVC.Services;
using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Extensions;
using Microsoft.AspNetCore.Identity;

namespace COI.UI_MVC.Controllers.Api
{
    [Route("api/[controller]")]
    [ApiController]
    public class PlatformsController : ControllerBase
    {
        private readonly IPlatformManager _platformManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly IFileUploader _fileUploader;

        public PlatformsController(
            [FromServices] IPlatformManager platformManager,
            [FromServices] IFileUploader imageUploader, 
            [FromServices] UnitOfWorkManager unitOfWorkManager
        )
        {
            _platformManager = platformManager;
            _fileUploader = imageUploader;
            _unitOfWorkManager = unitOfWorkManager;
        }

        [HttpPost]
        [OnlySuperAdmin]
        public async Task<IActionResult> Create([FromForm] PlatformViewModel platformViewModel,[FromServices] UserManager<User> userManager)
        {
            if (ModelState.IsValid)
            {
                var stylesheet = new ColorScheme()
                {
                    SocialBarColor = platformViewModel.SocialBarColor,
                    NavBarColor = platformViewModel.NavbarColor,
                    BannerColor = platformViewModel.BannerColor,
                    ButtonColor = platformViewModel.ButtonColor,
                    ButtonTextColor = platformViewModel.ButtonTextColor,
                    TextColor = platformViewModel.TextColor,
                    BodyColor = platformViewModel.BodyColor,
                };

                Media logoImageObj = null; 
                if (platformViewModel.Logo != null)
                {
                    var logoFileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Logo.FileName);
                    logoImageObj = new Media()
                    {
                        Name = logoFileName,
                        Url = await _fileUploader.UploadFile(logoFileName, "platform-logos", platformViewModel.Logo),
                    };
                }
                

                
                Media bannerImageObj = null;
                if (platformViewModel.Banner != null)
                {
                    var bannerFileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Banner.FileName);
                    bannerImageObj = new Media()
                    {
                        Name = bannerFileName,
                        Url = await _fileUploader.UploadFile(bannerFileName, "platform-banners", platformViewModel.Banner),
                    };
                }
                

                var platform = new Platform()
                {
                    Name = platformViewModel.Name,
                    Tenant = platformViewModel.Tenant,
                    Logo = logoImageObj,
                    Banner = bannerImageObj,
                    Description = platformViewModel.Description,
                    ColorScheme = stylesheet,
                };

                List<User> admins = userManager.GetUsersForClaimAsync(new Claim(platform.Tenant, "Admin")).Result.ToList();

                foreach (User admin in admins)
                {
                    await userManager.ReplaceClaimAsync(admin, new Claim(platform.Tenant, "Admin"),
                        new Claim(platform.Tenant, "User"));
                }
                
                
                if (platformViewModel.Admins != null)
                {
                    
                    foreach (string AdminUserName in platformViewModel.Admins)
                    {
                        User newAdmin = userManager.FindByNameAsync(AdminUserName).Result;
                        //Kijken of user al bestaat op het platform
                        var claimsForSubdomain = userManager.GetClaimsAsync(newAdmin).Result.FirstOrDefault(c => c.Type == platform.Tenant); //Subdomain is het subdomain waarop je zit
                        if (claimsForSubdomain == null){
                            //User heeft nog geen claim op dit platform => claim toewijzen dus! EN DIRECT ADMIN TOEWIJZEN
                            await userManager.AddClaimAsync(newAdmin, new Claim(platform.Tenant, "Admin"));
                        } 
                        else
                        {
                            
                            //User heeft al een claim op dit platform -> Claim verwijderen is Admin Vervangen door User
                            /*await userManager.ReplaceClaimAsync(newAdmin, new Claim(claimsForSubdomain.Type, claimsForSubdomain.Value),
                                new Claim(claimsForSubdomain.Type, "User"));*/

                            //User heeft al een claim op dit platform en wordt nu admin -> Claim verwijderen is User Vervangen door Admin
                            await userManager.ReplaceClaimAsync(newAdmin, new Claim(claimsForSubdomain.Type, claimsForSubdomain.Value),
                                new Claim(claimsForSubdomain.Type, "Admin"));
	
                        }
                    }
                }
                

                _platformManager.AddPlatform(platform);
                _unitOfWorkManager.Save();
                return Ok();
            }
            
            

            return StatusCode(400);
        }

        [HttpPut("{id}")]
        [OnlySuperAdmin]
        public async Task<IActionResult> Edit(int id, [FromForm] PlatformViewModel platformViewModel,[FromServices] UserManager<User> userManager)
        {
            if (ModelState.IsValid)
            {
                var platform = _platformManager.GetPlatform(id);

                platform.Name = platformViewModel.Name;
                platform.Tenant = platformViewModel.Tenant;
                platform.Description = platformViewModel.Description;

                platform.ColorScheme.SocialBarColor = platformViewModel.SocialBarColor;
                platform.ColorScheme.BannerColor = platformViewModel.BannerColor;
                platform.ColorScheme.ButtonColor = platformViewModel.ButtonColor;
                platform.ColorScheme.ButtonTextColor = platformViewModel.ButtonTextColor;
                platform.ColorScheme.TextColor = platformViewModel.TextColor;
                platform.ColorScheme.BodyColor = platformViewModel.BodyColor;

                if (platformViewModel.LogoChanged)
                {
                    var fileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Name);
                    var imageObj = new Media()
                    {
                        Name = fileName,
                        Url = await _fileUploader.UploadFile(fileName, "platform-logos", platformViewModel.Logo),
                    };
                    platform.Logo = imageObj;
                }

                if (platformViewModel.BannerChanged)
                {
                    var fileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Name);
                    var imageObj = new Media()
                    {
                        Name = fileName,
                        Url = await _fileUploader.UploadFile(fileName, "platform-banners", platformViewModel.Banner),
                    };
                    platform.Banner = imageObj;
                }
                
                 List<User> admins = userManager.GetUsersForClaimAsync(new Claim(platform.Tenant, "Admin")).Result.ToList();

                foreach (User admin in admins)
                {
                    await userManager.ReplaceClaimAsync(admin, new Claim(platform.Tenant, "Admin"),
                        new Claim(platform.Tenant, "User"));
                }
                
                List<User> admins2 = userManager.GetUsersForClaimAsync(new Claim(platform.Tenant, "Admin")).Result.ToList();
                
                if (platformViewModel.Admins != null)
                {
                    
                    foreach (string AdminUserName in platformViewModel.Admins)
                    {
                        User newAdmin = userManager.FindByNameAsync(AdminUserName).Result;
                        //Kijken of user al bestaat op het platform
                        var claimsForSubdomain = userManager.GetClaimsAsync(newAdmin).Result.FirstOrDefault(c => c.Type == platform.Tenant); //Subdomain is het subdomain waarop je zit
                        if (claimsForSubdomain == null){
                            //User heeft nog geen claim op dit platform => claim toewijzen dus! EN DIRECT ADMIN TOEWIJZEN
                            await userManager.AddClaimAsync(newAdmin, new Claim(platform.Tenant, "Admin"));
                        } 
                        else
                        {
                            
                            //User heeft al een claim op dit platform -> Claim verwijderen is Admin Vervangen door User
                            /*await userManager.ReplaceClaimAsync(newAdmin, new Claim(claimsForSubdomain.Type, claimsForSubdomain.Value),
                                new Claim(claimsForSubdomain.Type, "User"));*/

                            //User heeft al een claim op dit platform en wordt nu admin -> Claim verwijderen is User Vervangen door Admin
                            await userManager.ReplaceClaimAsync(newAdmin, new Claim(claimsForSubdomain.Type, claimsForSubdomain.Value),
                                new Claim(claimsForSubdomain.Type, "Admin"));
	
                        }
                    }
                }

                _platformManager.UpdatePlatform(platform);
                _unitOfWorkManager.Save();
                return Ok();
            }

            return StatusCode(400);
        }

        [HttpGet("exists")]
        public IActionResult TenantExists([FromQuery] string tenant)
        {
            var platform = _platformManager.GetPlatformByTenant(tenant);
            if (platform == null)
            {
                return Ok( new { Exists = false });
            }
            else
            {
                return Ok(new { Exists = true });
            }
        }

        [HttpGet("colorscheme/{tenant}")]
        public IActionResult GetColorScheme(string tenant)
        {
            var platform = _platformManager.GetPlatformByTenant(tenant);

            if (platform != null)
            {
                return Ok(platform.ColorScheme);
            }

            return NotFound();
        }
        
        [HttpGet("active/{id}")]
        public IActionResult GetActiveProjects(int id)
        {
            var platform = _platformManager.GetPlatform(id);
            return Ok(platform.ActiveProjects);
        }

        [HttpGet("finished/{id}")]
        public IActionResult GetFinishedProjects(int id)
        {
            var platform = _platformManager.GetPlatform(id);
            var project = platform.FinishedProjects;
            return Ok(project);
        }

        [HttpGet("all")]
        public IActionResult GetAllPlatforms([FromServices] IHostingEnvironment hostingEnvironment)
        {
            var platforms = _platformManager.GetPlatforms();

            platforms.ToList().ForEach((platform) =>
            {
                if (hostingEnvironment.EnvironmentName == "Production")
                {
                    platform.Tenant = $"{platform.Tenant}.{Constants.hostnames[3]}";
                } else
                {
                    platform.Tenant = $"{platform.Tenant}.{Constants.hostnames[1]}";
                }
            });

            return Ok(platforms);
        }

        [HttpGet("{skip}/{take}")]
        public IActionResult GetPlatforms(int skip, int take)
        {
            var platforms = _platformManager.GetPlatforms(skip, take);

            foreach (var platform in platforms)
            {
                platform.Tenant = $"{HttpContext.Request.Scheme}://{platform.Tenant}.{HttpContext.Request.Host}";
            }

            return Ok(platforms);
        }
    }
}