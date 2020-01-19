using COI.BL;
using COI.BL.Application;
using COI.BL.Domain.Form;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.User;
using COI.UI_MVC.Models.dto;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
using COI.BL.Domain.Answer;
using Location = COI.BL.Domain.Foundation.Location;

namespace COI.UI_MVC.Controllers.Api
{
    [ApiController]
    [Route("api/iot")]
    public class VotesController : Controller
    {
        private readonly IVoteController _voteController;
        private readonly UserManager<User> _userManager;

        public VotesController(UserManager<User> userManager
            ,[FromServices] IVoteController voteController)
        {
            _userManager = userManager;
            _voteController = voteController;
        }

        [HttpPost("VoteUp/{id}")]
        public IActionResult VoteUp(int id)
        {
            User user = _userManager.FindByNameAsync("Cronos_Benelux").Result;
            _voteController.VoteUp(id,user);
            return Ok();
        }
        
        [HttpPost("VoteDown/{id}")]
        public IActionResult VoteDown(int id)
        {
            _voteController.VoteDown(id);
            return Ok();
        }

        [HttpGet("project/all/{projectId}")]
        public IActionResult GetIoTLinksByProject(int projectId,[FromServices] IIoTManager ioTManager)
        {
            List<IotLink> iotLinks = ioTManager.GetIotLinksByProject(projectId).ToList();
            List<IoTDTO> iots = new List<IoTDTO>();

            foreach (var iotLink in iotLinks)
            {
                IoTDTO ioT = new IoTDTO()
                {
                    Location = new LocationDTO()
                    {
                        Latitude = iotLink.Location.Latitude,
                        Longitude = iotLink.Location.Longitude,
                        ZoomLevel = iotLink.Location.ZoomLevel
                    },
                    IsForm = iotLink.Form != null,
                };
                if (iotLink.Form != null)
                {
                    ioT.Question = iotLink.Form.Questions[0].QuestionString;
                    int upvotes = iotLink.Form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 1);
                    int downvotes = iotLink.Form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 0);
                    ioT.UpVotes = upvotes;
                    ioT.DownVotes = downvotes;
                    ioT.FormId = iotLink.Form.FormId;
                }
                else
                {
                    ioT.Question = iotLink.IdeationReply.Ideation.CentralQuestion;
                    ioT.UpVotes = iotLink.IdeationReply.Upvotes;
                    ioT.DownVotes = iotLink.IdeationReply.Downvotes;
                    ioT.IdeationId = iotLink.IdeationReply.IdeationReplyId;
                }
                
                iots.Add(ioT);
            }
            
            return Ok(iots);
        }
        
        [HttpGet("platform/all/{platformId}")]
        public IActionResult GetIoTLinksByPlatform(int platformId,[FromServices] IIoTManager ioTManager)
        {
            List<IotLink> iotLinks = ioTManager.GetIotLinksByPlatform(platformId).ToList();
            List<IoTDTO> iots = new List<IoTDTO>();

            foreach (var iotLink in iotLinks)
            {
                IoTDTO ioT = new IoTDTO()
                {
                    Location = new LocationDTO()
                    {
                        Latitude = iotLink.Location.Latitude,
                        Longitude = iotLink.Location.Longitude,
                        ZoomLevel = iotLink.Location.ZoomLevel
                    },
                    IsForm = iotLink.Form != null,
                };
                if (iotLink.Form != null)
                {
                    ioT.Question = iotLink.Form.Questions[0].QuestionString;
                    int upvotes = iotLink.Form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 1);
                    int downvotes = iotLink.Form.Replies.Select(f => ((SingleChoiceAnswer) f.Answers[0]).SelectedChoice).Count(c => c == 0);
                    ioT.UpVotes = upvotes;
                    ioT.DownVotes = downvotes;
                    ioT.FormId = iotLink.Form.FormId;
                }
                else
                {
                    ioT.Question = iotLink.IdeationReply.Ideation.CentralQuestion;
                    ioT.UpVotes = iotLink.IdeationReply.Upvotes;
                    ioT.DownVotes = iotLink.IdeationReply.Downvotes;
                    ioT.IdeationId = iotLink.IdeationReply.IdeationReplyId;
                }
                
                iots.Add(ioT);
            }
            
            return Ok(iots);
        }


        [HttpPost]
        public IActionResult CreateLink(IoTDTO iot,[FromServices] IFormManager formManager,[FromServices] IIdeationManager ideationManager,[FromServices] IIoTManager ioTManager,[FromServices]  UnitOfWorkManager unitOfWorkManager)
        {
            IotLink link = null;
            Location location = new Location()
            {
                Longitude = iot.Location.Longitude,
                Latitude = iot.Location.Latitude,
                ZoomLevel = iot.Location.ZoomLevel
            };
            
            
            if (iot.IsForm)
            {
                Form form = formManager.GetForm(iot.FormId);
                if (form == null)
                {
                    return NotFound();
                }
                link = ioTManager.CreateIotLink(form, null,form.Project,location);
            }
            
            else
            {
                Ideation ideation = ideationManager.GetIdeationWithReplies(iot.IdeationId);
                IdeationReply reply = ideationManager.GetIdeationReply(ideation.Replies[0].IdeationReplyId);
                if (reply == null)
                {
                    return NotFound();
                }
                link = ioTManager.CreateIotLink(null, reply,ideation.Project,location);
            }
            unitOfWorkManager.Save();

            return Created("", new {id = link.IotLinkId});

        }

        [HttpPut]
        public IActionResult EditLink(IoTDTO iot,[FromServices] IFormManager formManager,[FromServices] IIdeationManager ideationManager,[FromServices] IIoTManager ioTManager,[FromServices]  UnitOfWorkManager unitOfWorkManager)
        {
            IotLink link = ioTManager.GetIoTLink(iot.IotLinkId);
            
            Location location = new Location()
            {
                Longitude = iot.Location.Longitude,
                Latitude = iot.Location.Latitude,
                ZoomLevel = iot.Location.ZoomLevel
            };
            link.Location = location;
            
            ioTManager.UpdateIotLink(link);
            unitOfWorkManager.Save();

            return Ok();

        }
        
        [HttpDelete("{id}")]
        public IActionResult RemoveLinkt(int id,[FromServices] IIoTManager ioTManager,[FromServices]  UnitOfWorkManager unitOfWorkManager)
        {
            ioTManager.DeleteLink(id);
            unitOfWorkManager.Save();
            return Ok();
        }
    }
}