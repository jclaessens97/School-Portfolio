using System;
using COI.BL;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Models.dto;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Platform;
using COI.UI_MVC.Models;
using COI.UI_MVC.Services;

namespace COI.UI_MVC.Controllers.Api
{
    [ApiController]
    [Route("api/platformrequests")]
    public class PlatformRequestController : ControllerBase
    {
        private readonly IPlatformRequestManager _platformRequestManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly UserManager<User> _userManager;
        private readonly IFileUploader _fileUploader;
        private readonly IPlatformManager _platformManager;


        public PlatformRequestController(
            IPlatformRequestManager platformRequestManager,
            UserManager<User> userManager,
            [FromServices] UnitOfWorkManager unitOfWorkManager,
            [FromServices] IFileUploader imageUploader,
            [FromServices] IPlatformManager platformManager)
        {
            _platformRequestManager = platformRequestManager;
            _userManager = userManager;
            _unitOfWorkManager = unitOfWorkManager;
            _fileUploader = imageUploader;
            _platformManager = platformManager;
        }

        // GET
        [HttpGet]
        public IEnumerable<PlatformRequest> Get()
        {
            return _platformRequestManager.Get();
        }

        [HttpPost("AcceptRequest")]
        public IActionResult AcceptRequest(stringDTO val)
        {
            var request = _platformRequestManager.Get().First(c => c.PlatformRequestId == val.id);
            var answerText = val.text;
            request.Accept = true;
            request.Answer = answerText;
            request.Treated = true;

            _platformRequestManager.Update(request);
            _unitOfWorkManager.Save();
            return Ok();
        }

        [HttpPost("DenyRequest")]
        public IActionResult DenyRequest(stringDTO val)
        {
            var request = _platformRequestManager.Get().First(c => c.PlatformRequestId == val.id);
            var answerText = val.text;
            request.Accept = false;
            request.Answer = answerText;
            request.Treated = true;

            _platformRequestManager.Update(request);
            _unitOfWorkManager.Save();
            return Ok();
        }

        [HttpPost("Createplatformrequest")]
        public async Task<IActionResult> Createplatformrequest([FromForm] PlatformViewModel platformViewModel)
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

                var logoFileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Name);
                var logoImageObj = new Media()
                {
                    Name = logoFileName,
                    Url = await _fileUploader.UploadFile(logoFileName, "platform-logos", platformViewModel.Logo),
                };

                var bannerFileName = Util.Util.GenerateDataStoreObjectName(platformViewModel.Name);
                var bannerImageObj = new Media()
                {
                    Name = bannerFileName,
                    Url = await _fileUploader.UploadFile(bannerFileName, "platform-banners", platformViewModel.Banner),
                };

                var platform = new Platform()
                {
                    Name = platformViewModel.Name,
                    Tenant = platformViewModel.Tenant,
                    Logo = logoImageObj,
                    Banner = bannerImageObj,
                    Description = platformViewModel.Description,
                    ColorScheme = stylesheet,
                };

                var user = _userManager.GetUserAsync(User).Result;
                var platformRequest = new PlatformRequest()
                {
                    Accept = false,
                    Date = DateTime.Now,
                    OrganisationName = user.FirmName,
                    Reason = platformViewModel.PlatformReason,
                    Treated = false,
                    UserId = user.Id,
                    Platform = platform
                };

                _platformRequestManager.CreatePlatformRequest(platformRequest);
//                _platformManager.AddPlatform(platform);
                _unitOfWorkManager.Save();
                return Ok();
            }
            return StatusCode(400);
        }
    }
}