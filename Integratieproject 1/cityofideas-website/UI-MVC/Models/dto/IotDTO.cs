namespace COI.UI_MVC.Models.dto
{
    public class IoTDTO
    {
        public int IotLinkId { get; set; }
        public bool IsForm { get; set; }
        public int FormId { get; set; }
        public int IdeationId { get; set; }
        public LocationDTO Location { get; set; }

        public string Question { get; set; }
        public int UpVotes { get; set; }
        public int DownVotes { get; set; }
    }
}