using COI.BL.Domain.Platform;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IPlatformRepository
    {
        Platform ReadPlatform(int platformId);
        Platform ReadPlatformByTenant(string tenant);
        Platform ReadPlatformByTenantWithForms(string tenant);
        Platform ReadPlatformByReplyId(int replyId);
        IEnumerable<Platform> ReadAllPlatforms();
        IEnumerable<Platform> ReadPlatforms(int skip, int take);
        void CreatePlatform(Platform platform);
        void UpdatePlatform(Platform platform);
        IEnumerable<Platform> SearchPlatforms(string query);
    }
}