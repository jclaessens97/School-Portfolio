using COI.BL.Domain.Foundation;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class FormReplyDTO
    {
        public int FormId { get; set; }
        public List<FormAnswerDTO> Answers { get; set; }
        public string Email { get; set; }
    }

    public class FormAnswerDTO
    {
        public FieldType FieldType { get; set; }
        public string Reply { get; set; }
        public List<bool> MultipleAnswer { get; set; }
        public int SelectedChoice{ get; set; }
    }
}