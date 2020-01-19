using System.Threading;
using System.Threading.Tasks;

namespace UI_MVC.Scheduler.Scheduling
{
    public interface IScheduledTask
    {
        string Schedule { get; }
        Task ExecuteAsync(CancellationToken cancellationToken);
    }
}