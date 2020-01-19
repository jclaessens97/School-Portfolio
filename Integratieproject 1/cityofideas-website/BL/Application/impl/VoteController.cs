using COI.BL.Domain.Activity;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Platform;
using COI.BL.Domain.User;
using System;

namespace COI.BL.Application
{
    public class VoteController : IVoteController
    {
        private readonly IIoTManager _ioTManager;
        private readonly IIdeationManager _ideationManager;
        private readonly IFormManager _formManager;
        private readonly IPlatformManager _platformManager;
        private readonly IActivityManager _activityManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;

        public VoteController(
            IIoTManager ioTManager,
            IIdeationManager ideationManager, 
            IFormManager formManager, 
            IPlatformManager platformManager,
            IActivityManager activityManager,
            UnitOfWorkManager unitOfWorkManager
        )
        {
            _ioTManager = ioTManager;
            _ideationManager = ideationManager;
            _formManager = formManager;
            _platformManager = platformManager;
            _activityManager = activityManager;
            _unitOfWorkManager = unitOfWorkManager;
        }

        public Tuple<IotLinkType, int, Activity> VoteUp(int iotId, User user = null)
        {
            IotLink link = _ioTManager.GetIoTLink(iotId);
            link.VoteUp(user);

            Activity activity = null;
            activity = CreateActivity(link);
            if (link.Form == null)
            {
                activity.IdeationReply = link.IdeationReply;
            } else
            {
                activity.Form = link.Form;
            }

            var linkTypeAndId = UpdateIot(link);
            return new Tuple<IotLinkType, int, Activity>(linkTypeAndId.Item1, linkTypeAndId.Item2, activity);
        }

        public Tuple<IotLinkType, int, Activity> VoteDown(int iotId, User user = null)
        {
            IotLink link = _ioTManager.GetIoTLink(iotId);
            link.VoteDown(user);

            Activity activity = null;
            activity = CreateActivity(link);
            if (link.Form == null)
            {
                activity.IdeationReply = link.IdeationReply;
            }
            else
            {
                activity.Form = link.Form;
            }

            var linkTypeAndId = UpdateIot(link);
            return new Tuple<IotLinkType, int, Activity>(linkTypeAndId.Item1, linkTypeAndId.Item2, activity);
        }

        public Platform GetPlatformByIotLinkId(int iotLinkId)
        {
            var iotLink = _ioTManager.GetIoTLink(iotLinkId);

            if (iotLink.Form == null)
            {
                return _platformManager.GetPlatformByReplyId(iotLink.IdeationReply.IdeationReplyId);
            }
            else
            {
                throw new NotImplementedException("Get Platform for form id not implemented");
            }

        }

        #region Helpers
        private Tuple<IotLinkType, int> UpdateIot(IotLink link)
        {
            IotLinkType iotLinkType;
            int replyId;

            if (link.Form == null)
            {
                iotLinkType = IotLinkType.Ideation;
                replyId = link.IdeationReply.IdeationReplyId;
            }
            else
            {
                iotLinkType = IotLinkType.Form;
                replyId = link.Form.FormId;
            }

            switch (iotLinkType)
            {
                case IotLinkType.Ideation:
                    _ideationManager.UpdateReply(link.IdeationReply);
                    break;
                case IotLinkType.Form:
                    _formManager.UpdateForm(link.Form);
                    break;
            }

            try
            {
                _unitOfWorkManager.FixUnchangedEntries(); // only used one time where 2 dbcontexts were unavoidable -> UI.MVC/scheduler
                _unitOfWorkManager.Save();
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }

            return new Tuple<IotLinkType, int>(iotLinkType, replyId);
        }

        private Activity CreateActivity(IotLink link)
        {
            ActivityType activityType;
            Platform platform;

            if (link.Form == null)
            {
                activityType = ActivityType.IdeationVote;
                platform = link.IdeationReply.Ideation.Project.Platform;
            }
            else
            {
                activityType = ActivityType.FormVote;
                platform = link.Form.Project.Platform;
            }

            var activity = new Activity()
            {
                ActivityTime = DateTime.Now,
                ActivityType = activityType,
                User = null,
                Platform = platform
            };

            return activity;
        }
        #endregion
    }
}