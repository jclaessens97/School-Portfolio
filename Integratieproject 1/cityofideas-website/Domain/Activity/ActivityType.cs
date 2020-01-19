using System;
using System.Collections.Generic;
using System.Text;

namespace COI.BL.Domain.Activity
{
    public enum ActivityType : byte
    {
        IdeationVote = 0,
        FormVote,
        Comment,
        IdeationReply,
    }
}
