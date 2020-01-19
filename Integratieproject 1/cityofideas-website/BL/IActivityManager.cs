using COI.BL.Domain.Activity;
using COI.BL.Domain.Platform;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IActivityManager
    {
        void AddActivity(Activity activity);
        void RemoveActivity(Activity activity);
        IEnumerable<Activity> GetActivityFeed(Platform platform = null);
    }
}
