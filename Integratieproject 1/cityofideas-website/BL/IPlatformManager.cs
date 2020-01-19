using COI.BL.Domain.Platform;
using System.Collections.Generic;

namespace COI.BL
{
    public interface IPlatformManager
    {
        Platform GetPlatform(int platformId);
        Platform GetPlatformByTenant(string tenant);
        Platform GetPlatformByTenantWithForms(string tenant);
        Platform GetPlatformByReplyId(int replyId);
        IEnumerable<Platform> GetPlatforms();
        IEnumerable<Platform> GetPlatforms(int skip, int take);
        void AddPlatform(Platform platform);
        void UpdatePlatform(Platform platform);
        IEnumerable<Platform> SearchPlatforms(string query, int minLength = 3);
    }
}
