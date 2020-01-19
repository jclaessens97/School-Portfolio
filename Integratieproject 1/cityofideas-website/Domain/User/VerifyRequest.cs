using System;
using System.ComponentModel.DataAnnotations;

namespace COI.BL.Domain.User
{
    public class VerifyRequest
    {
        [Key]
        public int RequestId { get; set; }
        public string UserId { get; set; }
        public string UserName { get; set; }
        public string Reason { get; set; }
        public bool Treated { get; set; }
        public DateTime Date { get; set; }
        public string Answer { get; set; }
        public bool Accept { get; set; }
    }
}