using COI.BL.Domain.Project;
using System.Collections.Generic;

namespace COI.UI_MVC.Models
{
    public class CreateFormViewModel
    {
        public int ProjectId { get; set; }
        public int PhaseId { get; set; }

        public string ProjectTitle { get; set; }
        public List<Phase> Phases { get; set; }
        public string Phase { get; set; }

        public string FormTitle { get; set; }
    }
}