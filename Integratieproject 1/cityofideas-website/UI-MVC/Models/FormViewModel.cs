using COI.BL.Domain.Ideation;
using System.Collections.Generic;

namespace COI.UI_MVC.Models
{
    public class FormViewModel
    {
        public int FormId { get; set; }
        public string Title { get; set; }

        public List<FormQuestionViewModel> Questions { get; set; }

        public EmailViewModel Email { get; set; }
        public IotLink IotLink { get; set; }
    }
}