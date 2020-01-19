using COI.BL.Domain.Form;
using COI.BL.Domain.Ideation;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class IoTRepository : IIoTRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public IoTRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        public IotLink ReadIoTLink(int id)
        {
            return _ctx.IotLinks
                .Include(i => i.Location)
                .Include(i => i.IdeationReply)
                .ThenInclude(ir => ir.Votes)
                .Include(i => i.Form)
                .ThenInclude(f => f.Replies)
                .FirstOrDefault(i => i.IotLinkId == id);
        }

        public IotLink ReadIoTLinkByForm(Form form)
        {
            return _ctx.IotLinks
                .Include(i => i.Form)
                .Include(i => i.Location)
                .FirstOrDefault(i => i.Form == form);
        }

        public IotLink ReadIoTLinkByIdeationReply(IdeationReply reply)
        {
            return _ctx.IotLinks
                .Include(i => i.IdeationReply)
                .Include(i => i.Location)
                .FirstOrDefault(i => i.IdeationReply == reply);
        }

        public IEnumerable<IotLink> ReadIoTLinkByProject(int projectId)
        {
            return _ctx.IotLinks
                .Include(l => l.Location)
                .Include(l => l.Project)
                .Include(l => l.Form)
                .ThenInclude(f => f.Questions)
                .Include(l => l.Form)
                .ThenInclude(f => f.Replies)
                .ThenInclude(r => r.Answers)
                .Include(l => l.IdeationReply)
                .ThenInclude(r => r.Ideation)
                .Include(l => l.IdeationReply)
                .ThenInclude(r => r.Votes)
                .Where(l => l.Project.ProjectId == projectId);
        }

        public IEnumerable<IotLink> ReadIoTLinkByPlatform(int platformId)
        {
            return _ctx.IotLinks
                .Include(l => l.Location)
                .Include(l => l.Project)
                .Include(l => l.Form)
                .ThenInclude(f => f.Questions)
                .Include(l => l.Form)
                .ThenInclude(f => f.Replies)
                .ThenInclude(r => r.Answers)
                .Include(l => l.IdeationReply)
                .ThenInclude(r => r.Ideation)
                .Include(l => l.IdeationReply)
                .ThenInclude(r => r.Votes)
                .Where(l => l.Project.Platform.PlatformId == platformId);
        }

        public IotLink CreateIotLink(IotLink iotLink)
        {
            _ctx.IotLinks.Add(iotLink);
            _ctx.SaveChanges();
            return iotLink;
        }

        public IotLink UpdateIotLink(IotLink iotLink)
        {
            _ctx.IotLinks.Update(iotLink);
            _ctx.SaveChanges();
            return iotLink;
        }

        public void DeleteLink(int id)
        {
            IotLink link = _ctx.IotLinks.Find(id);
            _ctx.IotLinks.Remove(link);
            _ctx.SaveChanges();
        }

        public int ReadIotCountByProject(int projectId)
        {
            return _ctx.IotLinks
                .Count(l => l.Project.ProjectId == projectId);
        }

        public int ReadIotCountByPlatform(int platformId)
        {
            return _ctx.IotLinks
                .Count(l => l.Project.Platform.PlatformId == platformId);
        }

        public int ReadIotCount()
        {
            return _ctx.IotLinks.Count();
        }
    }
}