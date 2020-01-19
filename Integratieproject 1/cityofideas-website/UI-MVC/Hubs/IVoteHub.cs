using System.Threading.Tasks;

namespace COI.UI_MVC.Hubs
{
    /// <summary>
    /// Defines contract with clientside websocket methods
    /// </summary>
    public interface IVoteHub
    {
        Task ReceiveUpvote();
        Task ReceiveDownvote();
    }
}
