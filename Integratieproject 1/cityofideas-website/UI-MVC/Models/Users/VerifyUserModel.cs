using System.ComponentModel.DataAnnotations;

namespace COI.UI_MVC.Models.Users
{
    public class VerifyUserModel
    {
        [Required]
        [Display(Name = "Reason for VerifyRequest")]
        public string RequestReason { get; set; }
    }
}