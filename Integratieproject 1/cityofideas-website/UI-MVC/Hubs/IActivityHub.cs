using COI.UI_MVC.Models;
using System.Threading.Tasks;

namespace COI.UI_MVC.Hubs
{
    /// <summary>
    /// Defines contract with clientside websocket methods
    /// </summary>
    public interface IActivityHub
    {
        Task UpdateActivityFeed(ActivityViewModel activityVm);
    }
}
