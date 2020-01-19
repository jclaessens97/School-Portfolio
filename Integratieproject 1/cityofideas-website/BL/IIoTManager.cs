using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Project;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IIoTManager
    {
        IotLink GetIoTLink(int id);
        IotLink GetIoTLinkByForm(Form form);
        IotLink GetIoTLinkByIdeationReply(IdeationReply reply);
        IEnumerable<IotLink> GetIotLinksByProject(int projectId);
        IEnumerable<IotLink> GetIotLinksByPlatform(int platformId);
        int GetIotCountByProject(int projectId);
        int GetIotCountByPlatform(int platformId);
        int GetIotCount();

        IotLink CreateIotLink(Form form, IdeationReply ideationReply,Project project,Location location);
        IotLink UpdateIotLink(IotLink iotLink);
        void DeleteLink(int id);
    }
}