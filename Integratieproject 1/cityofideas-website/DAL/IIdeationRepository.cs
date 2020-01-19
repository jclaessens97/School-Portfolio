using COI.BL.Domain.Ideation;
using COI.BL.Domain.User;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IIdeationRepository
    {
        // Ideations
        Ideation ReadIdeation(int ideationId);
        Ideation ReadIdeationWithReplies(int ideationId);
        Ideation ReadIdeationWithQuestions(int ideationId);
        IEnumerable<Ideation> ReadAllIdeations();
        IEnumerable<Ideation> ReadAllAdminIdeations(int projectId);
        Ideation CreateIdeation(Ideation ideation);
        int CommentAmount(int id);
        IEnumerable<Ideation> SearchIdeations(string query);

        // Ideation Replies
        IEnumerable<IdeationReply> ReadIdeationReplies(int id);
        IEnumerable<IdeationReply> ReadReportedIdeationReplies(int ideationId, int take, int skip);
        int ReadIdeationReplyCountByIdeation(int ideationId);
        int ReadReportedIdeationReplyCountByIdeation(int ideationId);
        IdeationReply ReadIdeationReply(int ideationReplyId);
        IEnumerable<Comment> GetIdeationReplyComments(int ideationReplyId);
        IdeationReply CreateIdeationReply(IdeationReply reply);
        void UpdateReply(IdeationReply reply);
        void CreateIdeationReport(IdeationReport report);
        IdeationReport ReadIdeationReport(IdeationReply reply, User user);
        void DeleteIdeationReport(IdeationReport report);

        // Comments
        Comment ReadComment(int commentId);
        IEnumerable<Comment> ReadComments(int id, int skip, int take);
        Comment AddComment(Comment comment);
        IEnumerable<Comment> GetFlaggedComments(int id,int skip, int take);
        Report CreateReport(Report report);
        void DeleteComment(int id);
        Report ReadReportByDetails(User user, Comment comment);
        Report ReadReport(int id);
        void DeleteReport(Report report);
        void UpdateComment(Comment comment);
        void DeleteReportByComment(int commentId);

        // Votes
        void InsertVote(Vote vote);
        void RemoveVote(Vote vote);
        int ReadTotalVoteCount();
        int ReadTotalVoteCount(int platformId);


        int ReadIdeationReplyCount(int platformId);
        int ReadCommentCount(int platformId);
    }
}