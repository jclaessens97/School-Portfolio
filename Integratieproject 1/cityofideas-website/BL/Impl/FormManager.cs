using COI.BL.Domain.Form;
using COI.DAL;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore.Metadata.Internal;

namespace COI.BL.Impl
{
    public class FormManager : IFormManager
    {
        private readonly IFormRepository _formRepository;

        public FormManager(IFormRepository formRepository)
        {
            _formRepository = formRepository;
        }

        #region Forms
        public Form GetForm(int formId)
        {
            Form form = _formRepository.ReadForm(formId);
            form.Questions.Sort();
            return form;
        }

        public IEnumerable<Form> GetForms()
        {
            return _formRepository.ReadAllForms();
        }

        public IEnumerable<Form> GetAllStatementForms(int projectId)
        {
            return _formRepository.ReadAllStatementForms(projectId);
        }

        public Form AddForm(Form form)
        {
            return _formRepository.CreateForm(form);
        }

        public void UpdateForm(Form form)
        {
            _formRepository.UpdateForm(form);
        }

        #endregion

        #region Form Replies
        public FormReply AddFormReply(FormReply formReply)
        {
            return _formRepository.CreateFormReply(formReply);
        }

        public IEnumerable<FormReply> GetFormReplies(int id)
        {
            return _formRepository.ReadFormReplies(id);
        }

        public int GetFormReplyCount(int id)
        {
            return _formRepository.ReadFormReplyCount(id);
        }

        public FormReply GetFormReply(int formReplyId)
        {
            return _formRepository.ReadFormReply(formReplyId);
        }
        #endregion
    }
}