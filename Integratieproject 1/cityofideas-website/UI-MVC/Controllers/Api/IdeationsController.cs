using COI.BL;
using COI.BL.Domain.Activity;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using COI.UI_MVC.Attributes;
using COI.UI_MVC.Extensions;
using COI.UI_MVC.Hubs;
using COI.UI_MVC.Hubs.Impl;
using COI.UI_MVC.Models;
using COI.UI_MVC.Models.dto;
using COI.UI_MVC.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore.Internal;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using COI.BL.Domain.Platform;
using COI.BL.Impl;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Controllers.API
{
    [Route("api/[controller]")]
    [ApiController]
    public class IdeationsController : ControllerBase
    {
        private readonly IIdeationManager _ideationManager;
        private readonly IFileUploader _fileUploader;
        private readonly IActivityManager _activityManager;
        private readonly IPlatformManager _platformManager;
        private readonly UnitOfWorkManager _unitOfWorkManager;
        private readonly UserManager<User> _usermanager;
        private readonly IHubContext<ActivityHub, IActivityHub> _activityHubContext;
        private readonly IHubContext<VoteHub, IVoteHub> _voteHubContext;

        public IdeationsController(
            [FromServices] IIdeationManager ideationManager,
            [FromServices] IFileUploader imageUploader,
            [FromServices] IPlatformManager platformManager,
            [FromServices] IActivityManager activityManager,
            [FromServices] UnitOfWorkManager unitOfWorkManager,
            [FromServices] IHubContext<VoteHub, IVoteHub>  voteHubContext,
            UserManager<User> userManager,
            IHubContext<ActivityHub, IActivityHub> activityHubContext
        )
        {
            _ideationManager = ideationManager;
            _fileUploader = imageUploader;
            _platformManager = platformManager;
            _activityManager = activityManager;
            _unitOfWorkManager = unitOfWorkManager;
            _usermanager = userManager;
            _activityHubContext = activityHubContext;
            _voteHubContext = voteHubContext;
        }

        #region Ideations
        // GET: /api/ideations/1
        [HttpGet("{ideationId}", Name = "GetIdeation")]
        public IActionResult GetIdeation(int ideationId)
        {
            Ideation ideation = _ideationManager.GetIdeationWithQuestions(ideationId);

            if (ideation == null)
            {
                return NoContent();
            }

            return Ok(ideation);
        }

        // GET: /api/ideations/all
        [HttpGet("all")]
        public IActionResult GetIdeations()
        {
            var ideations = _ideationManager.GetIdeations();
            
            var subdomain = GetSubdomain(HttpContext.Request.Host.ToString());
            if (subdomain != null)
            {
                ideations = ideations.Where(p => p.Project.Platform.Tenant == subdomain).AsEnumerable();
            }
            
            if (ideations == null || !ideations.Any())
            {
                return NotFound("er zijn geen ideations teruggevonden");
            }

            return Ok(ideations);
        }

        [HttpGet("all/admin/{id}")]
        public IActionResult GetAllAdminIdeations(int id, [FromServices] IIoTManager ioTManager)
        {
            List<Ideation> ideations = _ideationManager.GetAllAdminIdeations(id).ToList();
            
            List<IdeationViewModel> ideationViewModels = new List<IdeationViewModel>();
            

            foreach (var ideation in ideations)
            {
                bool hasReplies = ideation.Replies.Any();
                if (!hasReplies)
                {
                    continue;
                }
                IoTDTO iotLink = null;
                if (hasReplies)
                {
                    IotLink link = null;
                    link = ioTManager.GetIoTLinkByIdeationReply(ideation.Replies[0]);
                    if (link != null)
                    {
                        iotLink = new IoTDTO()
                        {
                            IotLinkId = link.IotLinkId,
                            IsForm = false,
                            IdeationId = ideation.Replies[0].IdeationReplyId,
                            Location = new LocationDTO()
                            {
                                Longitude = link.Location.Longitude,
                                Latitude = link.Location.Latitude,
                                ZoomLevel = link.Location.ZoomLevel
                            }
                        };
                    }
                }
                IdeationViewModel vm = new IdeationViewModel()
                {
                    CentralQuestion = ideation.CentralQuestion,
                    Description = ideation.Description,
                    IdeationId = ideation.IdeationId,
                    HasReplies = hasReplies,
                    IotLink = iotLink 
                };
                ideationViewModels.Add(vm);
            }
            
            return Ok(ideationViewModels);
        }

        // POST: /api/ideations
        [HttpPost]
        public IActionResult PostIdeation(IdeationDTO ideationDto,[FromServices] IProjectManager projectManager)
        {
            Project project = projectManager.GetProject(ideationDto.ProjectId);
            Phase phase = project.Phases.FirstOrDefault(p => p.PhaseId == ideationDto.PhaseId);
            Ideation newIdeation = new Ideation()
            {
                CentralQuestion = ideationDto.CentralQuestion,
                Description = ideationDto.Description,
                Url = ideationDto.Url,
                Questions = new List<Question>(),
                Replies = new List<IdeationReply>(),
                IdeationType = ideationDto.IdeationType,
                Project = project,
                Phase = phase
            };
            int index = 0;
            foreach (QuestionDTO question in ideationDto.Questions)
            {
                Question newQuestion = new Question()
                {
                    FieldType = question.Type,
                    Options = new List<string>(),
                    QuestionString = question.Question,
                    Required = question.Required,
                    LongAnswer = question.LongAnswer,
                    OrderIndex = index,
                    Location = question.Location
                };
                foreach (var option in question.Options)
                {
                    newQuestion.Options.Add(option.String);
                }

                newIdeation.Questions.Add(newQuestion);
                index++;
            }

            _ideationManager.AddIdeation(newIdeation);
            _unitOfWorkManager.Save();

            return Created("/ideation/reply/" + newIdeation.IdeationId, newIdeation);
        }
        #endregion

        #region Ideation Replies
        // GET: /api/ideations/replies/1
        [HttpGet("replies/{ideationId}")]
        public IActionResult GetReplies(int ideationId, [FromQuery(Name = "sortBy")] string sortBy = "recent")
        {
            List<IdeationReply> replies = _ideationManager.GetIdeationReplies(ideationId,0,999, sortBy).ToList();

            if (!replies.Any())
            {
                return Ok();
            }

            var replyDtos = new List<IdeationReplyDTO>();
            replies.ForEach(r =>
            {
                var replyDto = new IdeationReplyDTO()
                {
                    IdeationReplyId = r.IdeationReplyId,
                    IdeationId = r.Ideation.IdeationId,
                    UpVotes = r.Upvotes,
                    NumberOfComments = _ideationManager.CommentAmount(r.IdeationReplyId),
                    CreatedString = r.Created.FormatParasableDate(),
                    Title = r.Title,
                    UserDisplayName = r.User.GetDisplayName()
                    
                };

                replyDtos.Add(replyDto);
            });

            return Ok(replyDtos);
        }
        
        [HttpGet("replies/{ideationId}/{skip}/{take}")]
        public IActionResult GetReplies(int ideationId,int skip, int take, [FromQuery(Name = "sortBy")] string sortBy = "recent")
        {
            List<IdeationReply> replies;
            int replyAmount;
            if (sortBy == "reported")
            {
                replies = _ideationManager.GetReportedIdeationReplies(ideationId, skip, take).ToList();
                replyAmount = _ideationManager.GetReportedIdeationReplyCountByIdeation(ideationId);
            }
            else
            {
                replies = _ideationManager.GetIdeationReplies(ideationId, skip, take, sortBy).ToList();
                replyAmount = _ideationManager.GetIdeationReplyCountByIdeation(ideationId);
            }
            

            if (!replies.Any())
            {
                return Ok();
            }

            var replyDtos = new List<IdeationReplyDTO>();
            replies.ForEach(r =>
            {
                var replyDto = new IdeationReplyDTO()
                {
                    IdeationReplyId = r.IdeationReplyId,
                    IdeationId = r.Ideation.IdeationId,
                    UpVotes = r.Upvotes,
                    NumberOfComments = _ideationManager.CommentAmount(r.IdeationReplyId),
                    CreatedString = r.Created.FormatParasableDate(),
                    Title = r.Title,
                    UserDisplayName = r.User.GetDisplayName()
                };
                if (sortBy == "reported")
                {
                    replyDto.UserDisplayName = r.User.GetFullName();
                    replyDto.ReportCount = r.Reports.Count;
                }

                replyDtos.Add(replyDto);
            });

            return Ok(new { count = replyAmount,replies = replyDtos});
        }
        
        [HttpPost("replyApp")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public IActionResult PostIdeationReplyApp([FromBody] IdeationReplyAppDto ideationReplyApp)
        {
            Ideation ideation = _ideationManager.GetIdeationWithReplies(ideationReplyApp.IdeationId);

            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            
            IdeationReply newReply = new IdeationReply()
            {
                Ideation = ideation,
                Title = ideationReplyApp.Title,
                Answers = new List<Answer>(),
                Votes = new List<Vote>(),
                Created = DateTime.Now,
                Comments = new List<Comment>(),
                User = user,
                Reports = new List<IdeationReport>()
            };
            
            int index = 0;
            foreach (var dto in ideationReplyApp.Answers)
            {
                Answer newAnswer = null;

                switch (dto.FieldType)
                {
                    case FieldType.OpenText:
                        newAnswer = new OpenTextAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            Value = dto.Reply
                        };
                        break;
                    case FieldType.SingleChoice:
                    case FieldType.DropDown:
                        newAnswer = new SingleChoiceAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            SelectedChoice = dto.SelectedChoice
                        };
                        break;
                    case FieldType.MultipleChoice:
                        newAnswer = new MultipleChoiceAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            SelectedChoices = dto.MultipleAnswer
                        };
                        break;
                    case FieldType.Location:
                        newAnswer = new LocationAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            Value = new Location()
                            {
                                Latitude = dto.LocationAnswer.Latitude,
                                Longitude = dto.LocationAnswer.Longitude,
                                ZoomLevel = dto.LocationAnswer.ZoomLevel,
                            }
                        };
                        break;
                    default:
                        throw new ArgumentOutOfRangeException();
                }

                newAnswer.OrderIndex = index++;
                newReply.Answers.Add(newAnswer);
            }
            
            // Create activity
            var activity = CreateActivity(ActivityType.IdeationReply, user,ideation.Project.Platform);
            activity.IdeationReply = newReply;
            _activityManager.AddActivity(activity);

            // Save everything
            _unitOfWorkManager.Save();

            // Push activity
            var activityVm = new ActivityViewModel(activity);
            PushWebsockets(activityVm).Wait();

            return Ok();
        }

        [HttpGet("reply/{id}")]
        public IActionResult ViewIdeationReply(int id)
        {
            IdeationReply reply = _ideationManager.GetIdeationReply(id);
            Ideation ideation = reply.Ideation;

            if (reply == null)
            {
                return NotFound();
            }

            if (ideation == null)
            {
                return StatusCode(500);
            }

            var vm = new IdeationReplyViewModel()
            {
                IdeationReplyId = reply.IdeationReplyId,
                CentralQuestion = ideation.CentralQuestion,
                DateTime = reply.Created,
                Answers = new List<AnswerViewModel>(),
                UserDisplayName = reply.User.GetDisplayName(),
                UpVotes = reply.Upvotes,
                Title = reply.Title,
                DownVotes = reply.Downvotes,
            };

            foreach (Answer answer in reply.Answers)
            {
                Question question = ideation.Questions.Find(q => q.OrderIndex == answer.QuestionIndex);
                if (question != null)
                {


                    AnswerViewModel answervm = new AnswerViewModel
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
            }
            

            return Ok(vm);
        }

        // POST: /api/ideations/reply
        [HttpPost("reply")]
        public IActionResult PostIdeationReply([FromForm] IdeationReplyDTO ideationReplyDto)
        {
           // return Created("/ideation/overview/1", null);
            //log test 1
            Ideation ideation = _ideationManager.GetIdeationWithQuestions(ideationReplyDto.IdeationId);
            //log test 2
            User user = _usermanager.GetUserAsync(User).Result;
            //log test 3
            if (ideation == null || user == null)
            {
                return NotFound();
            }
            //log test 4
            
            IdeationReply newReply = new IdeationReply()
            {
                Ideation = ideation,
                Title = ideationReplyDto.Title,
                Answers = new List<Answer>(),
                Votes = new List<Vote>(),
                Comments = new List<Comment>(),
                Created = DateTime.Now,
                User = user,
                Reports = new List<IdeationReport>()
            };
            //log test 5
            int index = 0;
            ideationReplyDto.Answers.ForEach(dto =>
            {
                Answer newAnswer = null;

                switch (dto.FieldType)
                {
                    case FieldType.OpenText:
                        newAnswer = new OpenTextAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            Value = dto.OpenAnswer
                        };
                        break;
                    case FieldType.Image:
                    case FieldType.Video:
                        string fileName = Util.Util.GenerateDataStoreObjectName(dto.FileAnswer.FileName);
                        string pathName = _fileUploader.UploadFile(fileName, "ideationReply", dto.FileAnswer).Result;
                        newAnswer = new MediaAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            Value = new Media()
                            {
                                Name = dto.FileAnswer.FileName,
                                Url = pathName
                            }
                        };
                        break;
                    case FieldType.SingleChoice:
                    case FieldType.DropDown:
                        newAnswer = new SingleChoiceAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            SelectedChoice = dto.SingleAnswer
                        };
                        break;
                    case FieldType.MultipleChoice:
                        newAnswer = new MultipleChoiceAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            SelectedChoices = dto.MultipleAnswer
                        };
                        break;
                    case FieldType.Location:
                        newAnswer = new LocationAnswer()
                        {
                            QuestionIndex = dto.QuestionIndex,
                            Value = new Location()
                            {
                                Latitude = dto.LocationAnswer.Latitude,
                                Longitude = dto.LocationAnswer.Longitude,
                                ZoomLevel = dto.LocationAnswer.ZoomLevel,
                            }
                        };
                        break;
                    default:
                        throw new ArgumentOutOfRangeException();
                }

                newAnswer.OrderIndex = index++;
                newReply.Answers.Add(newAnswer);
            });
            //log test 6
            _ideationManager.AddIdeationReply(newReply);
            //log test 7
            // Create activity
            var activity = CreateActivity(ActivityType.IdeationReply, user);
            activity.IdeationReply = newReply;
            _activityManager.AddActivity(activity);
            //log test 8
            // Save everything
            _unitOfWorkManager.Save();
            //log test 9
            // Push activity
            var activityVm = new ActivityViewModel(activity);
            //log test 10
            PushWebsockets(activityVm).Wait();
            //log test 11
            return Created("/ideation/view/" + newReply.IdeationReplyId, new { id= newReply.IdeationReplyId});

        }

        [HttpPost("reply/report/{id}")]
        public IActionResult ReportIdeation(int id)
        {
            User user = _usermanager.GetUserAsync(User).Result;
            _ideationManager.ReportIdeation(id,user);
            _unitOfWorkManager.Save();
            return Ok();
        }
        
        [HttpPost("reply/report/cancel/{id}")]
        public IActionResult UnReportIdeation(int id)
        {
            User user = _usermanager.GetUserAsync(User).Result;
            _ideationManager.CancelReportIdeation(id,user);
            _unitOfWorkManager.Save();
            return Ok();
        }

        //[OnlyModeratorAndAbove]
        [HttpPost("reply/approve/{id}")]
        public IActionResult ApprovePost(int id)
        {
            _ideationManager.ApprovePost(id);
            _unitOfWorkManager.Save();
            return Ok();
        }
        
        //[OnlyModeratorAndAbove]
        [HttpPost("reply/disapprove/{id}")]
        public IActionResult DisapprovePost(int id)
        {
            _ideationManager.DisapprovePost(id);
            _unitOfWorkManager.Save();
            return Ok();
        }
        
        #endregion

        #region Ideation Questions
        // GET: /api/ideations/questions/1
        [HttpGet("questions/{ideationId}")]
        public IActionResult GetQuestions(int ideationId)
        {
            IEnumerable<Question> questions = _ideationManager.GetIdeationQuestions(ideationId);

            if (!questions.Any())
            {
                return NoContent();
            }

            return Ok(questions);
        }
        #endregion

        #region Comments
        // GET: /api/ideations/comments/1/5/10
        [HttpGet("comments/{id}/{skip}/{take}")]
        public IActionResult GetComments(int id, int skip, int take)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            User user = _usermanager.GetUserAsync(User).Result;
            IEnumerable<Comment> comments = _ideationManager.GetComments(id, skip, take);
            List<CommentDTO> commentDtos = new List<CommentDTO>();
            foreach (Comment comment in comments)
            {
                Report report = null;

                if (user != null)
                {
                    report = comment.Reports.Find(r => r.User.Id == user.Id);
                }

                CommentDTO commentDto = new CommentDTO()
                {
                    CommentId = comment.CommentId,
                    CommentText = comment.CommentText,
                    DateTime = comment.Created.FormatParasableDate(),
                    UserDisplayName = comment.User.GetDisplayName(),
                    UserName = comment.User.UserName,
                    ReportedByMe = (report != null)
                 
                };
                
                if (_usermanager.IsUserAdminOrAbove(comment.User,tenant))
                {
                    commentDto.UserDisplayName = comment.User.GetFullName();
                }

                if ((user != null && _usermanager.IsUserModOrAbove(user,tenant)) || _usermanager.IsUserOrganisation(comment.User))
                { //Mods can see the full name
                    commentDto.UserFullName = comment.User.GetFullName();
                    commentDto.UserDisplayName = comment.User.GetFullName();
                }

                commentDtos.Add(commentDto);
            }

            return Ok(commentDtos);
        }

        // POST: /api/ideations/comments
        [HttpPost("comments")]
        public IActionResult PostComment(CommentDTO commentDto)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            IdeationReply ideationReply = _ideationManager.GetIdeationReply(commentDto.IdeationReplyId);
            
            User user = _usermanager.GetUserAsync(User).Result;
            Comment comment = _ideationManager.AddComment(commentDto.CommentText, user, ideationReply);

            // Create activity
            var activity = CreateActivity(ActivityType.Comment, user);
            activity.Comment = comment;
            _activityManager.AddActivity(activity);

            // Save everything
            _unitOfWorkManager.Save();

            // Push activity
            var activityVm = new ActivityViewModel(activity);
            PushWebsockets(activityVm).Wait();

            //Fill in data to send back as response
            commentDto.CommentId = comment.CommentId;
            commentDto.DateTime = comment.Created.FormatParasableDate();
            commentDto.UserDisplayName = comment.User.GetDisplayName();
            commentDto.UserName = comment.User.UserName;

            if (user != null && _usermanager.IsUserModOrAbove(user,tenant))
            {
                //Mods can see the full name
                commentDto.UserFullName = comment.User.GetFullName();
            }
            
            return Ok(commentDto);
        }
        

        // DELETE: /api/ideations/comments/1
        [HttpDelete("comments/{id}")]
        public IActionResult DeleteComment(int id)
        {
            _ideationManager.RemoveComment(id);
            _unitOfWorkManager.Save();
            return Ok();
        }

        // GET: /api/ideations/flaggedcomments/5/10
        [HttpGet("flaggedcomments/{id}/{skip}/{take}")]
        public IActionResult GetFlaggedComments(int id,int skip, int take)
        {
            IEnumerable<Comment> flaggedComments = _ideationManager.GetFlaggedComments(id,skip, take);
            List<CommentDTO> comments = new List<CommentDTO>();
            foreach (Comment comment in flaggedComments)
            {
                CommentDTO commentDto = new CommentDTO()
                {
                    CommentId = comment.CommentId,
                    CommentText = comment.CommentText,
                    DateTime = comment.Created.FormatParasableDate(),
                    UserFullName = comment.User.GetFullName(),
                    UserName = comment.User.UserName,
                    Reports = new List<ReportDTO>()
                };
                foreach (Report report in comment.Reports)
                {
                    ReportDTO newReport = new ReportDTO()
                    {
                        Reason = report.Reason,
                        ReportId = report.ReportId,
                        UserFullName = report.User.GetFullName()
                    };
                    commentDto.Reports.Add(newReport);
                }
                comments.Add(commentDto);
            }

            return Ok(comments);
        }

        // POST: /api/ideations/reports
        [HttpPost("Reports")]
        public IActionResult ReportComment(ReportDTO reportDto)
        {
            Comment comment = _ideationManager.GetComment(reportDto.CommentId);
            User user = _usermanager.GetUserAsync(User).Result;
            Report FoundReport = _ideationManager.FindReport(user, comment);
            if (FoundReport != null)
            {
                return Ok();
            }
            Report report = new Report()
            {
                Reason = reportDto.Reason,
                ReportedComment = comment,
                User = user
            };
            _ideationManager.ReportComment(report);
            _unitOfWorkManager.Save();

            return Ok(report.ReportId);
        }

        [HttpDelete("reports/remove/{id}")]
        public IActionResult UnReportComment(int id)
        {
            _ideationManager.RemoveReport(id);
            _unitOfWorkManager.Save();
            return Ok();
        }

        // POST: /api/ideations/comments/hide/1
       // [OnlyModeratorAndAbove]
        [HttpPost("comments/hide/{commentId}")]
        public IActionResult HideComment(int commentId)
        {
            _ideationManager.HideComment(commentId);
            _unitOfWorkManager.Save();
            return Ok();
        }
        
       // [OnlyModeratorAndAbove]
        [HttpPost("comments/allow/{commentId}")]
        public IActionResult AllowComment(int commentId)
        {
            _ideationManager.AllowComment(commentId);
            _unitOfWorkManager.Save();
            return Ok();
        }

        // POST: /api/ideations/comments/unhide/1
        [OnlyModeratorAndAbove]
        [HttpPost("comments/unhide/{commentId}")]
        public IActionResult UnHideComment(int commentId)
        {
            _ideationManager.UnHideComment(commentId);
            _unitOfWorkManager.Save();
            return Ok();
        }
        #endregion

        #region Votes
        // POST: /api/ideations/reply/vote/up/1
        [HttpPost("reply/vote/up/{replyId}")]
        public void VoteUp(int replyId)
        {
            User user = _usermanager.GetUserAsync(User).Result;
            var vote = _ideationManager.VoteReplyUp(replyId, user);
            IdeationReply reply = _ideationManager.GetIdeationReply(replyId);
            
            
            if (vote != null)
            {
               // _voteHubContext.Clients.Group($"ideationReply - {replyId}").ReceiveUpvote();
                
                var activity = CreateActivity(ActivityType.IdeationVote, user,reply.Ideation.Project.Platform);
                activity.Vote = vote;
                _activityManager.AddActivity(activity);
                _unitOfWorkManager.Save();

                var activityVm = new ActivityViewModel(activity);
                PushWebsockets(activityVm).Wait();
            }
        }

        // POST: /api/ideations/reply/vote/down/1
        [HttpPost("reply/vote/down/{replyId}")]
        public void VoteDown(int replyId)
        {
            User user = _usermanager.GetUserAsync(User).Result;
            var vote = _ideationManager.VoteReplyDown(replyId, user);
            IdeationReply reply = _ideationManager.GetIdeationReply(replyId);

            if (vote != null)
            {
                //_voteHubContext.Clients.Group($"ideationReply - {replyId}").ReceiveDownvote();
                var activity = CreateActivity(ActivityType.IdeationVote, user,reply.Ideation.Project.Platform);
                activity.Vote = vote;
                _activityManager.AddActivity(activity);

                _unitOfWorkManager.Save();

                var activityVm = new ActivityViewModel(activity);
                PushWebsockets(activityVm).Wait();
            }
        }
        #endregion

        #region App
        [HttpPost("App/Reports")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public IActionResult ReportCommentApp(ReportDTO reportDto)
        {
            Comment comment = _ideationManager.GetComment(reportDto.CommentId);
            // dit moet verandert worden
            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            Report FoundReport = _ideationManager.FindReport(user, comment);
            if (FoundReport != null)
            {
                return Ok();
            }
            Report report = new Report()
            {
                Reason = reportDto.Reason,
                ReportedComment = comment,
                User = user
            };
            _ideationManager.ReportComment(report);
            _unitOfWorkManager.Save();

            return Ok(report.ReportId);
        }
        
        [HttpPost("app/comments")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)] 
        public IActionResult PostCommentApp(CommentDTO commentDto)
        {
            //var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            IdeationReply ideationReply = _ideationManager.GetIdeationReply(commentDto.IdeationReplyId);
            
            
            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            Comment comment = _ideationManager.AddComment(commentDto.CommentText, user, ideationReply);

            // Create activity
            var activity = CreateActivity(ActivityType.Comment, user,ideationReply.Ideation.Project.Platform);
            activity.Comment = comment;
            _activityManager.AddActivity(activity);

            // Save everything
            _unitOfWorkManager.Save();

            // Push activity
            var activityVm = new ActivityViewModel(activity);
            PushWebsockets(activityVm).Wait();

            //Fill in data to send back as response
            commentDto.CommentId = comment.CommentId;
            commentDto.DateTime = comment.Created.FormatDateTime();
            commentDto.UserDisplayName = comment.User.GetDisplayName();
            commentDto.UserName = comment.User.UserName;

            if (user != null && _usermanager.IsUserModOrAbove(user,ideationReply.Ideation.Project.Platform.Tenant))
            {
                //Mods can see the full name
                commentDto.UserFullName = comment.User.GetFullName();
            }
            
            return Ok(commentDto);
        }
        
        // POST: /api/ideations/reply/vote/up/1
        [HttpPost("App/reply/vote/up/{replyId}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public void VoteUpApp(int replyId)
        {
            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            
            var vote = _ideationManager.VoteReplyUp(replyId, user);
            IdeationReply reply = _ideationManager.GetIdeationReply(replyId);

            if (vote != null)
            {
                //_voteHubContext.Clients.Group($"ideationReply - {replyId}").ReceiveDownvote();
                var activity = CreateActivity(ActivityType.IdeationVote, user,reply.Ideation.Project.Platform);
                activity.Vote = vote;
                _activityManager.AddActivity(activity);

                _unitOfWorkManager.Save();

                var activityVm = new ActivityViewModel(activity);
                PushWebsockets(activityVm).Wait();
            }
            
           
        }
        
        [HttpPost("app/reply/vote/down/{replyId}")]
        public void VoteDownApp(int replyId)
        {
            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            
            var vote = _ideationManager.VoteReplyDown(replyId, user);
            IdeationReply reply = _ideationManager.GetIdeationReply(replyId);

            if (vote != null)
            {
                //_voteHubContext.Clients.Group($"ideationReply - {replyId}").ReceiveDownvote();
                var activity = CreateActivity(ActivityType.IdeationVote, user,reply.Ideation.Project.Platform);
                activity.Vote = vote;
                _activityManager.AddActivity(activity);

                _unitOfWorkManager.Save();

                var activityVm = new ActivityViewModel(activity);
                PushWebsockets(activityVm).Wait();
            }
           
        }
        
        [HttpPost("app/reply/report/{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public IActionResult ReportIdeationApp(int id)
        {
            User user = _usermanager.FindByEmailAsync(HttpContext.User.Claims.FirstOrDefault(c => c.Type == "Email").Value).Result;
            _ideationManager.ReportIdeation(id,user);
            _unitOfWorkManager.Save();
            return Ok();
        }
        
        [HttpGet("app/comments/{id}/{skip}/{take}")]
        public IActionResult GetCommentsApp(int id, int skip, int take)
        {
           // var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            User user = _usermanager.GetUserAsync(User).Result;
            IEnumerable<Comment> comments = _ideationManager.GetComments(id, skip, take);
            List<CommentDTO> commentDtos = new List<CommentDTO>();
            foreach (Comment comment in comments)
            {
                Report report = null;

                if (user != null)
                {
                    report = comment.Reports.Find(r => r.User.Id == user.Id);
                }

                CommentDTO commentDto = new CommentDTO()
                {
                    CommentId = comment.CommentId,
                    CommentText = comment.CommentText,
                    DateTime = comment.Created.FormatDateTime(),
                    UserDisplayName = comment.User.GetDisplayName(),
                    UserName = comment.User.UserName,
                    ReportedByMe = (report != null)
                 
                };

                commentDtos.Add(commentDto);
            }

            return Ok(commentDtos);
        }
        
        #endregion

        #region Helpers
        private Activity CreateActivity(ActivityType activityType, User user)
        {
            var tenant = Util.Util.GetSubdomain(HttpContext.Request.Host.ToString());
            var platform = _platformManager.GetPlatformByTenant(tenant);
            return CreateActivity(activityType, user, platform);
        }
        
        private Activity CreateActivity(ActivityType activityType, User user,Platform platform)
        {
            return new Activity()
            {
                ActivityType = activityType,
                ActivityTime = DateTime.Now,
                User = user,
                Platform = platform
            };
        }

        private Task PushWebsockets(ActivityViewModel activityVm)
        {
            return Task.WhenAll(
                _activityHubContext.Clients.Group($"activity - {activityVm.PlatformTenant}").UpdateActivityFeed(activityVm),
                _activityHubContext.Clients.Group($"activity - all").UpdateActivityFeed(activityVm)
            );
        }
        #endregion
    }
}