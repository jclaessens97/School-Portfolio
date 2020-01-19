namespace COI.UI_MVC.Models.Users
{
    public class UserViewModel
    {
        public string UserName { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string FirmName { get; set; }
        public string Email { get; set; }
        public bool IsMod { get; set; } // if he is mod for a certain project
        public bool IsOrganistation { get; set; }
    }
}