using COI.BL.Domain.Activity;
using COI.BL.Domain.Platform;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class ActivityRepository : IActivityRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public ActivityRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        public void CreateActivity(Activity activity)
        {
            _ctx.Activities.Add(activity);
            _ctx.SaveChanges();
        }

        public void DeleteActivity(Activity activity)
        {
            _ctx.Activities.Remove(activity);
            _ctx.SaveChanges();
        }

        public IEnumerable<Activity> ReadActivities(int limit, Platform platform = null)
        {
            var query = _ctx
                    .Activities
                    .Include(a => a.User)
                    .Include(a => a.Vote)
                    .ThenInclude(v => v.IdeationReply.Ideation)
                    .Include(a => a.Comment)
                    .ThenInclude(c => c.IdeationReply.Ideation)
                    .Include(a => a.IdeationReply)
                    .ThenInclude(r => r.Ideation)
                    .Include(a => a.Form)
                    .OrderByDescending(a => a.ActivityTime)
                    .Take(limit);

            if (platform == null)
            {
                return query.ToList();
            } else
            {
                return query
                    .Where(a => a.Platform == platform)
                    .ToList();
            }
        }
    }
}
