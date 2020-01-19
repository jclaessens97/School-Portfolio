using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace COI.UI_MVC.Models.dto
{
    public class ProjectDTO
    {
        [Required]
        public string Title { get; set; }

        [Required]
        public string Goal { get; set; }

        public IFormFile Logo { get; set; }
        public bool LogoChanged { get; set; }

        [Required]
        public DateTime StartDate { get; set; }
        
        [Required]
        public DateTime EndDate { get; set; }

        [Required]
        public List<PhaseViewModel> Phases { get; set; }

        [Required]
        public string PlatformTenant { get; set; }

        public List<string> Moderators { get; set; }
        public int CurrentPhase { get; set; }
    }
}
