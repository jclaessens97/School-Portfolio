using System;
using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Identity;

namespace COI.BL.Domain.User
{
    public class Organisation : IdentityUser //: User
    {
        [PersonalData] public string FirstName { get; set; }

        [PersonalData] public string LastName { get; set; }

        public bool organisation { get; set; }


        /*[Key] //Tijdelijk voor error
        public string Name { get; set; }
        public string Description { get; set; }
        public string VATNumber { get; set; }*/ // To Identify a company from a person ??

        /*public override string GetName()
        {
            return Name;
        }*/
    }
}