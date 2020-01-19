using COI.BL.Domain.User;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IPlatformRequestManager
    {
      void CreatePlatformRequest(PlatformRequest platformRequest);
      IEnumerable<PlatformRequest> Get();
      IEnumerable<PlatformRequest> GetUntreated();
      void Update(PlatformRequest platformRequest);
      IEnumerable<PlatformRequest> GetTreated();
    }
}