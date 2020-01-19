using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    //this class is used for adding forms, not filling out forms
    public class FormDTO
    {
        public int ProjectId { get; set; }
        public int PhaseId { get; set; }
        public string FormTitle { get; set; }

        public List<QuestionDTO> Questions { get; set; }
        public bool IsStatementForm { get; set; }
    }
}