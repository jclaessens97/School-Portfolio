using Microsoft.AspNetCore.SignalR;
using System.Threading.Tasks;

namespace COI.UI_MVC.Hubs.Impl
{
    /// <summary>
    /// This hub contains all methods that clients can call related to activity feed
    /// </summary>
    public class ActivityHub : Hub<IActivityHub>
    {
        public async Task JoinPage(string platform)
        {
            await Groups.AddToGroupAsync(Context.ConnectionId, $"activity - {platform}");
        }

        public async Task LeavePage(string platform)
        {
            await Groups.RemoveFromGroupAsync(Context.ConnectionId, $"activity - {platform}");
        }
    }
}
