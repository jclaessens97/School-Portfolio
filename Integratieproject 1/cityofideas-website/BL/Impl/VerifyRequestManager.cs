using COI.BL.Domain.User;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class VerifyRequestManager : IVerifyRequestManager
    {
        private readonly IVerifyRequestRepository _verifyRequestRepository;

        public VerifyRequestManager(IVerifyRequestRepository verifyRequestRepository)
        {
            _verifyRequestRepository = verifyRequestRepository;
        }

        public void CreateVerifyRequest(VerifyRequest verifyRequest)
        {
            _verifyRequestRepository.CreateVerifyRequest(verifyRequest);
        }
        public IEnumerable<VerifyRequest> Get()
        {
            var request = _verifyRequestRepository.Get();
            return request;
        }
        
        public IEnumerable<VerifyRequest> GetUntreated()
        {
            var request = _verifyRequestRepository.GetUntreated();
            return request;
        }
        
        public IEnumerable<VerifyRequest> GetTreated()
        {
            var request = _verifyRequestRepository.GetTreated();
            return request;
        }

        public void Update(VerifyRequest verifyRequest)
        {
            _verifyRequestRepository.Update(verifyRequest);
        }
    }
}