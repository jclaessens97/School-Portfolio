using COI.BL.Domain.User;
using COI.DAL.EF;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class PlatformRequestRepository : IPlatformRequestRepository
    {
        private readonly CityOfIdeasDbContext _ctx;


        public PlatformRequestRepository(CityOfIdeasDbContext ctx)
        {
            _ctx = ctx;
        }    

        public void CreatePlatformRequest(PlatformRequest platformRequest)
        {
             _ctx.PlatformRequests.Add(platformRequest);
             _ctx.SaveChanges();
        }

        public IEnumerable<PlatformRequest> Get()
        {
            IEnumerable<PlatformRequest> request = _ctx.PlatformRequests;
            return request;
        }
        
        public IEnumerable<PlatformRequest> GetUntreated()
        {
            IEnumerable<PlatformRequest> request = _ctx.PlatformRequests.Where(x => !x.Treated);
            return request;
        }
        
        public IEnumerable<PlatformRequest> GetTreated()
        {
            IEnumerable<PlatformRequest> request = _ctx.PlatformRequests.Where(x => x.Treated);
            return request;
        }

        public void Update(PlatformRequest platformRequest)
        {
            _ctx.PlatformRequests.Update(platformRequest);
            _ctx.SaveChanges();
        }
    }
}