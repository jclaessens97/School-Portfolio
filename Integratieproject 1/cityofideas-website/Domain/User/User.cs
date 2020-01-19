using System;
using Microsoft.AspNetCore.Identity;

namespace COI.BL.Domain.User
{
    // Add profile data for application users by adding properties to the User class
    public class User : IdentityUser
    {
        [PersonalData] public string FirstName { get; set; }
        [PersonalData] public string LastName { get; set; }
        [PersonalData] public string FirmName { get; set; }
        [PersonalData] public string VATNumber { get; set; }
        [PersonalData] public DateTime BirthDay { get; set; }
        [PersonalData] public string Sex { get; set; }
        [PersonalData] public int ZipCode { get; set; }

        public string GetDisplayName()
        {
            if (LastName != null)
            {
                return string.Format("{0} {1}.", FirstName, LastName[0]);
            } else if (FirmName != null)
            {
                return FirmName;
            }
            else return UserName;
        }

        public string GetFullName()
        {
            return string.Format("{0} {1}", FirstName, LastName);
        }
    }
}