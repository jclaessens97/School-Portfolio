using COI.BL.Domain.Form;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore.Metadata.Internal;

namespace COI.BL
{
    public interface IFormManager
    {
        // Forms
        Form GetForm(int formId);
        IEnumerable<Form> GetForms();
        IEnumerable<Form> GetAllStatementForms(int projectId);
        Form AddForm(Form form);
        void UpdateForm(Form form);

        // Form Replies
        FormReply GetFormReply(int formReplyId);
        FormReply AddFormReply(FormReply formReply);
        IEnumerable<FormReply> GetFormReplies(int id);
        int GetFormReplyCount(int id);
    }
}