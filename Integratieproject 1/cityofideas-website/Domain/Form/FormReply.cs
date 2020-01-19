using System.Collections.Generic;

namespace COI.BL.Domain.Form
{
    public class FormReply
    {
        public int FormReplyId { get; set; }
        public List<Answer.Answer> Answers { get; set; }
        public Form Form { get; set; }

        public string Email { get; set; }
        public User.User User { get; set; }
        public bool Anonymous { get; set; }

        public FormReply()
        {
            Answers = new List<Answer.Answer>();
        }
    }
}