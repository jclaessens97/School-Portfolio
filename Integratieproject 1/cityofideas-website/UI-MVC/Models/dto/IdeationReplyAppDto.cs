using COI.BL.Domain.Foundation;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class IdeationReplyAppDto
    {
        public int IdeationId { get; set; }
        public string Title { get; set; }
        public List<AnswerAppDto> Answers { get; set; }
    }

    public class AnswerAppDto
    {
        public int QuestionIndex { get; set; }
        public FieldType FieldType { get; set; }
        public string Reply { get; set; }
        public int SelectedChoice { get; set; }
        public List<bool> MultipleAnswer { get; set; }
        public LocationDTO LocationAnswer { get; set; }
        public Media FileAnswer { get; set; }
        
        public class LocationDTO
        {
            public double Latitude { get; set; }
            public double Longitude  { get; set; }
            public double ZoomLevel { get; set; }
        }
    }
}