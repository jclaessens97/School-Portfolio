using COI.BL.Domain.Foundation;
using System.Collections.Generic;

namespace COI.UI_MVC.Models.dto
{
    public class QuestionDTO
    {
        public FieldType Type { get; set; }
        public string Question { get; set; }
        public List<OptionDTO> Options { get; set; }
        public Location Location { get; set; }
        public int Index { get; set; }
        public bool Required { get; set; }
        public bool LongAnswer { get; set; }
    }

    public class OptionDTO
    {
        public int Index { get; set; }
        public string String { get; set; }
    }
}