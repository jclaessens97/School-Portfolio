using System;

namespace COI.BL.Domain.Ideation
{
    public class Vote
    {
        public int VoteId { get; set; }
        public bool Anonymous { get; set; }
        public DateTime Created { get; set; }
        public bool Value { get; set; }

        public IdeationReply IdeationReply { get; set; }
        public User.User User { get; set; }

        public Project.Project  Project { get; set; }
    }
}
