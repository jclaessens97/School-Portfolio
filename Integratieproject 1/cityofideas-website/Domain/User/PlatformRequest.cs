using System;
using System.ComponentModel.DataAnnotations;

namespace COI.BL.Domain.User
{
    public class PlatformRequest
    {
        [Key]
        public int PlatformRequestId { get; set; }
        public string UserId { get; set; }
        public string OrganisationName { get; set; }
        public string Reason { get; set; }
        public bool  Treated { get; set; }
        public DateTime Date { get; set; }
        public string Answer { get; set; }
        public bool Accept { get; set; }
        public Platform.Platform Platform { get; set; }
    }
}