using System;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.Users
{
    public class UsersWithClaims
    {
        public string UserId { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public string CurrentClaim { get; set; }
        public bool LockOutEnabled { get; set; }
        public Array Claim { get; set; }
        public string FirmName { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public bool Organisation { get; set; }
    }
}