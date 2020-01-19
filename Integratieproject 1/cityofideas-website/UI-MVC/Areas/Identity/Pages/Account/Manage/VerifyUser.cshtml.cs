using System;
using System.ComponentModel.DataAnnotations;
using System.Threading.Tasks;
using COI.BL;
using COI.BL.Domain.User;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace COI.UI_MVC.Areas.Identity.Pages.Account.Manage
{
    public class VerifyUser : PageModel
    {
        private readonly UserManager<User> _userManager;
        private readonly IVerifyRequestManager _verifyRequestManager;

        public VerifyUser(UserManager<User> userManager, IVerifyRequestManager verifyRequestManager)
        {
            _userManager = userManager;
            _verifyRequestManager = verifyRequestManager;
        }

        [BindProperty] public InputModel Input { get; set; }

        public class InputModel
        {
            [DataType(DataType.Text)]
            [Display(Name = "reden voor verificatie (optioneel)")]
            public string ReasonVerifyUser { get; set; }
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
            var reason = Input.ReasonVerifyUser;

            if (reason == null)
            {
                reason = "Wil geverifieerd worden aub.";
            }
            
            VerifyRequest verifyRequest = new VerifyRequest()
            {
                Date = DateTime.Now,
                Reason = reason,
                UserId = userId,
                UserName = userName
            };
            _verifyRequestManager.CreateVerifyRequest(verifyRequest);

            return RedirectToPage();
        }
    }
}