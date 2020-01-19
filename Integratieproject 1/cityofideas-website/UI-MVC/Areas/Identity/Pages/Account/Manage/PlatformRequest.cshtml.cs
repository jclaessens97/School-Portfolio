using System;
using System.ComponentModel.DataAnnotations;
using System.Threading.Tasks;
using COI.BL;
using COI.BL.Domain.User;
using Microsoft.AspNetCore.Http.Extensions;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace COI.UI_MVC.Areas.Identity.Pages.Account.Manage
{
    public class PlatformRequest : PageModel
    {
        private readonly UserManager<User> _userManager;

        private readonly IPlatformRequestManager _platformRequestManager;

        public PlatformRequest(UserManager<User> userManager , IPlatformRequestManager platformRequestManager)
        {
            _userManager = userManager;
            _platformRequestManager = platformRequestManager;
        }
        
        [BindProperty] public InputModel Input { get; set; }

        public class InputModel
        {
            [DataType(DataType.Text)]
            [Display(Name = "Reden voor aanvraag platform (optioneel)")]
            public string ReasonPlatformRequest { get; set; }
        }

        public async Task<IActionResult> OnPostAsync()
        {
            
            if (!ModelState.IsValid)
            {
                return Page();
            }

            var user = await _userManager.GetUserAsync(User);
            var userId = _userManager.GetUserId(User);
            var userName = user.UserName;
            var reason = Input.ReasonPlatformRequest;

            if (reason == null)
            {
                reason = "Wil platform aanmaken voor mijn organisatie aub.";
            }
            
            BL.Domain.User.PlatformRequest platformRequest = new BL.Domain.User.PlatformRequest()
            {
                Date = DateTime.Now,
                Reason = reason,
                UserId = userId,
                OrganisationName = userName
            };
            _platformRequestManager.CreatePlatformRequest(platformRequest);
            return RedirectToPage();
        }
    }
}