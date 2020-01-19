using COI.BL.Domain.Form;
using COI.DAL.EF;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;

namespace COI.DAL.Impl
{
    public class FormRepository : IFormRepository
    {
        private readonly CityOfIdeasDbContext _ctx;

        public FormRepository(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            _ctx = cityOfIdeasDbContext;
        }

        #region Forms

        public Form ReadForm(int formId)
        {
            return _ctx
                .Forms
                .Include(f => f.Project)
                .ThenInclude(p => p.Platform)
                .Include(f => f.Project)
                .ThenInclude(p => p.Phases)
                .Include(f => f.Replies)
                .ThenInclude(r => r.Answers)
                .Include(f => f.Questions)
                .FirstOrDefault(f => f.FormId == formId);
        }

        public Form ReadFormWithAnswers(int formId)
        {
            return _ctx
                .Forms
                .Include(f => f.Replies)
                .ThenInclude(f => f.Answers)
                .FirstOrDefault(f => f.FormId == formId);
        }

        public IEnumerable<Form> ReadAllForms()
        {
            return _ctx
                .Forms
                .Include(f => f.Questions)
                .ToList();
        }

        public IEnumerable<Form> ReadAllStatementForms(int projectId)
        {
            return _ctx.Forms
                .Include(f => f.Questions)
                .Include(f => f.Project)
                .ThenInclude(p => p.Phases)
                .Where(f => f.IsStatementForm && f.Project.ProjectId == projectId);
        }

        public Form CreateForm(Form form)
        {
            _ctx.Forms.Add(form);
            _ctx.SaveChanges();
            return form;
        }

        public void UpdateForm(Form form)
        {
            _ctx.Forms.Update(form);
            _ctx.SaveChanges();
        }

        #endregion

        #region Form Replies

        public FormReply ReadFormReply(int formReplyId)
        {
            return _ctx
                .FormReplies
                .Find(formReplyId);
        }

        public FormReply CreateFormReply(FormReply formReply)
        {
            _ctx.FormReplies.Add(formReply);
            _ctx.SaveChanges();
            return formReply;
        }

        public IEnumerable<FormReply> ReadFormReplies(int id)
        {
            Form form = _ctx.Forms
                .Include(f => f.Replies)
                .ThenInclude(r => r.Answers)
                .FirstOrDefault(f => f.FormId == id);
            return form.Replies;
        }

        public int ReadFormReplyCount(int id)
        {
            return _ctx
                .FormReplies
                .Count(fr => fr.Form.FormId == id);
        }

        #endregion
    }
}