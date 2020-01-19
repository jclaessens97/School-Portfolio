using System;

namespace COI.UI_MVC.Models.dto
{
    public class UserDto
     {
         public string FirstName { get; set; }
         public string LastName { get; set; }
         public string UserName { get; set; }
         public string Email { get; set; }
         public string Password { get; set; }
         public string ConfirmPassword { get; set; }
         public bool Organisation { get; set; }
         public string FirmName { get; set; }
         public string Vat { get; set; }
         public bool SexM { get; set; }
         public bool SexV { get; set; }
         public string Sex { get; set; }
         public DateTime BirthDate { get; set; }
         public int Zipcode { get; set; }
         public bool Remember { get; set; }

         public bool Exists { get; set; }
     }
 }