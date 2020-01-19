using COI.BL.Domain.Activity;
using COI.BL.Domain.Platform;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IActivityRepository
    {
        void CreateActivity(Activity activity);
        void DeleteActivity(Activity activity);
        IEnumerable<Activity> ReadActivities(int limit, Platform platform = null);
    }
}
