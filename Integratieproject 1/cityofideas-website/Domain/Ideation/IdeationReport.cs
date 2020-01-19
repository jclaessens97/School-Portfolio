namespace COI.BL.Domain.Ideation
{
    public class IdeationReport
    {
        public int IdeationReportId { get; set; }
        public User.User User { get; set; }
        public IdeationReply ReportedIdeation { get; set; }
    }
}