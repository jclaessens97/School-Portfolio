using COI.BL.Domain.Form;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.User;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IIdeationManager
    {
        // Ideations
        Ideation GetIdeationWithReplies(int ideationId, string orderBy = "recent");
        Ideation GetIdeationWithQuestions(int ideationId);
        IEnumerable<Ideation> GetIdeations();
        IEnumerable<Ideation> GetAllAdminIdeations(int projectId);
        Ideation AddIdeation(Ideation ideation);
        IEnumerable<Ideation> SearchIdeations(string query, int minLength = 3);

        // Ideation Replies
        IEnumerable<IdeationReply> GetIdeationReplies(int id, int skip, int take, string orderBy = "recent");
        IEnumerable<IdeationReply> GetReportedIdeationReplies(int ideationId, int skip, int take);
        int GetReportedIdeationReplyCountByIdeation(int ideationId);
        int GetIdeationReplyCountByIdeation(int ideationId);
        IdeationReply GetIdeationReply(int ideationReplyId);
        IdeationReply AddIdeationReply(IdeationReply ideationReply);
        int GetIdeationReplyCount(int platformId);
        void UpdateReply(IdeationReply reply);
        void ReportIdeation(int id, User user);
        void CancelReportIdeation(int id, User user);
        void ApprovePost(int id);
        void DisapprovePost(int id);

        // Ideation Questions
        IEnumerable<Question> GetIdeationQuestions(int ideationId);

        // Comments
        Comment GetComment(int commentId);
        IEnumerable<Comment> GetComments(int ideationId, int skip, int take);
        Comment AddComment(string commentText, User user, IdeationReply ideationReply);
        void RemoveComment(int commentId);
        int GetCommentCount(int platformId);
        Report FindReport(User user, Comment comment);
        Report ReportComment(Report report);
        void RemoveReport(int id);
        IEnumerable<Comment> GetFlaggedComments(int id,int skip, int take);
        void HideComment(int commentId);
        void UnHideComment(int commentId);
        void AllowComment(int commentId);

        // Votes
        Vote VoteReplyUp(int replyId,User user = null);
        Vote VoteReplyDown(int replyId,User user = null);
        int GetTotalVoteCount();
        int GetTotalVoteCount(int platformId);
        int CommentAmount(int id);
    }
}
