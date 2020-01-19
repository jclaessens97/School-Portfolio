using COI.BL;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Models;
using COI.UI_MVC.Models.dto;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using COI.UI_MVC.Hubs;
using COI.UI_MVC.Hubs.Impl;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.SignalR;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Controllers.API
{
    [Route("api/[controller]")]
    [ApiController]
    public class FormsController  : ControllerBase
    {
        private readonly IFormManager _formManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly IHubContext<VoteHub, IVoteHub> _voteHubContext;
          
        public FormsController(
            [FromServices] IFormManager formManager, 
            [FromServices] UnitOfWorkManager unitOfWorkManager,
            [FromServices] IHubContext<VoteHub, IVoteHub>  voteHubContext
        )
        {
            _formManager = formManager;
            _unitOfWorkManager = unitOfWorkManager;
            _voteHubContext = voteHubContext;
        }

        [HttpGet("Replies/{id}")]
        public IActionResult GetFormReply(int id)
        {
            FormReply reply = _formManager.GetFormReply(id);

            if (reply == null)
            {
                return NotFound();
            }

            return Ok(reply);
        }

        [HttpGet("All")]
        public IActionResult GetAllForms()
        {
            var forms = _formManager.GetForms();

            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            if (subdomain != null)
            {
                forms = forms.Where(p => p.Project.Platform.Tenant == subdomain).AsEnumerable();
            }
            
            if (forms == null)
            {
                return NotFound();
            }
          
            return Ok(forms);
        }
        
        [HttpGet("{id}")]
        public IActionResult GetForm(int id)
        {
            Form form = _formManager.GetForm(id);

            if (form == null)
            {
                return NotFound();
            }
            
            return Ok(form);
        }

        [HttpGet("results/{id}")]
        public IActionResult GetFormResult(int id)
        {
            Form form = _formManager.GetForm(id);
            if (form ==null)
            {
                return NotFound();
            }
            List<FormResultDTO> dto = new List<FormResultDTO>();

            foreach (var question in form.Questions)
            {
                int questionIndex = question.OrderIndex;
                List<Answer> answers = form.Replies.Select(r => r.Answers.FirstOrDefault(a => a.QuestionIndex == questionIndex)).Where(a => a != null).ToList();
                FormResultDTO resultDto = new FormResultDTO()
                {
                    Question = question,
                    Answers = answers
                };
                dto.Add(resultDto);
            }

            return Ok(dto);
        }

        [HttpGet("results/overview{id}")]
        public IActionResult GetFormResultOverview(int id)
        {
            return Ok();
        }

        [HttpPost("postReply")]
        public IActionResult Reply(FormReplyDTO formReply)
        {
            FormReply reply = new FormReply()
            {
                Answers = new List<Answer>(),
                Email = formReply.Email
            };
            
            foreach (FormAnswerDTO formAnswer in formReply.Answers)
            {
                Answer answer;

                switch (formAnswer.FieldType)
                {
                        case FieldType.OpenText:
                            answer = new OpenTextAnswer()
                            {
                                Value = formAnswer.Reply
                            };
                            break;
                        case FieldType.MultipleChoice:
                            answer = new MultipleChoiceAnswer()
                            {
                                SelectedChoices = formAnswer.MultipleAnswer.ToList()
                            };
                            break;
                        case FieldType.DropDown:
                        case FieldType.SingleChoice:
                            answer = new SingleChoiceAnswer()
                            {
                                SelectedChoice = formAnswer.SelectedChoice
                            };
                            break;
                    case FieldType.Statement:
                        answer = new SingleChoiceAnswer()
                        {
                            SelectedChoice = formAnswer.SelectedChoice
                        };
                        break;
                        default:
                            throw new NotSupportedException("Fieldtype not supported yet");
                }
                reply.Answers.Add(answer);
            }

            _formManager.AddFormReply(reply);
            _unitOfWorkManager.Save();

            return Ok();
        }

        [HttpGet("all/statement/{id}")]
        public IActionResult GetAllStatementForms(int id, [FromServices] IIoTManager ioTManager)
        {
            List<Form> forms = _formManager.GetAllStatementForms(id).ToList();
            
            List<FormViewModel> formViewModels = new List<FormViewModel>();

            foreach (Form form in forms)
            {
                IotLink iotLink = ioTManager.GetIoTLinkByForm(form);
                FormViewModel formViewModel = new FormViewModel()
                {
                    Title = form.Title,
                    FormId = form.FormId,
                    Questions = new List<FormQuestionViewModel>(),
                    IotLink = iotLink
                };

                FormQuestionViewModel question = new FormQuestionViewModel()
                {
                    Question = form.Questions[0].QuestionString
                };

                formViewModel.Questions.Add(question);
                formViewModels.Add(formViewModel);
            }
            
            return Ok(formViewModels);
        }

        [HttpPost]
        public IActionResult PostForm(FormDTO formDto, [FromServices] IProjectManager projectManager)
        {
            Project project = projectManager.GetProject(formDto.ProjectId);
            Phase phase = project.Phases.FirstOrDefault(p => p.PhaseId == formDto.PhaseId);
            Form newform = new Form()
            {
                Title = formDto.FormTitle,
                Questions = new List<Question>(),
                Replies = new List<FormReply>(),
                IsStatementForm = formDto.IsStatementForm,
                Project = project,
                Phase = phase
            };

            int index = 0;
            foreach (QuestionDTO question in formDto.Questions)
            {
                Question newQuestion = new Question()
                {
                    FieldType = question.Type,
                    Options = new List<string>(),
                    QuestionString = question.Question,
                    Required = question.Required,
                    LongAnswer = question.LongAnswer,
                    OrderIndex = index
                };
                foreach (var option in question.Options)
                {
                    newQuestion.Options.Add(option.String);
                }
                newform.Questions.Add(newQuestion);
                index++;
            }

            _formManager.AddForm(newform);
            
            _unitOfWorkManager.Save();
            return Created("/form/reply/" + newform.FormId,new { newform.FormId});
        }


        #region App
        [HttpGet("/app/All")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public IActionResult GetAllFormsApp()
        {
            var forms = _formManager.GetForms();

            if (forms == null)
            {
                return NotFound();
            }
          
            return Ok(forms);
        }

        #endregion
        
        [HttpPost("vote/{id}/{vote}")]
        public IActionResult Vote(int id, int vote, [FromServices] UserManager<User> userManager)
        {
            Form form = _formManager.GetForm(id);
            if (form == null)
            {
                return NotFound();
            }
            User user = userManager.GetUserAsync(User).Result;
            bool anonymous = false;
            anonymous = user == null;
            FormReply reply = new FormReply()
            {
                User = user,
                Anonymous = anonymous,
                Answers = new List<Answer>(),
                Form = form,
            };
            Answer answer = new SingleChoiceAnswer()
            {
                OrderIndex = 0,
                QuestionIndex = 0,
                SelectedChoice = vote
            };
            reply.Answers.Add(answer);
            _formManager.AddFormReply(reply);
            //form.Replies.Add(reply);
            _unitOfWorkManager.Save();
            
            int upvotes = form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 1);
            int downvotes = form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 0);



            _voteHubContext.Clients.Group($"form - {id}").ReceiveUpvote();

            
            
            return Created("",new {upvotes = upvotes, downvotes = downvotes});
        }

        [HttpGet("votes/{id}")]
        public IActionResult getVotes(int id)
        {
            Form form = _formManager.GetForm(id);
            if (form == null)
            {
                return NotFound();
            }
            int upvotes = form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 1);
            int downvotes = form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 0);
            return Ok(new {title = form.Questions[0].QuestionString,upvotes = upvotes, downvotes = downvotes});
        }
    }
}