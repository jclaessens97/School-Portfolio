using System;
using System.Collections.Generic;
using System.Linq;
namespace COI.BL.Domain.Ideation
{
    public class IdeationReply
    {
        public int IdeationReplyId { get; set; }
        public Ideation Ideation { get; set; }
        public User.User User { get; set; }
        public string Title { get; set; }
        public List<Answer.Answer> Answers { get; set; }
        public List<Vote> Votes { get; set; }
        public List<Comment> Comments { get; set; }
        public List<IdeationReport> Reports { get; set; }
        public DateTime Created { get; set; }

        public int VotesAmount
        {
            get
            {
                if (Votes != null && Votes.Count > 0)
                {
                    return Votes.Count;
                }

                return 0;
            }
        }
        public int Upvotes => GetUpvotes();
        public int Downvotes => GetDownvotes();
        
        public bool ReviewedByMod { get; set; } // Mod heeft zijn goedkeuren/afkeuring gegeven
        public bool Hidden { get; set; } // True = post verborgen door mod

        public Vote VoteUp(User.User user)
        {
            var vote = new Vote()
            {
                Anonymous = user == null,
                User = user,
                Created = DateTime.Now,
                Value = true,
                IdeationReply = this,
                Project = Ideation.Project
            };

            Votes.Add(vote);
            return vote;
        }

        public Vote VoteDown(User.User user)
        {
            var vote = new Vote()
            {
                Anonymous = user == null,
                User = user,
                Created = DateTime.Now,
                Value = false,
                IdeationReply = this,
                Project = Ideation.Project
            };

            Votes.Add(vote);
            return vote;
        }

        private int GetUpvotes()
        {
            if (Votes != null)
            {
                return Votes.Where(v => v.Value).ToList().Count;
            }

            return 0;

        }

        private int GetDownvotes()
        {
            if (Votes != null && Votes.Count > 0)
            {
                return Votes.Where(v => !v.Value).ToList().Count;
            }

            return 0;
        }
        
        public int GetUpVotesScaled()
        {
            if (Votes != null && Votes.Count > 0)
            {
                return (int)((Double)GetUpvotes() / (Double)VotesAmount * (Double)100);
            }

            return 0;
        }
        
        public int GetDownVotesScaled()
        {
            if (Votes != null && Votes.Count > 0)
            {
                return (int)((Double)GetDownvotes() / (Double)VotesAmount * (Double)100);
            }

            return 0;
        }
    }
}
