using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class CommentDTO
    {
        public int CommentId { get; set; }
        public int IdeationReplyId { get; set; }
        public string CommentText { get; set; }
        public string DateTime { get; set; }
        public string UserDisplayName { get; set; }
        public string UserName { get; set; }
        public string UserFullName { get; set; } //Only for admin
        public List<ReportDTO> Reports { get; set; } //Only for admin
        public bool ReportedByMe { get; set; }
    }
}