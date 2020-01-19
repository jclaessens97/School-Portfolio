namespace COI.BL.Domain.Project
{
    public class ProjectModerator
    {
        public int ProjectModeratorId { get; set; }
        public Project Project { get; set; }
        public User.User User { get; set; }
    }
}