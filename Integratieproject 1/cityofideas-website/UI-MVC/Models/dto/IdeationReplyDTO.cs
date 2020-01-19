using COI.BL.Domain.Foundation;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class IdeationReplyDTO
    {
        public int IdeationReplyId { get; set; }
        public int IdeationId { get; set; }
        public string Title { get; set; }
        public List<IdeationAnswerDTO> Answers { get; set; }
        public int UpVotes { get; set; }
        public int NumberOfComments { get; set; }
        public int ReportCount { get; set; }
        public DateTime Created { get; set; }
        public string CreatedString { get; set; }
        public List<CommentDTO> Comments { get; set; }
        public string UserDisplayName { get; set; }
    }

    public class IdeationAnswerDTO
    {
        public int QuestionIndex { get; set; }
        public FieldType FieldType { get; set; }
        public string OpenAnswer { get; set; }
        public int SingleAnswer { get; set; }
        public List<bool> MultipleAnswer { get; set; }
        public LocationDTO LocationAnswer { get; set; }
        public IFormFile FileAnswer { get; set; }
    }

    public class LocationDTO
    {
        public double Latitude { get; set; }
        public double Longitude  { get; set; }
        public double ZoomLevel { get; set; }
    }
}