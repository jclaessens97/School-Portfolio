using COI.BL.Domain.Form;
using System.Collections.Generic;

namespace COI.DAL
{
    public interface IFormRepository
    {
        // Forms
        Form ReadForm(int formId);
        Form ReadFormWithAnswers(int formId);
        IEnumerable<Form> ReadAllForms();
        IEnumerable<Form> ReadAllStatementForms(int projectId);
        Form CreateForm(Form form);
        void UpdateForm(Form form);

        // Form Replies
        FormReply ReadFormReply(int formReplyId);
        FormReply CreateFormReply(FormReply formReply);
        IEnumerable<FormReply> ReadFormReplies(int id);
        int ReadFormReplyCount(int id);
    }
}