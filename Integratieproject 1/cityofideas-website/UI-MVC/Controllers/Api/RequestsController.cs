using COI.BL;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Models.dto;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;

namespace COI.UI_MVC.Controllers.Api
{
    [ApiController]
    [Route("api/requests")]
   // [OnlyAdminAndAbove]
    public class RequestsController : ControllerBase
    {
        private readonly IVerifyRequestManager _verifyRequestManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly UserManager<User> _userManager;

        public RequestsController(IVerifyRequestManager verifyRequestManager, UserManager<User> userManager,
            [FromServices] UnitOfWorkManager unitOfWorkManager)
        {
            _verifyRequestManager = verifyRequestManager;
            _userManager = userManager;
            _unitOfWorkManager = unitOfWorkManager;
        }

        // GET
        [HttpGet]
        public IEnumerable<VerifyRequest> Get()
        {
            return _verifyRequestManager.Get();
        }

        [HttpPost("AcceptRequest")]
        public IActionResult AcceptRequest(stringDTO val)
        {
            var request = _verifyRequestManager.Get().First(c => c.RequestId == val.id);
            var answerText = val.text;
            request.Accept = true;
            request.Answer = answerText;
            request.Treated = true;
            var user = _userManager.Users.First(x => x.Id == request.UserId);
            
            _verifyRequestManager.Update(request);
            _userManager.AddClaimAsync(user, new Claim("Verified", "Verified"));
            _unitOfWorkManager.Save();
            return Ok();
        }

        [HttpPost("DenyRequest")]
        public IActionResult DenyRequest(stringDTO val)
        {
            var request = _verifyRequestManager.Get().First(c => c.RequestId == val.id);
            var answerText = val.text;
            request.Accept = false;
            request.Answer = answerText;
            request.Treated = true;

            _verifyRequestManager.Update(request);
            _unitOfWorkManager.Save();
            return Ok();
        }
    }
}