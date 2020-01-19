using COI.BL.Domain.Project;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class ProjectRepository : IProjectRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public ProjectRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        public Project ReadProject(int projectId)
        {
            var project = _ctx
                .Projects
                .Include(p => p.Phases)
                .ThenInclude(p => p.Ideations)
                .ThenInclude(i => i.Replies)
                .Include(p => p.Phases)
                .ThenInclude(p => p.Forms)
                .ThenInclude(f => f.Questions)
                .Include(p => p.Logo)
               
                .Include(p => p.Moderators)
                .Include(p => p.Platform)
                .FirstOrDefault(p => p.ProjectId == projectId);

            if (project != null)
            {
                project.Phases.Sort();
            }
            
            return project;
        }
        
        public IEnumerable<Project> ReadProjects()
        {
            
            return _ctx
                .Projects

                .Include(p => p.Phases ) 
                .Include(p => p.Logo)
                .Include(p => p.Platform)
                .ToList();
        }

        public void CreateProject(Project project)
        {
            _ctx.Projects.Add(project);
            _ctx.SaveChanges();
        }

        public void UpdateProject(Project project)
        {
            _ctx.Projects.Update(project);
            _ctx.SaveChanges();
        }

        public IEnumerable<Project> SearchProjects(string query)
        {
            return _ctx
                .Projects
                .Include(p => p.Platform)
                .Include(p => p.Phases)
                .Include(p => p.Logo)
                .Where(p => p.Title.ToLower().Contains(query.ToLower()));
        }

        public int ReadVoteCount(int projectId)
        {
            return _ctx
                .Votes
                .Count(v => v.IdeationReply.Ideation.Project.ProjectId == projectId);
        }

        public int ReadCommentCount(int projectId)
        {
            return _ctx
                .Comments
                .Count(c => c.IdeationReply.Ideation.Project.ProjectId == projectId);
        }

        public int ReadIdeationReplyCount(int projectId)
        {
            return _ctx
                .IdeationReplies
                .Count(r => r.Ideation.Project.ProjectId == projectId);
        }
    }
}