using COI.BL;
using COI.UI_MVC.Areas.Identity.Pages.Account;
using COI.UI_MVC.Areas.Identity.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace COI.UI_MVC.Controllers.Api
{

    [ApiController]
    [Route("api/[controller]")]
    public class AccountController : ControllerBase
    {
        private readonly AuthRepository _repo;
        private readonly UnitOfWorkManager _unitOfWorkManager;

        public AccountController(
            AuthRepository repo, 
            [FromServices] UnitOfWorkManager unitOfWorkManager)
        {
            _repo = repo;
            _unitOfWorkManager = unitOfWorkManager;
        }

        [AllowAnonymous]
        [HttpPost("ForgotPassword")]
        public async Task<IActionResult> ForgotPassword(ForgotPasswordModel.InputModel model)
        {
            bool forgot = await _repo.ForgotPassword(model, Url, Request);
            _unitOfWorkManager.Save();

            if (forgot)
            {
                return Ok();
            }

            return BadRequest("email is not registered or confirmed");
        }

        public async Task<IActionResult> LogoutEverywhere()
        {
            string email = null;
            try
            {
                email = HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value;
            }
            catch (Exception e)
            {
                return BadRequest("Not a valid JWT"+e);
            }

            IdentityResult result = await _repo.LogoutEveryWhere(email);
            if (result == IdentityResult.Success)
            {
                return Ok();
            }

            return BadRequest("Not a valid JWT");
        }
    }
}