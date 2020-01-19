using System.Collections.Generic;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;

namespace COI.UI_MVC.Models.dto
{
    public class FormResultDTO
    {
        public Question Question { get; set; }
        public List<Answer> Answers { get; set; }

        public FormResultDTO()
        {
            Answers = new List<Answer>();
        }
        
    }
}