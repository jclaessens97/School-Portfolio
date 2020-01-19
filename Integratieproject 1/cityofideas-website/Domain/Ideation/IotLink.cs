using System.Collections.Generic;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Ideation
{
    public class IotLink
    {
        public int IotLinkId { get; set; }
        public Form.Form Form { get; set; }
        public IdeationReply IdeationReply { get; set; }

        public Project.Project Project { get; set; } // Link to project for easy access

        public Location Location { get; set; }
        
        public void VoteUp(User.User user)
        {
            if (Form != null)
            {
                FormReply reply = new FormReply()
                {
                    Answers = new List<Answer.Answer>(),
                    User = user,
                    Anonymous = user == null,
                    Form = Form
                };
                Answer.Answer answer = new SingleChoiceAnswer()
                {
                    SelectedChoice = 1
                };
                reply.Answers.Add(answer);
                Form.Replies.Add(reply);
            }else if (IdeationReply != null)
            {
                IdeationReply.VoteUp(user);
            }
        }

        public void VoteDown(User.User user)
        {
            if (Form != null)
            {
                FormReply reply = new FormReply()
                {
                    Answers = new List<Answer.Answer>(),
                    User = user,
                    Anonymous = user == null,
                    Form = Form
                };
                Answer.Answer answer = new SingleChoiceAnswer()
                {
                    SelectedChoice = 0,
                    OrderIndex = 0,
                    QuestionIndex = 0,
                };
                reply.Answers.Add(answer);
                Form.Replies.Add(reply);
            }else if (IdeationReply != null)
            {
                IdeationReply.VoteDown(user);
            }
        }
    }
}