using COI.BL.Domain.User;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IVerifyRequestManager
    {
      void CreateVerifyRequest(VerifyRequest verifyRequest);
      IEnumerable<VerifyRequest> Get();
      IEnumerable<VerifyRequest> GetUntreated();
      void Update(VerifyRequest verifyRequest);
      IEnumerable<VerifyRequest> GetTreated();
    }
}