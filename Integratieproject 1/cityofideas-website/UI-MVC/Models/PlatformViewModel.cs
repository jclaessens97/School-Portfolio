using System.Collections.Generic;
using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Http;
using System.ComponentModel.DataAnnotations;

namespace COI.UI_MVC.Models
{
    public class PlatformViewModel
    {
        public int PlatformId { get; set; }

        [Required(ErrorMessage = "Platform name is required")]
        [MinLength(5, ErrorMessage = "Platformname need to be at least 5 characters long.")]
        [MaxLength(50, ErrorMessage = "Platformname can be max 50 characters long.")]
        public string Name { get; set; }

        [Required]
        [RegularExpression(Constants.NoSpaceRegex, ErrorMessage = "No white space allowed")]
        public string Tenant { get; set; }

        // [Required]
        public IFormFile Logo { get; set; }
        public bool LogoChanged { get; set; }
        public string LogoUrl { get; set; }

        public IFormFile Banner { get; set; }
        public bool BannerChanged { get; set; }
        public string BannerUrl { get; set; }

        public string PlatformReason { get; set; }

        [Required]
        public string Description { get; set; }
        
        public List<string> Admins { get; set; }

        #region ColorScheme
        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string SocialBarColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string NavbarColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string BannerColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string ButtonColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string ButtonTextColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string TextColor { get; set; }

        [Required]
        [RegularExpression(Constants.ColorHexRegex, ErrorMessage = "Color need to be of format #000000")]
        public string BodyColor { get; set; }
        #endregion
    }
}
