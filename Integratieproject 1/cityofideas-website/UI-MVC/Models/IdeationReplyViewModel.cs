using COI.BL.Domain.Foundation;
using System;
using System.Collections.Generic;

namespace COI.UI_MVC.Models
{
    public class IdeationReplyViewModel
    {
        public int IdeationReplyId { get; set; }
        public string CentralQuestion { get; set; }
        public string Title { get; set; }
        public List<AnswerViewModel> Answers { get; set; }
        public DateTime DateTime { get; set; }
        public bool UserHasVoted { get; set; } //Check if user that visits page has already voted
        public int UpVotes { get; set; }
        public int DownVotes { get; set; }
        public int NumberOfComments { get; set; }
        public string UserDisplayName { get; set; }
        public bool IsFlagged { get; set; }
        public bool ReviewedByMod { get; set; }
        public bool Hidden { get; set; }
    }

    public class AnswerViewModel
    {
        public FieldType FieldType { get; set; }
        public string QuestionString { get; set; }
        public string OpenAnswer { get; set; }
        public string SingleAnswer { get; set; } //wordt omgezet naar string in controller -> minder logica in View
        public List<string> MultipleAnswer { get; set; } //Idem
        public Location LocationAnswer { get; set; }
        public Media FileAnswer { get; set; }
    }
}