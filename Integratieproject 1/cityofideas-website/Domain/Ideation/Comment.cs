using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore.Internal;

namespace COI.BL.Domain.Ideation
{
    public class Comment
    {
        public int CommentId { get; set; }
        public string CommentText { get; set; }
        public DateTime Created { get; set; }
        public IdeationReply IdeationReply { get; set; }
        public User.User User { get; set; }

        public bool IsFlagged => Reports.Any();
        
        public bool Hidden { get; set; }

        public List<Report> Reports { get; set; }
    }
}