using COI.BL.Domain.User;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class PlatformRequestManager : IPlatformRequestManager
    {
        private readonly IPlatformRequestRepository _platformRequestRepository;

        public PlatformRequestManager(IPlatformRequestRepository platformRequestRepository)
        {
            _platformRequestRepository = platformRequestRepository;
        }

        public void CreatePlatformRequest(PlatformRequest platformRequest)
        {
            _platformRequestRepository.CreatePlatformRequest(platformRequest);
        }
        public IEnumerable<PlatformRequest> Get()
        {
            var request = _platformRequestRepository.Get();
            return request;
        }
        
        public IEnumerable<PlatformRequest> GetUntreated()
        {
            var request = _platformRequestRepository.GetUntreated();
            return request;
        }
        
        public IEnumerable<PlatformRequest> GetTreated()
        {
            var request = _platformRequestRepository.GetTreated();
            return request;
        }

        public void Update(PlatformRequest platformRequest)
        {
            _platformRequestRepository.Update(platformRequest);
        }
    }
}