using COI.BL.Domain.Activity;
using COI.BL.Domain.Platform;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class ActivityManager : IActivityManager
    {
        private const int MAX_NUMBER_OF_ACTIVITIES = 15;

        private readonly IActivityRepository _activityRepository;

        public ActivityManager(IActivityRepository activityRepository)
        {
            _activityRepository = activityRepository;
        }

        public void AddActivity(Activity activity)
        {
            _activityRepository.CreateActivity(activity);
        }

        public void RemoveActivity(Activity activity)
        {
            _activityRepository.DeleteActivity(activity);
        }

        public IEnumerable<Activity> GetActivityFeed(Platform platform = null)
        {
            return _activityRepository.ReadActivities(MAX_NUMBER_OF_ACTIVITIES, platform);
        }
    }
}
