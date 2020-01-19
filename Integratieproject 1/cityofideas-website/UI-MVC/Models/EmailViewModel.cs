using System.ComponentModel.DataAnnotations;

namespace COI.UI_MVC.Models
{
    public class EmailViewModel
    {
        [Required(ErrorMessage = "Email verplicht")]
        [EmailAddress(ErrorMessage = "Geen geldige email")]
        public string Email { get; set; }
    }
}