using System;
using System.Collections.Generic;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Project;

namespace COI.BL.Domain.Ideation
{
    public class Ideation
    {
        public int IdeationId { get; set; }
        public string CentralQuestion { get; set; }
        public string Description { get; set; }
        public string Url { get; set; }
        public IdeationType IdeationType { get; set; }
        public List<Question> Questions { get; set; }
        public List<IdeationReply> Replies { get; set; }
        public Project.Project Project { get; set; }
        
        public Phase Phase { get; set; }
    }
}
