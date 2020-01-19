using COI.BL;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Extensions;
using COI.UI_MVC.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;

namespace COI.UI_MVC.Controllers
{
    public class IdeationController : Controller
    {
        private readonly IIdeationManager _ideationManager;
        private readonly IProjectManager _projectManager;
        private readonly UserManager<User> _userManager;

        public IdeationController(
            [FromServices] IIdeationManager ideationManager,
            [FromServices] IProjectManager projectManager,
            [FromServices] UserManager<User> userManager
        )
        {
            _ideationManager = ideationManager;
            _projectManager = projectManager;
            _userManager = userManager;
        }


        [HttpGet]
        public IActionResult Create(int id)
        {
            Project project = _projectManager.GetProject(id);
            if (project == null)
            {
                return RedirectToAction("NotFound", "Home");
            }

            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
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

            return View(createFormViewModel);
        }


        [HttpGet]
        [OnlyUserAndAbove]
        public IActionResult Reply(int id)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            Ideation ideation = _ideationManager.GetIdeationWithReplies(id);
            if (ideation == null)
            {
                return RedirectToAction("NotFound", "Home");
            }

            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var ideationSubDomain = ideation.Project.Platform.Tenant;
            if (subdomain != ideationSubDomain)
            {
                return RedirectToAction("NotFound", "Home");
            }

            ViewBag.ProjectId = ideation.Project.ProjectId;

            if (ideation.IdeationType == IdeationType.Admin)
            {
                User user = _userManager.GetUserAsync(User).Result;
                bool isAdmin = _userManager.IsUserAdminOrAbove(user,tenant);
                if (!isAdmin)
                {
                    return Unauthorized();
                }
            }

            var ideationViewModel = new IdeationViewModel()
            {
                IdeationId = ideation.IdeationId,
                CentralQuestion = ideation.CentralQuestion,
                Description = ideation.Description,
                Url = ideation.Url,
                UserId = _userManager.GetUserAsync(User).Result.Id
            };

            return View(ideationViewModel);
        }

        [HttpGet]
        public IActionResult Overview(int id)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var ideation = _ideationManager.GetIdeationWithReplies(id);

            if (ideation == null)
            {
                return RedirectToAction("NotFound", "Home");
            }

            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var ideationSubDomain = ideation.Project.Platform.Tenant;
            if (subdomain != ideationSubDomain)
            {
                return RedirectToAction("NotFound", "Home");
            }

            User user = _userManager.GetUserAsync(User).Result;
            if (ideation.IdeationType == IdeationType.Admin)
            {
                bool isAdmin = _userManager.IsUserAdminOrAbove(user,tenant);
                if (!isAdmin)
                {
                    return Unauthorized();
                }
            }

            bool isMod = false;
            if (user != null)
            {
                isMod = _userManager.IsUserAdminOrAbove(user,tenant);

                if (!isMod)
                {
                    isMod = _userManager.IsUserModOrAboveForProject(user, ideation.Project,tenant);
                }
            }

            ViewBag.IsMod = isMod;


            ViewBag.ProjectId = ideation.Project.ProjectId;
            var ideationViewModel = new IdeationViewModel()
            {
                IdeationId = ideation.IdeationId,
                CentralQuestion = ideation.CentralQuestion,
                Description = ideation.Description,
                Url = ideation.Url,
                // UserId = _userManager.GetUserAsync(ideation.User).Result.Id
            };

            return View(ideationViewModel);
        }

        [HttpGet]
        public IActionResult View(int id)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            IdeationReply reply = _ideationManager.GetIdeationReply(id);
            if (reply == null)
            {
                return RedirectToAction("NotFound", "Home");
            }

            Ideation ideation = reply.Ideation;

            User user = _userManager.GetUserAsync(User).Result;
            if (ideation == null)
            {
                return StatusCode(500);
            }

            var subdomain = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var ideationSubDomain = ideation.Project.Platform.Tenant;
            if (subdomain != ideationSubDomain)
            {
                return RedirectToAction("NotFound", "Home");
            }

            Vote vote = null;
            if (user != null)
            {
                vote = reply.Votes.Find(v => v.User == user);
            }

            ViewBag.IsAdminIdeation = ideation.IdeationType == IdeationType.Admin;
            ViewBag.ProjectId = ideation.Project.ProjectId;
            ViewBag.IdeationId = ideation.IdeationId;

            bool isMod = false;
            if (user != null)
            {
                isMod = _userManager.IsUserModOrAboveForProject(user, ideation.Project,tenant);
            }

            ViewBag.IsMod = isMod;
            IdeationReport report = reply.Reports.Find(r => r.User == user && r.ReportedIdeation == reply);
            ViewBag.HasReported = (report != null);

            var vm = new IdeationReplyViewModel()
            {
                IdeationReplyId = reply.IdeationReplyId,
                CentralQuestion = ideation.CentralQuestion,
                DateTime = reply.Created,
                Title = reply.Title,
                Answers = new List<AnswerViewModel>(),
                UserDisplayName = reply.User.GetDisplayName(),
                UserHasVoted = vote != null,
                UpVotes = reply.Upvotes,
                DownVotes = reply.Downvotes,
                IsFlagged = reply.Reports.Any(),
                ReviewedByMod = reply.ReviewedByMod,
                Hidden = reply.Hidden
            };

            if (_userManager.IsUserAdminOrAbove(reply.User,tenant))
            {
                vm.UserDisplayName = reply.User.GetFullName();
            }

            if ((user != null && _userManager.IsUserModOrAbove(user,tenant)) || _userManager.IsUserOrganisation(reply.User))
            {
                //Mods can see the full name
                vm.UserDisplayName = reply.User.GetFullName();
            }

            foreach (Answer answer in reply.Answers)
            {
                Question question = ideation.Questions.Find(q => q.OrderIndex == answer.QuestionIndex);
                AnswerViewModel answervm = new AnswerViewModel()
                {
                    FieldType = question.FieldType,
                    QuestionString = question.QuestionString,
                };
                switch (question.FieldType)
                {
                    case FieldType.OpenText:
                        answervm.OpenAnswer = (string) answer.GetValue();
                        break;
                    case FieldType.Image:
                    case FieldType.Video:
                        answervm.FileAnswer = (Media) answer.GetValue();
                        break;
                    case FieldType.SingleChoice:
                    case FieldType.DropDown:
                        int singleAnswer = (int) answer.GetValue();
                        string singleAnswerAsString = question.Options[singleAnswer];
                        answervm.SingleAnswer = singleAnswerAsString;
                        break;
                    case FieldType.MultipleChoice:
                        List<bool> multiAnswer = (List<bool>) answer.GetValue();
                        List<string> multiAnswerAsStrings = new List<string>();
                        for (int i = 0; i < question.Options.Count; i++)
                        {
                            if (multiAnswer[i])
                            {
                                multiAnswerAsStrings.Add(question.Options[i]);
                            }
                        }

                        answervm.MultipleAnswer = multiAnswerAsStrings;
                        break;
                    case FieldType.Location:
                        answervm.LocationAnswer = (Location) answer.GetValue();
                        break;
                    default:
                        throw new ArgumentOutOfRangeException();
                }

                vm.Answers.Add(answervm);
            }

            return View(vm);
        }
    }
}