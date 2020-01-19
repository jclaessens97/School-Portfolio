using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Project;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class IoTManager : IIoTManager
    {
        private readonly IIoTRepository _ioTRepository;
        
        public IoTManager(IIoTRepository ioTRepository)
        {
            _ioTRepository = ioTRepository;
        }
        
        public IotLink GetIoTLink(int id)
        {
            return _ioTRepository.ReadIoTLink(id);
        }

        public IotLink GetIoTLinkByForm(Form form)
        {
            return _ioTRepository.ReadIoTLinkByForm(form);
        }

        public IotLink GetIoTLinkByIdeationReply(IdeationReply reply)
        {
            return _ioTRepository.ReadIoTLinkByIdeationReply(reply);
        }

        public IEnumerable<IotLink> GetIotLinksByProject(int projectId)
        {
            return _ioTRepository.ReadIoTLinkByProject(projectId);
        }

        public IEnumerable<IotLink> GetIotLinksByPlatform(int platformId)
        {
            return _ioTRepository.ReadIoTLinkByPlatform(platformId);
        }

        public int GetIotCountByProject(int projectId)
        {
            return _ioTRepository.ReadIotCountByProject(projectId);
        }

        public int GetIotCountByPlatform(int platformId)
        {
            return _ioTRepository.ReadIotCountByPlatform(platformId);
        }

        public int GetIotCount()
        {
            return _ioTRepository.ReadIotCount();
        }

        public IotLink CreateIotLink(Form form, IdeationReply ideationReply,Project project,Location location)
        {
            IotLink iotLink = new IotLink()
            {
                Form = form,
                IdeationReply = ideationReply,
                Project = project,
                Location = location,
                
            };
            return _ioTRepository.CreateIotLink(iotLink);
        }

        public IotLink UpdateIotLink(IotLink iotLink)
        {
            return _ioTRepository.UpdateIotLink(iotLink);
        }

        public void DeleteLink(int id)
        {
            _ioTRepository.DeleteLink(id);
        }
    }
}