using COI.BL.Domain.Project;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IProjectManager
    {
        Project GetProject(int projectId);
        IEnumerable<Project> GetProjects();
        void AddProject(Project project);
        void UpdateProject(Project project);
        IEnumerable<Project> SearchProjects(string query, int minLength = 3);


        int GetVoteCount(int projectId);
        int GetCommentCount(int projectId);
        int GetIdeationReplyCount(int projectId);
    }
}
