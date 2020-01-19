using COI.BL.Domain.Platform;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class PlatformRepository : IPlatformRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public PlatformRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        public Platform ReadPlatform(int platformId)
        {
            return _ctx
                .Platforms
                .Include(p => p.ColorScheme)
                .Include(p => p.Logo)
                .Include(p => p.Banner)
                .Include(p => p.Projects)
                .ThenInclude(p => p.Phases ) 
                .Include(p => p.Projects)
                .ThenInclude(p => p.Logo)
                .Include(p => p.Projects)
                .ThenInclude(p => p.Platform)
               
                .FirstOrDefault(p => p.PlatformId == platformId);
        }

        public Platform ReadPlatformByTenant(string tenant)
        {
            return _ctx
                .Platforms
                .Include(p => p.Logo)
                .Include(p => p.Banner)
                .Include(p => p.ColorScheme)
                .Include(p => p.Projects)
                .ThenInclude(proj => proj.Logo)
                .Include(p => p.Projects)
                .ThenInclude(proj => proj.Phases)
                .FirstOrDefault(p => p.Tenant.Equals(tenant));
        }

        public Platform ReadPlatformByTenantWithForms(string tenant)
        {
            return _ctx
                .Platforms
                .Include(p => p.Logo)
                .Include(p => p.Banner)
                .Include(p => p.ColorScheme)
                .Include(p => p.Projects)
                .ThenInclude(proj => proj.Logo)
                .Include(p => p.Projects)
                .ThenInclude(proj => proj.Phases)
                .ThenInclude(p => p.Forms)
                .FirstOrDefault(p => p.Tenant.Equals(tenant));
        }

        public Platform ReadPlatformByReplyId(int replyId)
        {
            var ideationReply = _ctx
                .IdeationReplies
                .Include(r => r.Ideation)
                .ThenInclude(i => i.Project)
                .ThenInclude(p => p.Platform)
                .FirstOrDefault(r => r.IdeationReplyId == replyId);

            return ideationReply.Ideation.Project.Platform;
        }

        public IEnumerable<Platform> ReadAllPlatforms()
        {
            return _ctx
                .Platforms
                .Include(p => p.ColorScheme)
                .Include(p => p.Logo)
                .Include(p => p.Banner);
        }

        public IEnumerable<Platform> ReadPlatforms(int skip, int take)
        {
            return _ctx
                .Platforms
                .Include(p => p.Logo)
                .Skip(skip)
                .Take(take)
                .OrderBy(p => p.Name);
        }

        public void CreatePlatform(Platform platform)
        {
            _ctx.Platforms.Add(platform);
            _ctx.SaveChanges();
        }

        public void UpdatePlatform(Platform platform)
        {
            _ctx.Platforms.Update(platform);
            _ctx.SaveChanges();
        }

        public IEnumerable<Platform> SearchPlatforms(string query)
        {
            return _ctx
                .Platforms
                .Where(p => p.Name.ToLower().Contains(query.ToLower()));
        }
    }
}