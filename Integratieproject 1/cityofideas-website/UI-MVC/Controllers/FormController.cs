using COI.BL;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Project;
using COI.UI_MVC.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using Google.Api.Gax;
using Platform = COI.BL.Domain.Platform.Platform;

namespace COI.UI_MVC.Controllers
{
    public class FormController : Controller
    {
        private readonly IFormManager _formManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;

        public FormController(
            [FromServices] IFormManager formManager, 
            [FromServices] UnitOfWorkManager unitOfWorkManager
        )
        {
            _formManager = formManager;
            _unitOfWorkManager = unitOfWorkManager;
        }

        // GET
        [HttpGet]
        public IActionResult Create(int id, [FromServices]IProjectManager projectManager)
        {
            Project project = projectManager.GetProject(id);

            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            if (project == null)
            {
                return RedirectToAction("NotFound", "Home");
            }
            var projectSubDomain = project.Platform.Tenant;
            if (subdomain != projectSubDomain)
            {
                return RedirectToAction("NotFound", "Home");
            }

            var createFormViewModel = new CreateFormViewModel()
            {
                ProjectId = project.ProjectId,
                Phases = project.Phases,
                ProjectTitle = project.Title
            };
            _unitOfWorkManager.Save();

            return View(createFormViewModel);
        }
       

        public IActionResult Reply(int id)
        {
            Form form = _formManager.GetForm(id);
            if (form == null)
            {
                return RedirectToAction("NotFound", "Home");
            }
            
            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var formSubDomain = form.Project.Platform.Tenant;
            if (subdomain != formSubDomain)
            {
                return NotFound();
            }

            ViewBag.ProjectId = form.Project.ProjectId;
            
            var formVM = new FormViewModel()
            {
                FormId = form.FormId,
                Title = form.Title,
                Questions = new List<FormQuestionViewModel>()
            };

            foreach (Question question in form.Questions)
            {
                var questionVM = new FormQuestionViewModel()
                {
                    Question = question.QuestionString,
                    FieldType = question.FieldType,
                    Options = question.Options,
                    Required = question.Required,
                    LongAnswer = question.LongAnswer
                };

                formVM.Questions.Add(questionVM);
            }
            _unitOfWorkManager.Save();

            return View(formVM);
        }

        [HttpPost]
        public IActionResult Reply(FormViewModel formVM)
        {
            if (!ModelState.IsValid)
            {
                return View(formVM);
            }

            Form form = _formManager.GetForm(formVM.FormId);
            
            FormReply reply = new FormReply()
            {
                Answers = new List<Answer>(),
                Email = formVM.Email.Email,
                Form = form
            };

            int index = 0;
            foreach (FormQuestionViewModel questionVM in formVM.Questions)
            {
                Answer answer;
                bool isValid = true;
                switch (questionVM.FieldType)
                {
                        case FieldType.OpenText:
                            isValid = questionVM.OpenAnswer != null;
                            answer = new OpenTextAnswer()
                            {
                                QuestionIndex = index,
                                OrderIndex = index,
                                Value = questionVM.OpenAnswer
                            };
                            break;
                        case FieldType.MultipleChoice:
                            isValid = questionVM.MultipleChoiceAnswer.Contains(true);
                            answer = new MultipleChoiceAnswer()
                            {
                                QuestionIndex = index,
                                OrderIndex = index,
                                SelectedChoices = questionVM.MultipleChoiceAnswer.ToList()
                            };
                            break;
                        case FieldType.DropDown:
                        case FieldType.SingleChoice:
                        case FieldType.Statement:
                            if (questionVM.SingleChoiceAnswer.HasValue)
                            {
                                answer = new SingleChoiceAnswer()
                                {
                                    QuestionIndex = index,
                                    OrderIndex = index,
                                    SelectedChoice = questionVM.SingleChoiceAnswer.Value
                                };
                            }
                            else
                            {
                                answer = null;
                                isValid = false;
                            }
                            
                            break;
                        default:
                            throw new NotSupportedException("Fieldtype not supported yet");
                }

                if (isValid)
                {
                    reply.Answers.Add(answer);
                }
                index++;
            }
            _formManager.AddFormReply(reply);
            _unitOfWorkManager.Save();

            return RedirectToAction("Confirmation", new {email = reply.Email});
        }

        [HttpGet]
        public IActionResult Confirmation(string email)
        {
            ViewBag.email = email;
            return View();
        }


        [HttpGet]
        public IActionResult Result(int id)
        {
            Form form = _formManager.GetForm(id);
            return View(form);
        }

        [HttpGet]
        public IActionResult ResultOverview(int id,[FromServices] IPlatformManager platformManager)
        {
            string tenant = HttpContext.Request.Host.Host.Split(".")[0];
            Platform platform = platformManager.GetPlatformByTenantWithForms(tenant);
            if (platform == null)
            {
                return RedirectToAction("NotFound", "Home");
            }
            List<Project> projects = platform.Projects;
            
            List<FormProjectsViewModel> projectsVm = new List<FormProjectsViewModel>();

            foreach (var project in projects)
            {
                List<Form> forms = project.Phases.SelectMany(p => p.Forms).ToList();
                
                FormProjectsViewModel vm = new FormProjectsViewModel()
                {
                    ProjectId = project.ProjectId,
                    Title = project.Title
                };

                foreach (var form in forms)
                {
                    FormResultsViewModel formVm = new FormResultsViewModel()
                    {
                        FormId = form.FormId,
                        Title = form.Title,
                        AnswerCount = _formManager.GetFormReplyCount(form.FormId)
                    };
                    vm.forms.Add(formVm);
                }
                projectsVm.Add(vm);
            }
            
            
            return View(projectsVm);
        }
    }
}