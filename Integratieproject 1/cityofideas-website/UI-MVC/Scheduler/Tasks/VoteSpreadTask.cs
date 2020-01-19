using COI.BL.Application;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Platform;
using COI.BL.Domain.User;
using COI.UI_MVC.Hubs;
using COI.UI_MVC.Hubs.Impl;
using COI.UI_MVC.Models;
using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using UI_MVC.Scheduler.Scheduling;

namespace COI.UI_MVC.Scheduler.Tasks
{
    public class VoteSpreadTask : IScheduledTask
    {
        private readonly IServiceProvider _serviceProvider;

        // VoteQueues per iotLinkId
        public Dictionary<int, Queue<char>> VoteQueues { get; set; }
        public Dictionary<Platform, Queue<User>> UserQueues { get; set; }

        public VoteSpreadTask(IServiceProvider serviceProvider)
        {
            VoteQueues = new Dictionary<int, Queue<char>>();
            UserQueues = new Dictionary<Platform, Queue<User>>();
            _serviceProvider = serviceProvider;
        }

        #region Scheduling
        // CronTab schedule: 
        public string Schedule => "* * * * *";

        public async Task ExecuteAsync(CancellationToken cancellationToken)
        {
            Debug.WriteLine("Processing votes..");
            var rnd = new Random();

            using (var scope = _serviceProvider.CreateScope())
            {
                var voteController = scope.ServiceProvider.GetService<IVoteController>();
                var voteHubContext = scope.ServiceProvider.GetService<IHubContext<VoteHub, IVoteHub>>();
                var activityHubContext = scope.ServiceProvider.GetService<IHubContext<ActivityHub, IActivityHub>>();

                foreach (var iotLinkId in VoteQueues.Keys)
                {
                    var platform = voteController.GetPlatformByIotLinkId(iotLinkId);
                    User user = null;

                    if (UserQueues.ContainsKey(platform))
                    {
                        UserQueues[platform].TryDequeue(out user);
                    }

                    int totalTime = 60 * 1000; // 1 minute in millis
                    int votesNextMinute = rnd.Next(Math.Min(11, VoteQueues[iotLinkId].Count)) + 1; // Between 0 and 10 unless queue length is smaller
                    int votesLeft = votesNextMinute;

                    Debug.WriteLine($"Amount of votes in the next minute: {votesNextMinute}");

                    for (var i = 0; i < votesNextMinute; i++)
                    {
                        int maxInterval = totalTime / votesLeft; // max possible interval in ms 
                        int randomizedInterval = rnd.Next(maxInterval);

                        Debug.WriteLine($"Maximum interval: {maxInterval}");

                        if (VoteQueues[iotLinkId].TryDequeue(out char vote))
                        {

                            if (vote == '+')
                            {
                                var linkTypeAndIdAndActivity = voteController.VoteUp(iotLinkId, user);
                                IotLinkType linkType = linkTypeAndIdAndActivity.Item1;
                                int id = linkTypeAndIdAndActivity.Item2;
                                BL.Domain.Activity.Activity activity = linkTypeAndIdAndActivity.Item3;

                                if (linkType == IotLinkType.Ideation)
                                {
                                    await voteHubContext.Clients.Group($"ideationReply - {id}").ReceiveUpvote();
                                }

                                var activityVm = new ActivityViewModel(activity);

                                await Task.WhenAll(
                                    activityHubContext.Clients.Group($"activity - {activity.Platform.PlatformId}").UpdateActivityFeed(activityVm),
                                    activityHubContext.Clients.Group($"activity - all").UpdateActivityFeed(activityVm)
                                );
                            }

                            if (vote == '-')
                            {
                                var linkTypeAndIdAndActivity = voteController.VoteDown(iotLinkId, user);
                                IotLinkType linkType = linkTypeAndIdAndActivity.Item1;
                                int id = linkTypeAndIdAndActivity.Item2;
                                BL.Domain.Activity.Activity activity = linkTypeAndIdAndActivity.Item3;

                                if (linkType == IotLinkType.Ideation)
                                {
                                    await voteHubContext.Clients.Group($"ideationReply - {id}").ReceiveDownvote();
                                }

                                var activityVm = new ActivityViewModel(activity);

                                await Task.WhenAll(
                                    activityHubContext.Clients.Group($"activity - {activity.Platform.PlatformId}").UpdateActivityFeed(activityVm),
                                    activityHubContext.Clients.Group($"activity - all").UpdateActivityFeed(activityVm)
                                );
                            }
                        }

                        Debug.WriteLine($"Waiting {randomizedInterval} ms");

                        Thread.Sleep(randomizedInterval);

                        Debug.WriteLine("Done waiting...");
                        Debug.WriteLine(VoteQueues[iotLinkId].Count + " votes left");

                        totalTime -= randomizedInterval;
                        votesLeft--;
                    }
                }
            }

            Cleanup();

            await Task.CompletedTask;
        }

        /// <summary>
        /// Cleanup methods to limit memory for queues & dictionaries
        /// </summary>
        private void Cleanup()
        {
            foreach (var reply in VoteQueues.Keys)
            {
                if (VoteQueues[reply].Count == 0)
                {
                    VoteQueues.Remove(reply);
                } else
                {
                    VoteQueues[reply].TrimExcess();
                }
            }

            foreach (var platform in UserQueues.Keys)
            {
                if (UserQueues[platform].Count == 0)
                {
                    UserQueues.Remove(platform);
                } else
                {
                    UserQueues[platform].TrimExcess();
                }
            }
        }
        #endregion
    }
}
