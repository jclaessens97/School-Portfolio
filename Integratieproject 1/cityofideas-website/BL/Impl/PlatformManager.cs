using COI.BL.Domain.Platform;
using COI.DAL;
using System.Collections.Generic;

namespace COI.BL.Impl
{
    public class PlatformManager : IPlatformManager
    {
        private readonly IPlatformRepository _platformRepository;

        public PlatformManager(IPlatformRepository platformRepository)
        {
            _platformRepository = platformRepository;
        }

        public Platform GetPlatform(int platformId)
        {
            return _platformRepository.ReadPlatform(platformId);
        }

        public Platform GetPlatformByTenant(string tenant)
        {
            return _platformRepository.ReadPlatformByTenant(tenant);
        }

        public Platform GetPlatformByTenantWithForms(string tenant)
        {
            return _platformRepository.ReadPlatformByTenantWithForms(tenant);
        }

        public Platform GetPlatformByReplyId(int replyId)
        {
            return _platformRepository.ReadPlatformByReplyId(replyId);
        }

        public IEnumerable<Platform> GetPlatforms()
        {
            return _platformRepository.ReadAllPlatforms();
        }

        public IEnumerable<Platform> GetPlatforms(int skip, int take)
        {
            return _platformRepository.ReadPlatforms(skip, take);
        }

        public void AddPlatform(Platform platform)
        {
            _platformRepository.CreatePlatform(platform);
        }

        public void UpdatePlatform(Platform platform)
        {
            _platformRepository.UpdatePlatform(platform);
        }

        public IEnumerable<Platform> SearchPlatforms(string query, int minLength = 3)
        {
            if (query != null && query.Length >= minLength)
            {
                return _platformRepository.SearchPlatforms(query);
            }

            return new List<Platform>();
        }
    }
}
