using System.Collections.Generic;
using COI.BL.Domain.Project;

namespace COI.BL.Domain.Form
{
    public class Form
    {
        public int FormId { get; set; }
        public string Title { get; set; }
        public bool IsStatementForm { get; set; }

        public List<Question> Questions { get; set; }
        public List<FormReply> Replies { get; set; }
        public Project.Project Project { get; set; }
        public Phase Phase { get; set; }

        public Form()
        {
            Replies = new List<FormReply>();
            Questions = new List<Question>();
            
        }
    }
}