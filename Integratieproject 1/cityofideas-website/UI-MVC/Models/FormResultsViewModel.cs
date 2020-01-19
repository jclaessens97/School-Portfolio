using System.Collections.Generic;

namespace COI.UI_MVC.Models
{
    public class FormProjectsViewModel
    {
        public int ProjectId { get; set; }
        public string Title { get; set; }
        public List<FormResultsViewModel> forms { get; set; }

        public FormProjectsViewModel()
        {
            forms = new List<FormResultsViewModel>();
        }
    }

    public class FormResultsViewModel
    {
        public int FormId { get; set; }
        public string Title { get; set; }
        public int AnswerCount { get; set; }
    }

}