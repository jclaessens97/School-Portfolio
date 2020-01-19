using COI.BL.Domain.Answer;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.User;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class IdeationRepository : IIdeationRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public IdeationRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        #region Ideations

        public Ideation ReadIdeation(int ideationId)
        {
            return _ctx
                .Ideations
                .Include(i => i.Project)
                .ThenInclude(p => p.Platform)
                .FirstOrDefault(i => i.IdeationId == ideationId);
        }

        public Ideation ReadIdeationWithReplies(int ideationId)
        {
            return _ctx
                .Ideations
                .Include(i => i.Project)
                .ThenInclude(p => p.Platform)
                .Include(i => i.Project)
                .ThenInclude(p => p.Moderators)
                .Include(i => i.Replies)
                .ThenInclude(r => r.Votes)
                .ThenInclude(r => r.User)
                .Include(i => i.Questions)
                .Include(i => i.Replies)
                .ThenInclude(r => r.Comments)
                .FirstOrDefault(i => i.IdeationId == ideationId);
        }

        public Ideation ReadIdeationWithQuestions(int ideationId)
        {
            return _ctx
                .Ideations
                .Include(i => i.Questions)
                .ThenInclude(q => q.Location)
                .FirstOrDefault(i => i.IdeationId == ideationId);
        }

        public IEnumerable<Ideation> ReadAllIdeations()
        {
            return _ctx
                .Ideations
                .ToList();
        }

        public IEnumerable<Ideation> ReadAllAdminIdeations(int projectId)
        {
            return _ctx.Ideations
                .Include(i => i.Replies)
                .Include(i => i.Project)
                .ThenInclude(p => p.Phases)
                .Where(i => i.IdeationType == IdeationType.Admin && i.Project.ProjectId == projectId);
        }

        public Ideation CreateIdeation(Ideation ideation)
        {
            _ctx.Ideations.Add(ideation);
            _ctx.SaveChanges();
            return ideation;
        }

        public IEnumerable<Ideation> SearchIdeations(string query)
        {
            return _ctx
                .Ideations
                .Include(i => i.Project.Platform)
                .Include(i => i.Project.Phases)
                .Include(i => i.Questions)
                .ThenInclude(i => i.Location)
                .Include(i => i.Replies)
                .ThenInclude(r => r.Answers)
                .Where(i => i.CentralQuestion.ToLower().Contains(query.ToLower()));
        }

        #endregion

        #region Ideation Replies

        public IEnumerable<IdeationReply> ReadIdeationReplies(int id)
        {
            // Hidden replies worden NIET meegegeven
            return _ctx
                .IdeationReplies
                .Include(r => r.Votes)
                .Include(r => r.User)
                .Include(r => r.Comments)
                .Include(r => r.Ideation)
                .Where(r => r.Ideation.IdeationId == id && !r.Hidden);
        }

        public IEnumerable<IdeationReply> ReadReportedIdeationReplies(int ideationId,int skip, int take)
        {
            return _ctx
                .IdeationReplies
                .Include(r => r.Votes)
                .Include(r => r.User)
                .Include(r => r.Comments)
                .Include(r => r.Ideation)
                .Include(r => r.Reports)
                .Where(r => r.Ideation.IdeationId == ideationId && !r.ReviewedByMod && r.Reports.Count > 0)
                .OrderByDescending(r => r.Reports.Count)
                .Skip(skip)
                .Take(take);

        }

        public int ReadIdeationReplyCountByIdeation(int ideationId)
        {
            return _ctx
                .IdeationReplies
                .Count(r => r.Ideation.IdeationId == ideationId && !r.Hidden);
        }

        public int ReadReportedIdeationReplyCountByIdeation(int ideationId)
        {
            return _ctx
                .IdeationReplies
                .Count(r => r.Ideation.IdeationId == ideationId && !r.ReviewedByMod && r.Reports.Count > 0);
        }

        public IdeationReply ReadIdeationReply(int ideationReplyId)
        {
            return _ctx
                .IdeationReplies
                .Include(r => r.Votes)
                .Include(r => r.Answers)
                .ThenInclude(question => ((MediaAnswer) question).Value)
                .Include(r => r.Answers)
                .ThenInclude(question => ((LocationAnswer) question).Value)
                .Include(r => r.Reports)
                .Include(r => r.User)
                .Include(r => r.Ideation)
                .ThenInclude(i => i.Questions)
                .Include(r => r.Ideation)
                .ThenInclude(i => i.Project)
                .ThenInclude(p => p.Platform)
                .Include(r => r.Ideation)
                .ThenInclude(i => i.Project)
                .ThenInclude(p => p.Moderators)
                .FirstOrDefault(r => r.IdeationReplyId == ideationReplyId);
        }

        public IEnumerable<Comment> GetIdeationReplyComments(int ideationReplyId)
        {
            return _ctx.Comments.Where(c => c.IdeationReply.IdeationReplyId == ideationReplyId);
        }

        public IdeationReply CreateIdeationReply(IdeationReply reply)
        {
            _ctx.IdeationReplies.Add(reply);
            _ctx.SaveChanges();
            return reply;
        }

        public void UpdateReply(IdeationReply reply)
        {
            _ctx.IdeationReplies.Update(reply);
            _ctx.CommitChanges();
        }

        public void CreateIdeationReport(IdeationReport report)
        {
            _ctx.IdeationReports.Add(report);
            _ctx.SaveChanges();
        }

        public IdeationReport ReadIdeationReport(IdeationReply reply, User user)
        {
            return _ctx.IdeationReports.FirstOrDefault(r => r.ReportedIdeation == reply && r.User == user);
        }

        public void DeleteIdeationReport(IdeationReport report)
        {
            _ctx.Remove(report);
            _ctx.SaveChanges();
        }

        #endregion

        #region Comments

        public Comment ReadComment(int commentId)
        {
            return _ctx
                .Comments
                .Find(commentId);
        }

        public int CommentAmount(int id)
        {
            return _ctx.IdeationReplies.Find(id).Comments.Count;
        }

        public IEnumerable<Comment> ReadComments(int id, int skip, int take)
        {
            return _ctx
                .Comments
                .Include(c => c.User)
                .Include(c => c.Reports)
                .ThenInclude(r => r.User)
                .Where(c => c.IdeationReply.IdeationReplyId == id && !c.Hidden)
                .OrderByDescending(c => c.Created)
                .Skip(skip)
                .Take(take);
        }

        public Comment AddComment(Comment comment)
        {
            _ctx.Add(comment);
            _ctx.SaveChanges();
            return comment;
        }

        public IEnumerable<Comment> GetFlaggedComments(int id, int skip, int take)
        {

            if (id < 0)
            {
                return _ctx.Comments
                    .Include(c => c.IdeationReply)
                    .Include(c => c.Reports)
                    .ThenInclude(r => r.User)
                    .Include(c => c.User)
                    .OrderByDescending(c => c.Reports.Count)
                    .Where(c => c.Reports.Count > 0 && !c.Hidden)
                    .Skip(skip)
                    .Take(take);
            }
            else
            {
                return 
                _ctx.Comments
                    .Include(c => c.IdeationReply)
                    .Include(c => c.Reports)
                    .ThenInclude(r => r.User)
                    .Include(c => c.User)
                    .OrderByDescending(c => c.Reports.Count)
                    .Where(c => c.Reports.Count > 0 && !c.Hidden && c.IdeationReply.IdeationReplyId == id)
                    .Skip(skip)
                    .Take(take);
            }
        }

        public Report CreateReport(Report report)
        {
            _ctx.Reports.Add(report);
            _ctx.SaveChanges();
            return report;
        }

        public void DeleteComment(int id)
        {
            Comment comment = _ctx.Comments.Find(id);
            _ctx.Comments.Remove(comment);
            _ctx.SaveChanges();
        }

        public Report ReadReportByDetails(User user, Comment comment)
        {
            return _ctx.Reports.FirstOrDefault(r => r.User == user && r.ReportedComment == comment);
        }

        public Report ReadReport(int id)
        {
            return _ctx.Reports.Find(id);
        }

        public void DeleteReport(Report report)
        {
            _ctx.Reports.Remove(report);
            _ctx.SaveChanges();
        }

        public void UpdateComment(Comment comment)
        {
            _ctx.Comments.Update(comment);
            _ctx.SaveChanges();
        }

        public void DeleteReportByComment(int commentId)
        {
            Comment comment = _ctx.Comments.Find(commentId);
            _ctx.Reports.RemoveRange(_ctx.Reports.Where(r => r.ReportedComment == comment));
            _ctx.SaveChanges();
        }

        #endregion

        #region Votes

        public void InsertVote(Vote vote)
        {
            _ctx.Votes.Add(vote);
            _ctx.SaveChanges();
        }

        public void RemoveVote(Vote vote)
        {
            _ctx.Votes.Remove(vote);
            _ctx.SaveChanges();
        }

        public int ReadTotalVoteCount()
        {
            return _ctx.Votes.Count();
        }

        public int ReadTotalVoteCount(int platformId)
        {
            return _ctx.Votes.Count(v => v.Project.Platform.PlatformId == platformId);
        }

        public int ReadIdeationReplyCount(int platformId)
        {
            return _ctx.IdeationReplies.Count(r => r.Ideation.Project.Platform.PlatformId == platformId);
        }

        public int ReadCommentCount(int platformId)
        {
            return _ctx.Comments.Count(c => c.IdeationReply.Ideation.Project.Platform.PlatformId == platformId);
        }

        #endregion
    }
}