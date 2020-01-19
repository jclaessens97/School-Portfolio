using COI.BL.Domain.Activity;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Platform;
using COI.BL.Domain.User;
using System;

namespace COI.BL.Application
{
    public interface IVoteController
    {
        Tuple<IotLinkType, int, Activity> VoteUp(int iotId, User user = null);
        Tuple<IotLinkType, int, Activity> VoteDown(int iotId, User user = null);
        Platform GetPlatformByIotLinkId(int iotLinkId);
    }
}