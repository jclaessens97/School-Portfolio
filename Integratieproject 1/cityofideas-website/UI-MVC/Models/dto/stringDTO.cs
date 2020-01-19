using COI.UI_MVC.Models.Users;
using System.Collections.Generic;
using System.Security.Claims;

namespace COI.UI_MVC.Models.dto
{
    public class stringDTO
    {
        public string text { get; set; }
        public IList<Claim> userClaim { get; set; }
        public  List<UsersWithClaims> list { get; set; }
        public int id { get; set; }
    }
}