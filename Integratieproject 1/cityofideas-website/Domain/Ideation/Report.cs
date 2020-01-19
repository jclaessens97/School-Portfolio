namespace COI.BL.Domain.Ideation
{
    public class Report
    {
        public int ReportId { get; set; }
        public User.User User { get; set; }
        public string Reason { get; set; }
        public Comment ReportedComment { get; set; }
    }
}