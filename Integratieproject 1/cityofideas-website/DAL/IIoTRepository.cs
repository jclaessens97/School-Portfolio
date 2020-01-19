using COI.BL.Domain.Form;
using COI.BL.Domain.Ideation;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IIoTRepository
    {
        IotLink ReadIoTLink(int id);
        IotLink ReadIoTLinkByForm(Form form);
        IotLink ReadIoTLinkByIdeationReply(IdeationReply reply);
        IEnumerable<IotLink> ReadIoTLinkByProject(int projectId);
        IEnumerable<IotLink> ReadIoTLinkByPlatform(int platformId);

        IotLink CreateIotLink(IotLink iotLink);
        IotLink UpdateIotLink(IotLink iotLink);
        void DeleteLink(int id);
        int ReadIotCountByProject(int projectId);
        int ReadIotCountByPlatform(int platformId);
        int ReadIotCount();
    }
}