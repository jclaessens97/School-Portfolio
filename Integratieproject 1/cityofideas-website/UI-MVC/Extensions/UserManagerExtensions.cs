using COI.BL.Domain.User;
using Microsoft.AspNetCore.Identity;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using Project = COI.BL.Domain.Project.Project;

namespace COI.UI_MVC.Extensions
{
    public static class UserManagerExtensions
    {
        public static bool userHasClaim(this UserManager<User> mgr, User user, string claimToCheck)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Type == claimToCheck)
                {
                    return true;
                }
            }

            return false;
        }
        
        public static bool IsUserUserOrAbove(this UserManager<User> mgr, User user,string tenant)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Value == "Moderator" &&  claim.Type == tenant || claim.Value == "Admin" &&  claim.Type == tenant || claim.Type == "SuperAdmin" ||
                    claim.Value == "User" &&  claim.Type == tenant)
                {
                    return true;
                }
            }

            return false;
        }

        public static bool IsUserModOrAbove(this UserManager<User> mgr, User user, string tenant)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Value == "Moderator" &&  claim.Type == tenant|| claim.Value == "Admin" &&  claim.Type == tenant || claim.Value == "SuperAdmin")
                {
                    return true;
                }
            }

            return false;
        }
        
        public static bool IsUserModOrAboveForProject(this UserManager<User> mgr, User user,Project project,string tenant)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Value == "Admin" &&  claim.Type == tenant|| claim.Type == "SuperAdmin")
                {
                    return true;
                }
                if (claim.Value == "Moderator" &&  claim.Type == tenant)
                {
                    return project.Moderators.Select(m => m.User).Contains(user);
                }
            }

            return false;
        }

        public static bool IsUserAdminOrAbove(this UserManager<User> mgr, User user, string tenant)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Value == "Admin" && claim.Type == tenant || claim.Value == "SuperAdmin")
                {
                    return true;
                }
            }

            return false;
        }

        public static bool IsUserSuperAdmin(this UserManager<User> mgr, User user)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Type == "SuperAdmin")
                {
                    return true;
                }
            }

            return false;
        }
        
        public static bool IsUserOrganisation(this UserManager<User> mgr, User user)
        {
            IList<Claim> claims = mgr.GetClaimsAsync(user).Result;

            foreach (Claim claim in claims)
            {
                if (claim.Type == "Organisation")
                {
                    return true;
                }
            }

            return false;
        }
    }
}