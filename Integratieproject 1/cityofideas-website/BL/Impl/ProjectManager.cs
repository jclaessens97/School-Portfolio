using COI.BL.Domain.Project;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class ProjectManager : IProjectManager
    {
        private readonly IProjectRepository _projectRepository;

        public ProjectManager(IProjectRepository projectRepository)
        {
            _projectRepository = projectRepository;
        }

        public Project GetProject(int projectId)
        {
            return _projectRepository.ReadProject(projectId);
        }
        
        public IEnumerable<Project> GetProjects()
        {
            return _projectRepository.ReadProjects();
        }

        public void AddProject(Project project)
        {
            _projectRepository.CreateProject(project);
        }

        public void UpdateProject(Project project)
        {
            _projectRepository.UpdateProject(project);
        }

        public IEnumerable<Project> SearchProjects(string query, int minLength = 3)
        {
            if (query != null && query.Length >= minLength)
            {
                return _projectRepository.SearchProjects(query);
            }

            return new List<Project>();
        }

        public int GetVoteCount(int projectId)
        {
            return _projectRepository.ReadVoteCount(projectId);
        }

        public int GetCommentCount(int projectId)
        {
            return _projectRepository.ReadCommentCount(projectId);
        }

        public int GetIdeationReplyCount(int projectId)
        {
            return _projectRepository.ReadIdeationReplyCount(projectId);
        }
    }
}
