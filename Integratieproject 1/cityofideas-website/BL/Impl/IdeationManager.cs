using COI.BL.Algorithms;
using COI.BL.Domain.Form;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.User;
using COI.DAL;
using System;
using System.Collections.Generic;
using System.Linq;

namespace COI.BL.Impl
{
    public class IdeationManager : IIdeationManager
    {
        private readonly IIdeationRepository _ideationRepository;
        private readonly ITrendingAlgorithm _trendingAlgorithm;

        public IdeationManager(
            IIdeationRepository ideationRepository,
            ITrendingAlgorithm trendingAlgorithm
        )
        {
            _ideationRepository = ideationRepository;
            _trendingAlgorithm = trendingAlgorithm;
        }

        #region Ideations
        public Ideation GetIdeationWithReplies(int ideationId, string orderBy = "recent")
        {
            var ideation = _ideationRepository.ReadIdeationWithReplies(ideationId);
            if (ideation == null) return null;

            switch(orderBy.ToLower())
            {
                case "recent":
                    ideation.Replies.Sort((a, b) => DateTime.Compare(b.Created, a.Created));
                    break;
                case "top":
                    ideation.Replies.Sort((a, b) => b.Upvotes.CompareTo(a.Upvotes));
                    break;
                case "trending":
                    ideation.Replies = _trendingAlgorithm.GetTrendingList(ideation.Replies).Result.ToList();
                    break;
                case "controversial":
                    ideation.Replies.Sort((a, b) => b.Downvotes.CompareTo(a.Downvotes));
                    break;
            }

            return ideation;
        }

        public Ideation GetIdeationWithQuestions(int ideationId)
        {
            return _ideationRepository.ReadIdeationWithQuestions(ideationId);
        }

        public IEnumerable<Ideation> GetIdeations()
        {
            return _ideationRepository.ReadAllIdeations();
        }

        public IEnumerable<Ideation> GetAllAdminIdeations(int projectId)
        {
            return _ideationRepository.ReadAllAdminIdeations(projectId);
        }

        public Ideation AddIdeation(Ideation ideation)
        {
            return _ideationRepository.CreateIdeation(ideation);
        }

        public IEnumerable<Ideation> SearchIdeations(string query, int minLength = 3)
        {
            if (query != null && query.Length >= minLength)
            {
                return _ideationRepository.SearchIdeations(query);
            }

            return new List<Ideation>();
        }

        #endregion

        #region Ideation Replies

        public IEnumerable<IdeationReply> GetIdeationReplies(int id, int skip, int take, string orderBy = "recent")
        {
            List<IdeationReply> replies = _ideationRepository.ReadIdeationReplies(id).ToList();
            
            switch(orderBy.ToLower())
            {
                case "recent":
                    replies.Sort((a, b) => DateTime.Compare(b.Created, a.Created));
                    break;
                case "top":
                    replies.Sort((a, b) => b.Upvotes.CompareTo(a.Upvotes));
                    break;
                case "trending":
                    replies = _trendingAlgorithm.GetTrendingList(replies).Result.ToList();
                    break;
                case "controversial":
                    replies.Sort((a, b) => b.Downvotes.CompareTo(a.Downvotes));
                    break;
            }

            return replies.Skip(skip).Take(take);
        }

        public IEnumerable<IdeationReply> GetReportedIdeationReplies(int ideationId, int skip, int take)
        {
            return _ideationRepository.ReadReportedIdeationReplies(ideationId,skip,take);
        }

        public int GetReportedIdeationReplyCountByIdeation(int ideationId)
        {
            return _ideationRepository.ReadReportedIdeationReplyCountByIdeation(ideationId);
        }

        public int GetIdeationReplyCountByIdeation(int ideationId)
        {
            return _ideationRepository.ReadIdeationReplyCountByIdeation(ideationId);
        }

        public IdeationReply GetIdeationReply(int ideationReplyId)
        {
            IdeationReply reply = _ideationRepository.ReadIdeationReply(ideationReplyId);
            reply.Answers.Sort();
            reply.Ideation.Questions.Sort();
            return reply;
        }

        public IdeationReply AddIdeationReply(IdeationReply ideationReply)
        {
            return _ideationRepository.CreateIdeationReply(ideationReply);
        }

        public int GetIdeationReplyCount(int platformId)
        {
            return _ideationRepository.ReadIdeationReplyCount(platformId);
        }

        public void UpdateReply(IdeationReply reply)
        {
            _ideationRepository.UpdateReply(reply);
        }

        public void ReportIdeation(int id, User user)
        {
            IdeationReply reply = _ideationRepository.ReadIdeationReply(id);
            IdeationReport report = new IdeationReport()
            {
                User = user,
                ReportedIdeation = reply
            };

            _ideationRepository.CreateIdeationReport(report);
        }

        public void CancelReportIdeation(int id, User user)
        {
            IdeationReply reply = _ideationRepository.ReadIdeationReply(id);
            IdeationReport report = _ideationRepository.ReadIdeationReport(reply, user);
            _ideationRepository.DeleteIdeationReport(report);
        }

        public void ApprovePost(int id)
        {
            IdeationReply reply = _ideationRepository.ReadIdeationReply(id);
            reply.ReviewedByMod = true;
            reply.Hidden = false;
            _ideationRepository.UpdateReply(reply);
        }

        public void DisapprovePost(int id)
        {
            IdeationReply reply = _ideationRepository.ReadIdeationReply(id);
            reply.ReviewedByMod = true;
            reply.Hidden = true;
            _ideationRepository.UpdateReply(reply);
        }

        #endregion

        #region Ideation Questions
        public IEnumerable<Question> GetIdeationQuestions(int ideationId)
        {
            Ideation ideation = _ideationRepository.ReadIdeationWithQuestions(ideationId);

            if (ideation == null || !ideation.Questions.Any())
            {
                return new List<Question>();
            }

            ideation.Questions.Sort();
            return ideation.Questions;
        }
        #endregion

        #region Comments
        public Comment GetComment(int commentId)
        {
            return _ideationRepository.ReadComment(commentId);
        }

        public IEnumerable<Comment> GetComments(int ideationId, int skip, int take)
        {
            return _ideationRepository.ReadComments(ideationId, skip, take);
        }

        public Comment AddComment(string commentText, User user, IdeationReply ideationReply)
        {
            var commentToAdd = new Comment
            {
                CommentText = commentText,
                Hidden = false,
                IdeationReply = ideationReply,
                User = user,
                Created = DateTime.Now,
                Reports = new List<Report>()
            };
            
            return _ideationRepository.AddComment(commentToAdd); ;
        }

        public void RemoveComment(int commentId)
        {
            _ideationRepository.DeleteComment(commentId);
        }

        public int GetCommentCount(int platformId)
        {
            return _ideationRepository.ReadCommentCount(platformId);
        }

        public Report FindReport(User user, Comment comment)
        {
            return _ideationRepository.ReadReportByDetails(user, comment);
        }

        public Report ReportComment(Report report)
        {
            return _ideationRepository.CreateReport(report);
        }

        public void RemoveReport(int id)
        {
            Report report = _ideationRepository.ReadReport(id);
            if (report != null)
            {
                _ideationRepository.DeleteReport(report);
            }
        }

        public IEnumerable<Comment> GetFlaggedComments(int id,int skip, int take)
        {
            return _ideationRepository.GetFlaggedComments(id,skip, take);
        }

        public void HideComment(int commentId)
        {
            Comment commentToHide = _ideationRepository.ReadComment(commentId);
            commentToHide.Hidden = true;
            _ideationRepository.UpdateComment(commentToHide);
        }

        public void UnHideComment(int commentId)
        {
            Comment commentToHide = _ideationRepository.ReadComment(commentId);
            commentToHide.Hidden = false;
            _ideationRepository.UpdateComment(commentToHide);
        }

        public void AllowComment(int commentId)
        {
            _ideationRepository.DeleteReportByComment(commentId);
        }

        #endregion

        #region Votes
        public Vote VoteReplyUp(int replyId, User user = null)
        {
            var reply = _ideationRepository.ReadIdeationReply(replyId);
            Vote vote = null;
            if (user != null)
            {
                vote = reply.Votes.Find(v => v.User == user);
            }

            if (vote != null)
            {
                return null; //already voted
            }

            vote = reply.VoteUp(user);
            _ideationRepository.UpdateReply(reply);
            return vote;
        }

        public Vote VoteReplyDown(int replyId, User user = null)
        {
            var reply = _ideationRepository.ReadIdeationReply(replyId);
            Vote vote = null;
            if (user != null)
            {
                vote = reply.Votes.Find(v => v.User == user);
            }

            if (vote != null)
            {
                return null; //already voted
            }

            vote = reply.VoteDown(user);
            _ideationRepository.UpdateReply(reply);
            return vote;
        }

        public int GetTotalVoteCount()
        {
            return _ideationRepository.ReadTotalVoteCount();
        }

        public int GetTotalVoteCount(int platformId)
        {
            return _ideationRepository.ReadTotalVoteCount(platformId);
        }

        public int CommentAmount(int id)
        {
            return _ideationRepository.CommentAmount(id);
        }

        #endregion
    }
}