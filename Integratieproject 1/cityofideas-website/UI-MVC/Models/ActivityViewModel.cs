using COI.BL.Domain.Activity;
using COI.UI_MVC.Extensions;

namespace COI.UI_MVC.Models
{
    public class ActivityViewModel
    {
        private readonly Activity _activity;

        public string PlatformTenant => _activity.Platform.Tenant.ToLower();

        public string User
        {
            get
            {
                if (_activity.User != null)
                {
                    string name = _activity.User.GetDisplayName();
                    if (name == null)
                    {
                        {
                            return _activity.User.FirmName;
                        }
                    }
                }

                return "Anoniem";
            }
        }

        public string Action
        {
            get
            {
                switch (_activity.ActivityType)
                {
                    case ActivityType.IdeationVote:
                    case ActivityType.FormVote:
                        return "gestemd op";
                    case ActivityType.Comment:
                        return "gereageerd op";
                    case ActivityType.IdeationReply:
                        return "een idee gegeven op";
                }

                return string.Empty;
            }
        }

        public string Value
        {
            get
            {
                switch (_activity.ActivityType)
                {
                    case ActivityType.IdeationVote:
                        return _activity.Vote.IdeationReply.Ideation.CentralQuestion;
                    case ActivityType.FormVote:
                        return _activity.Form.Title;
                    case ActivityType.Comment:
                        return _activity.Comment.IdeationReply.Ideation.CentralQuestion;
                    case ActivityType.IdeationReply:
                        return _activity.IdeationReply.Ideation.CentralQuestion;
                    default:
                        return string.Empty;
                }
            }
        }

        public string GetLink()
        {
            switch (_activity.ActivityType)
            {
                case ActivityType.IdeationVote:
                    return "/ideation/view/" + _activity.Vote.IdeationReply.IdeationReplyId;
                case ActivityType.FormVote:
                    return _activity.Form.Title;
                case ActivityType.Comment:
                    return _activity.Comment.IdeationReply.Ideation.CentralQuestion;
                case ActivityType.IdeationReply:
                    return "/ideation/view/" + _activity.IdeationReply.IdeationReplyId;
                default:
                    return string.Empty;
            }
        }

        public string ActivityDate
        {
            get { return _activity.ActivityTime.FormatParasableDate(); }
        }

        public ActivityViewModel(Activity activity)
        {
            _activity = activity;
        }
    }
}