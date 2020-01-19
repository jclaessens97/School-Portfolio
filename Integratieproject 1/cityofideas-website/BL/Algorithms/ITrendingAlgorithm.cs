using COI.BL.Domain.Ideation;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace COI.BL.Algorithms
{
    public interface ITrendingAlgorithm
    {
        Task<IEnumerable<IdeationReply>> GetTrendingList(List<IdeationReply> replies);
    }
}
