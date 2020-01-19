using COI.BL.Domain.Ideation;
using COI.BL.Domain.Form;
using System;

namespace COI.BL.Domain.Activity
{
    public class Activity
    {
        public int ActivityId { get; set; }
        public DateTime ActivityTime { get; set; }
        public ActivityType ActivityType { get; set; }
        public User.User User { get; set; }
        public Platform.Platform Platform { get; set; }

        // only one of the following fields is filled in:
        public Vote Vote { get; set; }
        public Comment Comment { get; set; }
        public IdeationReply IdeationReply { get; set; }
        public Form.Form Form { get; set; }
    }
}
