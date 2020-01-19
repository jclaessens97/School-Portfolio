using COI.BL.Domain.Foundation;
using COI.UI_MVC.Models.dto;

namespace COI.UI_MVC.Models
{
    public class IdeationViewModel
    {
        public int IdeationId { get; set; }
        public string UserId { get; set; }
        public string CentralQuestion { get; set; }
        public string Description { get; set; }
        public string Url { get; set; }
        public IdeationType IdeationType { get; set; }
        public bool HasReplies { get; set; }
        public IoTDTO IotLink { get; set; }
    }
}