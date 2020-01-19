using COI.BL.Domain.User;

namespace COI.UI_MVC.Models.dto
{
    public class VoteDTO
    {
        public int id { get; set; }
        public User User { get; set; }
    }
}