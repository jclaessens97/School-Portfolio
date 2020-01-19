using System;
using System.ComponentModel.DataAnnotations;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.User
{
    public class Person //: User
    {
        [Key] //Tijdelijk voor error
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public DateTime DateOfBirth { get; set; }
        public Gender Gender { get; set; }

        /*public override string GetName()
        {
            return FirstName + LastName;
        }*/
    }
}
