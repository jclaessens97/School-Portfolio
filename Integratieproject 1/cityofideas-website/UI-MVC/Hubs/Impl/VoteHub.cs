using COI.BL.Domain.Ideation;
using Microsoft.AspNetCore.SignalR;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authentication;

namespace COI.UI_MVC.Hubs.Impl
{
    /// <summary>
    /// This hub contains all methods that clients can call related to votes
    /// </summary>
    public class VoteHub : Hub<IVoteHub>
    {
        public void SendUpvote(IotLinkType iotLinkType, int replyId)
        {
            var groupName = string.Empty;

            switch (iotLinkType)
            {
                case IotLinkType.Form:
                    groupName = $"form - {replyId}";
                    break;
                case IotLinkType.Ideation:
                    groupName = $"ideationReply - {replyId}";
                    break;
            }

            Clients.GroupExcept(groupName, Context.ConnectionId).ReceiveUpvote();
        }

        public void SendDownvote(IotLinkType iotLinkType, int replyId)
        {
            var groupName = string.Empty;

            switch (iotLinkType)
            {
                case IotLinkType.Form:
                    groupName = $"form - {replyId}";
                    break;
                case IotLinkType.Ideation:
                    groupName = $"ideationReply - {replyId}";
                    break;
            }

            Clients.GroupExcept(groupName, Context.ConnectionId).ReceiveDownvote();
        }

        public async Task JoinPage(int replyId,IotLinkType iotLinkType = IotLinkType.Ideation)
        {
            switch (iotLinkType)
            {
                case IotLinkType.Form:
                    await Groups.AddToGroupAsync(Context.ConnectionId, $"form - {replyId}");
                    return;
                case IotLinkType.Ideation:
                    await Groups.AddToGroupAsync(Context.ConnectionId, $"ideationReply - {replyId}");
                    break;
            }
        }

        public async Task LeavePage(int replyId,IotLinkType iotLinkType = IotLinkType.Ideation)
        {
            switch (iotLinkType)
            {
                case IotLinkType.Form:
                    await Groups.RemoveFromGroupAsync(Context.ConnectionId, $"form - {replyId}");
                    return;
                case IotLinkType.Ideation:
                    await Groups.RemoveFromGroupAsync(Context.ConnectionId, $"ideationReply - {replyId}");
                    break;
            }
        }
    }
}
