using COI.BL.Domain.Project;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IProjectRepository
    {
        Project ReadProject(int projectId);
        IEnumerable<Project> ReadProjects();
        void CreateProject(Project project);
        void UpdateProject(Project project);
        IEnumerable<Project> SearchProjects(string query);
        int ReadVoteCount(int projectId);
        int ReadCommentCount(int projectId);
        int ReadIdeationReplyCount(int projectId);
    }
}