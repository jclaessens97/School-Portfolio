using COI.BL.Domain.User;
using COI.DAL.EF;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class VerifyRequestRepository : IVerifyRequestRepository
    {
        private readonly CityOfIdeasDbContext _ctx;


        public VerifyRequestRepository(CityOfIdeasDbContext ctx)
        {
            _ctx = ctx;
        }    

        public void CreateVerifyRequest(VerifyRequest request)
        {
             _ctx.VerifyRequests.Add(request);
             _ctx.SaveChanges();
        }

        public IEnumerable<VerifyRequest> Get()
        {
            IEnumerable<VerifyRequest> request = _ctx.VerifyRequests;
            return request;
        }
        
        public IEnumerable<VerifyRequest> GetUntreated()
        {
            IEnumerable<VerifyRequest> request = _ctx.VerifyRequests.Where(x => !x.Treated);
            return request;
        }
        
        public IEnumerable<VerifyRequest> GetTreated()
        {
            IEnumerable<VerifyRequest> request = _ctx.VerifyRequests.Where(x => x.Treated);
            return request;
        }

        public void Update(VerifyRequest verifyRequest)
        {
            _ctx.VerifyRequests.Update(verifyRequest);
            _ctx.SaveChanges();
        }
    }
}