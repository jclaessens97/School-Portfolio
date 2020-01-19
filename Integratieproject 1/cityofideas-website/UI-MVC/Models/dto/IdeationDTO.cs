using COI.BL.Domain.Foundation;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class IdeationDTO
    {
        public int ProjectId { get; set; }
        public int PhaseId { get; set; }

        public string CentralQuestion { get; set; }
        public string Description { get; set; }
        public string Url { get; set; }
        public IdeationType IdeationType { get; set; }
        
        public List<QuestionDTO> Questions { get; set; }
    }
}