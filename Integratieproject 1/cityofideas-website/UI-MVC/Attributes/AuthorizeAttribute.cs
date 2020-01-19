using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Authorization;
using System;

namespace COI.UI_MVC.Attributes
{
    public class OnlySuperAdminAttribute : AuthorizeAttribute
    {
        public OnlySuperAdminAttribute()
            :base(Enum.GetName(typeof(Policy), Util.Policy.SuperAdmins))
        {
        }
    }

    public class OnlyAdminAndAboveAttribute : AuthorizeAttribute
    {
        public OnlyAdminAndAboveAttribute()
            : base(Enum.GetName(typeof(Policy), Util.Policy.Admins))
        {
        }
    }

    public class OnlyModeratorAndAboveAttribute : AuthorizeAttribute
    {
        public OnlyModeratorAndAboveAttribute()
            : base(Enum.GetName(typeof(Policy), Util.Policy.Moderators))
        {
        }
    }

    public class OnlyUserAndAboveAttribute : AuthorizeAttribute
    {
        public OnlyUserAndAboveAttribute()
            : base(Enum.GetName(typeof(Policy), Util.Policy.Users))
        {
        }
    }
}
