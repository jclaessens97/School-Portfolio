using COI.BL.Domain.User;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IVerifyRequestRepository
    {
        void CreateVerifyRequest(VerifyRequest request);
        IEnumerable<VerifyRequest> Get();
        IEnumerable<VerifyRequest> GetUntreated();
        void Update(VerifyRequest verifyRequest);
        IEnumerable<VerifyRequest> GetTreated();
    }
}